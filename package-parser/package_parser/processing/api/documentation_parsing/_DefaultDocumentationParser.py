import astroid
from package_parser.processing.api.model import (
    ClassDocumentation,
    FunctionDocumentation,
    ParameterAssignment,
    ParameterDocumentation,
)

from ._AbstractDocumentationParser import AbstractDocumentationParser
from ._get_full_docstring import get_full_docstring


class DefaultDocumentationParser(AbstractDocumentationParser):
    """
    Parses documentation in any format. Should not be used if there is another parser for the specific format.
    """

    def get_class_documentation(
        self, class_node: astroid.ClassDef
    ) -> ClassDocumentation:
        return ClassDocumentation(
            full_docstring=get_full_docstring(class_node),
        )

    def get_function_documentation(
        self, function_node: astroid.FunctionDef
    ) -> FunctionDocumentation:
        return FunctionDocumentation(
            full_docstring=get_full_docstring(function_node),
        )

    def get_parameter_documentation(
        self,
        function_node: astroid.FunctionDef,
        parameter_name: str,
        parameter_assigned_by: ParameterAssignment,
    ) -> ParameterDocumentation:
        return ParameterDocumentation()
