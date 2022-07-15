import re
from typing import Optional, Tuple

import astroid
import numpydoc.docscrape
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

        return ClassDocumentation(
            description=_get_description(NumpyDocString(docstring)),
            full_docstring=docstring,
        )

    def get_function_documentation(
        self, function_node: astroid.FunctionDef
    ) -> FunctionDocumentation:
        docstring = get_full_docstring(function_node)

        return FunctionDocumentation(
            description=_get_description(
                self.__get_cached_function_numpydoc_string(function_node, docstring)
            ),
            full_docstring=docstring,
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
        all_parameters_numpydoc: list[
            numpydoc.docscrape.Parameter
        ] = function_numpydoc.get("Parameters", [])
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
            description="\n".join(
                [line.rstrip() for line in last_parameter_numpydoc.desc]
            ),
        )

    def __get_cached_function_numpydoc_string(
        self, function_node: astroid.FunctionDef, docstring: str
    ) -> NumpyDocString:
        """
        Returns the NumpyDocString for the given function node. It is only recomputed when the function node differs
        from the previous one that was passed to this function. This avoids reparsing the docstring for the function
        itself and all of its parameters.

        On Lars's system this caused a significant performance improvement: Previously, 8.382s were spent inside the
        function get_parameter_documentation when parsing sklearn. Afterwards, it was only 2.113s.
        """

        if self.__cached_function_node is not function_node:
            self.__cached_function_node = function_node
            self.__cached_numpydoc_string = NumpyDocString(docstring)

        return self.__cached_numpydoc_string


def _get_description(numpydoc_string: NumpyDocString) -> str:
    """
    Returns the concatenated summary and extended summary parts of the given docstring or an empty string if these parts
    are blank.
    """

    summary: list[str] = numpydoc_string.get("Summary", [])
    extended_summary: list[str] = numpydoc_string.get("Extended Summary", [])

    result = ""
    result += "\n".join([line.rstrip() for line in summary])
    result += "\n\n"
    result += "\n".join([line.rstrip() for line in extended_summary])
    return result.strip()


def _is_matching_parameter_numpydoc(
    parameter_numpydoc: numpydoc.docscrape.Parameter,
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
        name.strip() == lookup_name for name in parameter_numpydoc.name.split(",")
    )


def _get_type_and_default_value(
    parameter_numpydoc: numpydoc.docscrape.Parameter,
) -> Tuple[str, str]:
    """
    Returns the type and default value for the given NumpyDoc.
    """

    type_ = parameter_numpydoc.type
    parts = re.split(r",\s*optional|,\s*default\s*[:=]?", type_)

    if len(parts) != 2:
        return type_.strip(), ""

    return parts[0].strip(), parts[1].strip()
