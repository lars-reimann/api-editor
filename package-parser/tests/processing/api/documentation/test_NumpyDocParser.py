import astroid
import pytest

from package_parser.processing.api.documentation import NumpyDocParser, ClassDocumentation, ParameterDocumentation, \
    FunctionDocumentation


@pytest.fixture
def numpydoc_parser() -> NumpyDocParser:
    return NumpyDocParser()


# language=python
class_with_documentation = '''
class C:
    """
    Lorem ipsum.

    Dolor sit amet.
    """

    def __init__(self):
        pass
'''

# language=python
class_without_documentation = '''
class C:
    pass
'''


@pytest.mark.parametrize(
    "python_code, expected_class_documentation",
    [

        (class_with_documentation, ClassDocumentation(
            description="Lorem ipsum.\n\nDolor sit amet.",
            full_docstring="Lorem ipsum.\n\nDolor sit amet.")),
        (class_without_documentation, ClassDocumentation(
            description="",
            full_docstring=""
        )),
    ],
    ids=[
        "class with documentation",
        "class without documentation",
    ]
)
def test_get_class_documentation(
    numpydoc_parser: NumpyDocParser,
    python_code: str,
    expected_class_documentation: ClassDocumentation
):
    node = astroid.extract_node(python_code)

    assert isinstance(node, astroid.ClassDef)
    assert numpydoc_parser.get_class_documentation(node) == expected_class_documentation


# language=python
function_with_documentation = '''
def f():
    """
    Lorem ipsum.

    Dolor sit amet.
    """

    pass
'''

# language=python
function_without_documentation = '''
def f():
    pass
'''


@pytest.mark.parametrize(
    "python_code, expected_function_documentation",
    [

        (function_with_documentation, FunctionDocumentation(
            description="Lorem ipsum.\n\nDolor sit amet.",
            full_docstring="Lorem ipsum.\n\nDolor sit amet.")),
        (function_without_documentation, FunctionDocumentation(
            description="",
            full_docstring=""
        ))

    ],
    ids=[
        "function with documentation",
        "function without documentation",
    ]
)
def test_get_function_documentation(
    numpydoc_parser: NumpyDocParser,
    python_code: str,
    expected_function_documentation: FunctionDocumentation
):
    node = astroid.extract_node(python_code)

    assert isinstance(node, astroid.FunctionDef)
    assert numpydoc_parser.get_function_documentation(node) == expected_function_documentation


# language=python
class_with_parameters = '''
class C:
    """
    Lorem ipsum.

    Dolor sit amet.

    Parameters
    ----------
    p : int, default=1
        foo
    """

    def __init__(self, p: int = 1):
        pass
'''

# language=python
function_with_parameters = '''
def f(
    no_type_no_default,
    type_no_default,
    optional_unknown_default: int = 0,
    with_default_syntax_1: int = 1,
    with_default_syntax_2: int = 2,
    with_default_syntax_3: int = 3,
    grouped_parameter_1: int = 4,
    grouped_parameter_2: int = 4
):
    """
    Lorem ipsum.

    Dolor sit amet.

    Parameters
    ----------
    no_type_no_default
        foo: no_type_no_default
    type_no_default : int
        foo: type_no_default
    optional_unknown_default : int, optional
        foo: optional_unknown_default
    with_default_syntax_1 : int, default 1
        foo: with_default_syntax_1
    with_default_syntax_2 : int, default: 2
        foo: with_default_syntax_2
    with_default_syntax_3 : int, default=3
        foo: with_default_syntax_3
    grouped_parameter_1, grouped_parameter_2 : int, default=4
        foo: grouped_parameter_1 and grouped_parameter_2
    """

    pass
'''


@pytest.mark.parametrize(
    "python_code, parameter_name, expected_parameter_documentation",
    [

        (class_with_parameters, "p", ParameterDocumentation(
            type="int",
            default_value="1",
            description="foo",
        )),
        (class_with_parameters, "missing", ParameterDocumentation(
            type="",
            default_value="",
            description="",
        )),
        (function_with_parameters, "no_type_no_default", ParameterDocumentation(
            type="",
            default_value="",
            description="foo: no_type_no_default",
        )),
        (function_with_parameters, "type_no_default", ParameterDocumentation(
            type="int",
            default_value="",
            description="foo: type_no_default",
        )),
        (function_with_parameters, "optional_unknown_default", ParameterDocumentation(
            type="int",
            default_value="",
            description="foo: optional_unknown_default",
        )),
        (function_with_parameters, "with_default_syntax_1", ParameterDocumentation(
            type="int",
            default_value="1",
            description="foo: with_default_syntax_1",
        )),
        (function_with_parameters, "with_default_syntax_2", ParameterDocumentation(
            type="int",
            default_value="2",
            description="foo: with_default_syntax_2"
        )),
        (function_with_parameters, "with_default_syntax_3", ParameterDocumentation(
            type="int",
            default_value="3",
            description="foo: with_default_syntax_3"
        )),
        (function_with_parameters, "grouped_parameter_1", ParameterDocumentation(
            type="int",
            default_value="4",
            description="foo: grouped_parameter_1 and grouped_parameter_2"
        )),
        (function_with_parameters, "grouped_parameter_2", ParameterDocumentation(
            type="int",
            default_value="4",
            description="foo: grouped_parameter_1 and grouped_parameter_2"
        )),
        (function_with_parameters, "missing", ParameterDocumentation(
            type="",
            default_value="",
            description=""
        )),
    ],
    ids=[
        "existing class parameter",
        "missing class parameter",
        "function parameter with no type and no default",
        "function parameter with type and no default",
        "function parameter with optional unknown default",
        "function parameter with default syntax 1 (just space)",
        "function parameter with default syntax 2 (colon)",
        "function parameter with default syntax 3 (equals)",
        "function parameter with grouped parameters 1",
        "function parameter with grouped parameters 2",
        "missing function parameter",
    ]
)
def test_get_parameter_documentation(
    numpydoc_parser: NumpyDocParser,
    python_code: str,
    parameter_name: str,
    expected_parameter_documentation: ParameterDocumentation
):
    node = astroid.extract_node(python_code)
    assert isinstance(node, astroid.ClassDef) or isinstance(node, astroid.FunctionDef)

    # Find the constructor
    if isinstance(node, astroid.ClassDef):
        for method in node.mymethods():
            if method.name == "__init__":
                node = method

    assert isinstance(node, astroid.FunctionDef)
    assert numpydoc_parser.get_parameter_documentation(node, parameter_name) == expected_parameter_documentation