import logging
import re
from typing import Optional, Union

import astroid
from astroid.context import InferenceContext
from astroid.helpers import safe_infer
from package_parser.processing.api.model import (
    API,
    Class,
    FromImport,
    Function,
    Import,
    Module,
)
from package_parser.utils import parent_qualified_name

from ._file_filters import _is_init_file
from ._get_parameter_list import get_parameter_list
from .docstring_parsing import AbstractDocstringParser


class _AstVisitor:
    def __init__(
        self, docstring_parser: AbstractDocstringParser, api: API
    ) -> None:
        self.docstring_parser: AbstractDocstringParser = docstring_parser
        self.reexported: dict[str, list[str]] = {}
        self.api: API = api
        self.__declaration_stack: list[Union[Module, Class, Function]] = []

    def __get_id(self, name: str) -> str:
        segments = [self.api.package]
        segments += [it.name for it in self.__declaration_stack]
        segments += [name]

        return "/".join(segments)

    def __get_function_id(self, name: str, decorators: list[str]) -> str:
        def is_getter() -> bool:
            return "property" in decorators

        def is_setter() -> bool:
            for decorator in decorators:
                if re.search(r"^[^.]*.setter$", decorator):
                    return True

            return False

        def is_deleter() -> bool:
            for decorator in decorators:
                if re.search(r"^[^.]*.deleter$", decorator):
                    return True

            return False

        result = self.__get_id(name)

        if is_getter():
            result += "@getter"
        elif is_setter():
            result += "@setter"
        elif is_deleter():
            result += "@deleter"

        return result

    def enter_module(self, module_node: astroid.Module):
        imports: list[Import] = []
        from_imports: list[FromImport] = []
        visited_global_nodes: set[astroid.NodeNG] = set()
        id_ = f"{self.api.package}/{module_node.qname()}"

        for _, global_node_list in module_node.globals.items():
            global_node = global_node_list[0]

            # For some reason from-imports get visited as often as there are imported names, leading to duplicates
            if global_node in visited_global_nodes:
                continue
            visited_global_nodes.add(global_node)

            # import X as Y
            if isinstance(global_node, astroid.Import):
                for (name, alias) in global_node.names:
                    imports.append(Import(name, alias))

            # from X import a as b
            if isinstance(global_node, astroid.ImportFrom):
                base_import_path = module_node.relative_to_absolute_name(
                    global_node.modname, global_node.level
                )

                for (name, alias) in global_node.names:
                    from_imports.append(FromImport(base_import_path, name, alias))

                # Find re-exported declarations in __init__.py files
                if _is_init_file(module_node.file) and is_public_module(
                    module_node.qname()
                ):
                    for declaration, _ in global_node.names:
                        context = InferenceContext()
                        context.lookupname = declaration
                        node = safe_infer(global_node, context)

                        if node is None:
                            logging.warning(
                                f"Could not resolve 'from {global_node.modname} import {declaration}"
                            )
                            continue

                        reexported_name = node.qname()

                        if reexported_name.startswith(module_node.name):
                            if reexported_name not in self.reexported:
                                self.reexported[reexported_name] = []
                            self.reexported[reexported_name].append(id_)

        # Remember module, so we can later add classes and global functions
        module = Module(
            id_,
            module_node.qname(),
            imports,
            from_imports,
        )
        self.__declaration_stack.append(module)

    def leave_module(self, _: astroid.Module) -> None:
        module = self.__declaration_stack.pop()
        if not isinstance(module, Module):
            raise AssertionError("Imbalanced push/pop on stack")

        self.api.add_module(module)

    def enter_classdef(self, class_node: astroid.ClassDef) -> None:
        qname = class_node.qname()

        decorators: Optional[astroid.Decorators] = class_node.decorators
        if decorators is not None:
            decorator_names = [decorator.as_string() for decorator in decorators.nodes]
        else:
            decorator_names = []

        # Remember class, so we can later add methods
        class_ = Class(
            id_=self.__get_id(class_node.name),
            qname=qname,
            decorators=decorator_names,
            superclasses=class_node.basenames,
            is_public=self.is_public(class_node.name, qname),
            reexported_by=self.reexported.get(qname, []),
            documentation=self.docstring_parser.get_class_documentation(class_node),
        )
        self.__declaration_stack.append(class_)

    def leave_classdef(self, _: astroid.ClassDef) -> None:
        class_ = self.__declaration_stack.pop()
        if not isinstance(class_, Class):
            raise AssertionError("Imbalanced push/pop on stack")

        if len(self.__declaration_stack) > 0:
            parent = self.__declaration_stack[-1]

            # Ignore nested classes for now
            if isinstance(parent, Module):
                self.api.add_class(class_)
                parent.add_class(class_.id)

    def enter_functiondef(self, function_node: astroid.FunctionDef) -> None:
        qname = function_node.qname()

        decorators: Optional[astroid.Decorators] = function_node.decorators
        if decorators is not None:
            decorator_names = [decorator.as_string() for decorator in decorators.nodes]
        else:
            decorator_names = []

        is_public = self.is_public(function_node.name, qname)

        function = Function(
            id=self.__get_function_id(function_node.name, decorator_names),
            qname=qname,
            decorators=decorator_names,
            parameters=get_parameter_list(
                self.docstring_parser,
                function_node,
                self.__get_id(function_node.name),
                qname,
                is_public,
            ),
            results=[],  # TODO: results
            is_public=is_public,
            reexported_by=self.reexported.get(qname, []),
            documentation=self.docstring_parser.get_function_documentation(
                function_node
            ),
        )
        self.__declaration_stack.append(function)

    def leave_functiondef(self, _: astroid.FunctionDef) -> None:
        function = self.__declaration_stack.pop()
        if not isinstance(function, Function):
            raise AssertionError("Imbalanced push/pop on stack")

        if len(self.__declaration_stack) > 0:
            parent = self.__declaration_stack[-1]

            # Ignore nested functions for now
            if isinstance(parent, Module):
                self.api.add_function(function)
                parent.add_function(function.id)
            elif isinstance(parent, Class):
                self.api.add_function(function)
                parent.add_method(function.id)

    def is_public(self, name: str, qualified_name: str) -> bool:
        if name.startswith("_") and not name.endswith("__"):
            return False

        if qualified_name in self.reexported:
            return True

        # Containing class is re-exported (always false if the current API element is not a method)
        if (
            isinstance(self.__declaration_stack[-1], Class)
            and parent_qualified_name(qualified_name) in self.reexported
        ):
            return True

        # The slicing is necessary so __init__ functions are not excluded (already handled in the first condition).
        return all(not it.startswith("_") for it in qualified_name.split(".")[:-1])


def is_public_module(module_name: str) -> bool:
    return all(not it.startswith("_") for it in module_name.split("."))
