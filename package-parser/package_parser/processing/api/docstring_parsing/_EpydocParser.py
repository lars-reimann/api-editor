from typing import Optional

import astroid
from docstring_parser import parse as parse_docstring, DocstringStyle, Docstring, DocstringParam

from package_parser.processing.api.model import (
    ClassDocumentation,
    FunctionDocumentation,
    ParameterAssignment,
    ParameterDocumentation,
)
from ._AbstractDocstringParser import AbstractDocstringParser
from ._helpers import get_full_docstring, get_description


class EpydocParser(AbstractDocstringParser):
    """
    Parses documentation in the Epydoc format. See http://epydoc.sourceforge.net/epytext.html for more information.

    This class is not thread-safe. Each thread should create its own instance.
    """

    def __init__(self):
        self.__cached_function_node: Optional[astroid.FunctionDef] = None
        self.__cached_docstring: Optional[DocstringParam] = None

    def get_class_documentation(
        self, class_node: astroid.ClassDef
    ) -> ClassDocumentation:
        docstring = get_full_docstring(class_node)
        docstring_obj = parse_docstring(docstring, style=DocstringStyle.EPYDOC)

        return ClassDocumentation(
            description=get_description(docstring_obj)
        )

    def get_function_documentation(
        self, function_node: astroid.FunctionDef
    ) -> FunctionDocumentation:
        docstring = get_full_docstring(function_node)

        return FunctionDocumentation(
            description=get_description(
                self.__get_cached_function_numpydoc_string(function_node, docstring)
            )
        )

    def get_parameter_documentation(
        self,
        function_node: astroid.FunctionDef,
        parameter_name: str,
        parameter_assigned_by: ParameterAssignment,
    ) -> ParameterDocumentation:

        # For constructors (__init__ functions) the parameters are described on the class
        if function_node.name == "__init__" and isinstance(
            function_node.parent, astroid.ClassDef
        ):
            docstring = get_full_docstring(function_node.parent)
        else:
            docstring = get_full_docstring(function_node)

        # Find matching parameter docstrings
        function_numpydoc = self.__get_cached_function_numpydoc_string(
            function_node, docstring
        )
        all_parameters_numpydoc: list[DocstringParam] = function_numpydoc.params
        matching_parameters_numpydoc = [
            it
            for it in all_parameters_numpydoc
            if it.arg_name == parameter_name
        ]

        if len(matching_parameters_numpydoc) == 0:
            return ParameterDocumentation(type="", default_value="", description="")

        last_parameter_docstring_obj = matching_parameters_numpydoc[-1]
        return ParameterDocumentation(
            type=last_parameter_docstring_obj.type_name or "",
            default_value=last_parameter_docstring_obj.default or "",
            description=last_parameter_docstring_obj.description,
        )

    def __get_cached_function_numpydoc_string(
        self, function_node: astroid.FunctionDef, docstring: str
    ) -> Docstring:
        """
        Returns the NumpyDocString for the given function node. It is only recomputed when the function node differs
        from the previous one that was passed to this function. This avoids reparsing the docstring for the function
        itself and all of its parameters.

        On Lars's system this caused a significant performance improvement: Previously, 8.382s were spent inside the
        function get_parameter_documentation when parsing sklearn. Afterwards, it was only 2.113s.
        """

        if self.__cached_function_node is not function_node:
            self.__cached_function_node = function_node
            self.__cached_docstring = parse_docstring(docstring, style=DocstringStyle.EPYDOC)

        return self.__cached_docstring
