from typing import Optional

import astroid
from package_parser.processing.api.docstring_parsing import (
    AbstractDocstringParser,
)
from package_parser.processing.api.model import Parameter, ParameterAssignment


def get_parameter_list(
    docstring_parser: AbstractDocstringParser,
    function_node: astroid.FunctionDef,
    function_id: str,
    function_qname: str,
    function_is_public: bool,
) -> list[Parameter]:
    parameters_assigned_by = _get_parameters_assigned_by(function_node)
    result = []

    for parameter_name in function_node.argnames():
        parameter_assigned_by = parameters_assigned_by[parameter_name]

        result.append(
            Parameter(
                id_=function_id + "/" + parameter_name,
                name=parameter_name,
                qname=function_qname + "." + parameter_name,
                default_value=_get_stringified_default_value(
                    function_node, parameter_name
                ),
                assigned_by=parameter_assigned_by,
                is_public=function_is_public,
                documentation=docstring_parser.get_parameter_documentation(
                    function_node, parameter_name, parameter_assigned_by
                ),
            )
        )

    return result


def _get_parameters_assigned_by(
    function_node: astroid.FunctionDef,
) -> dict[str, ParameterAssignment]:
    parameters = function_node.args
    n_implicit_parameters = function_node.implicit_parameters()

    result = {}
    for arg in parameters.posonlyargs:
        result[arg.name] = ParameterAssignment.POSITION_ONLY

    for arg in parameters.args:
        result[arg.name] = ParameterAssignment.POSITION_OR_NAME

    if parameters.vararg is not None:
        result[parameters.vararg] = ParameterAssignment.POSITIONAL_VARARG

    for arg in parameters.kwonlyargs:
        result[arg.name] = ParameterAssignment.NAME_ONLY

    if parameters.kwarg is not None:
        result[parameters.kwarg] = ParameterAssignment.NAMED_VARARG

    # Overwrite assigned_by for implicit parameters. If first parameter of instance of class method is variadic,
    # n_implicit_parameters is 0.
    for arg in parameters.arguments[:n_implicit_parameters]:
        result[arg.name] = ParameterAssignment.IMPLICIT

    return result


def _get_stringified_default_value(
    function_node: astroid.FunctionDef, parameter_name
) -> Optional[str]:
    try:
        default_value = function_node.args.default_value(parameter_name)
        if default_value is None:
            return None
        return default_value.as_string()
    except astroid.exceptions.NoDefault:
        return None
