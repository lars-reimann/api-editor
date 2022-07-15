import re
from typing import Optional, Tuple

import astroid
from docstring_parser import parse as parse_docstring, DocstringStyle, Docstring, DocstringParam
from numpydoc.docscrape import NumpyDocString

from package_parser.processing.api.model import (
    ClassDocumentation,
    FunctionDocumentation,
    ParameterAssignment,
    ParameterDocumentation,
)
from ._AbstractDocstringParser import AbstractDocstringParser
from ._get_full_docstring import get_full_docstring


class NumpyDocParser(AbstractDocstringParser):
    """
    Parses documentation in the NumpyDoc format. See https://numpydoc.readthedocs.io/en/latest/format.html for more
    information.

    This class is not thread-safe. Each thread should create its own instance.
    """

    def __init__(self):
        self.__cached_function_node: Optional[astroid.FunctionDef] = None
        self.__cached_numpydoc_string: Optional[NumpyDocString] = None

    def get_class_documentation(
        self, class_node: astroid.ClassDef
    ) -> ClassDocumentation:
        docstring = get_full_docstring(class_node)
        docstring_obj = parse_docstring(docstring, style=DocstringStyle.NUMPYDOC)

        return ClassDocumentation(
            description=_get_description(docstring_obj)
        )

    def get_function_documentation(
        self, function_node: astroid.FunctionDef
    ) -> FunctionDocumentation:
        docstring = get_full_docstring(function_node)

        return FunctionDocumentation(
            description=_get_description(
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
            if _is_matching_parameter_numpydoc(
                it, parameter_name, parameter_assigned_by
            )
        ]

        if len(matching_parameters_numpydoc) == 0:
            return ParameterDocumentation(type="", default_value="", description="")

        last_parameter_numpydoc = matching_parameters_numpydoc[-1]
        type_, default_value = _get_type_and_default_value(last_parameter_numpydoc)
        return ParameterDocumentation(
            type=type_,
            default_value=default_value,
            description=last_parameter_numpydoc.description,
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
            self.__cached_numpydoc_string = parse_docstring(docstring)

        return self.__cached_numpydoc_string


def _get_description(numpydoc_string: Docstring) -> str:
    """
    Returns the concatenated short and long description of the given docstring or an empty string if these parts
    are blank.
    """

    summary: str = numpydoc_string.short_description or ""
    extended_summary: str = numpydoc_string.long_description or ""

    result = ""
    result += summary.rstrip()
    result += "\n\n"
    result += extended_summary.rstrip()
    return result.strip()


def _is_matching_parameter_numpydoc(
    parameter_numpydoc: DocstringParam,
    parameter_name: str,
    parameter_assigned_by: ParameterAssignment,
) -> bool:
    """
    Returns whether the given NumpyDoc applied to the parameter with the given name.
    """

    if parameter_assigned_by == ParameterAssignment.POSITIONAL_VARARG:
        lookup_name = f"*{parameter_name}"
    elif parameter_assigned_by == ParameterAssignment.NAMED_VARARG:
        lookup_name = f"**{parameter_name}"
    else:
        lookup_name = parameter_name

    # Numpydoc allows multiple parameters to be documented at once. See
    # https://numpydoc.readthedocs.io/en/latest/format.html#parameters for more information.
    return any(
        name.strip() == lookup_name for name in parameter_numpydoc.arg_name.split(",")
    )


def _get_type_and_default_value(
    parameter_numpydoc: DocstringParam,
) -> Tuple[str, str]:
    """
    Returns the type and default value for the given NumpyDoc.
    """

    type_name = parameter_numpydoc.type_name or ""
    parts = re.split(r",\s*optional|,\s*default\s*[:=]?", type_name)

    if len(parts) != 2:
        return type_name.strip(), parameter_numpydoc.default or ""

    return parts[0].strip(), parts[1].strip()
