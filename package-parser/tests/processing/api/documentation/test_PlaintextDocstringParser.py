import astroid
import pytest

from package_parser.processing.api.docstring_parsing import PlaintextDocstringParser
from package_parser.processing.api.model import (
    ClassDocumentation,
    FunctionDocumentation,
    ParameterAssignment,
    ParameterDocumentation,
)


@pytest.fixture
def plaintext_documentation_parser() -> PlaintextDocstringParser:
    return PlaintextDocstringParser()


# language=python
class_with_documentation = '''
class C:
    """
    Lorem ipsum.

    Dolor sit amet.
    """

    def __init__(self, p: int):
        pass
'''

# language=python
class_without_documentation = """
class C:
    pass
"""


@pytest.mark.parametrize(
    "python_code, expected_class_documentation",
    [
        (
            class_with_documentation,
            ClassDocumentation(
                description="",
                full_docstring="Lorem ipsum.\n\nDolor sit amet.",
            ),
        ),
        (
            class_without_documentation,
            ClassDocumentation(description="", full_docstring=""),
        ),
    ],
    ids=[
        "class with documentation",
        "class without documentation",
    ],
)
def test_get_class_documentation(
    plaintext_documentation_parser: PlaintextDocstringParser,
    python_code: str,
    expected_class_documentation: ClassDocumentation,
):
    node = astroid.extract_node(python_code)

    assert isinstance(node, astroid.ClassDef)
    assert (
        plaintext_documentation_parser.get_class_documentation(node)
        == expected_class_documentation
    )


# language=python
function_with_documentation = '''
def f(p: int):
    """
    Lorem ipsum.

    Dolor sit amet.
    """

    pass
'''

# language=python
function_without_documentation = """
def f(p: int):
    pass
"""


@pytest.mark.parametrize(
    "python_code, expected_function_documentation",
    [
        (
            function_with_documentation,
            FunctionDocumentation(
                description="",
                full_docstring="Lorem ipsum.\n\nDolor sit amet.",
            ),
        ),
        (
            function_without_documentation,
            FunctionDocumentation(description="", full_docstring=""),
        ),
    ],
    ids=[
        "function with documentation",
        "function without documentation",
    ],
)
def test_get_function_documentation(
    plaintext_documentation_parser: PlaintextDocstringParser,
    python_code: str,
    expected_function_documentation: FunctionDocumentation,
):
    node = astroid.extract_node(python_code)

    assert isinstance(node, astroid.FunctionDef)
    assert (
        plaintext_documentation_parser.get_function_documentation(node)
        == expected_function_documentation
    )


@pytest.mark.parametrize(
    "python_code, parameter_name, expected_parameter_documentation",
    [
        (
            function_with_documentation,
            "p",
            ParameterDocumentation(
                type="",
                default_value="",
                description="",
            ),
        ),
        (
            function_without_documentation,
            "p",
            ParameterDocumentation(
                type="",
                default_value="",
                description="",
            ),
        ),
    ],
    ids=[
        "function with documentation",
        "function without documentation",
    ],
)
def test_get_parameter_documentation(
    plaintext_documentation_parser: PlaintextDocstringParser,
    python_code: str,
    parameter_name: str,
    expected_parameter_documentation: ParameterDocumentation,
):
    node = astroid.extract_node(python_code)
    assert isinstance(node, astroid.FunctionDef)
    assert (
        plaintext_documentation_parser.get_parameter_documentation(
            node, parameter_name, ParameterAssignment.POSITION_OR_NAME
        )
        == expected_parameter_documentation
    )
