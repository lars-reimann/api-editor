import pytest
from package_parser.processing.api.model import (
    ClassDocumentation,
    FunctionDocumentation,
    ParameterDocumentation,
)


@pytest.mark.parametrize(
    "class_documentation",
    [
        ClassDocumentation(),
        ClassDocumentation(description="foo"),
    ],
)
def test_dict_conversion_for_class_documentation(
    class_documentation: ClassDocumentation,
):
    assert (
        ClassDocumentation.from_dict(class_documentation.to_dict())
        == class_documentation
    )


@pytest.mark.parametrize(
    "function_documentation",
    [
        FunctionDocumentation(),
        FunctionDocumentation(description="foo"),
    ],
)
def test_dict_conversion_for_function_documentation(
    function_documentation: FunctionDocumentation,
):
    assert (
        FunctionDocumentation.from_dict(function_documentation.to_dict())
        == function_documentation
    )


@pytest.mark.parametrize(
    "parameter_documentation",
    [
        ParameterDocumentation(),
        ParameterDocumentation(type="int", default_value="1", description="foo bar"),
    ],
)
def test_dict_conversion_for_parameter_documentation(
    parameter_documentation: ParameterDocumentation,
):
    assert (
        ParameterDocumentation.from_dict(parameter_documentation.to_dict())
        == parameter_documentation
    )
