package com.larsreimann.api_editor.codegen

import com.larsreimann.api_editor.model.ComparisonOperator
import com.larsreimann.api_editor.model.PythonParameterAssignment
import com.larsreimann.api_editor.model.SerializablePythonParameter
import com.larsreimann.api_editor.mutable_model.MutablePythonClass
import com.larsreimann.api_editor.mutable_model.MutablePythonFunction
import com.larsreimann.api_editor.mutable_model.MutablePythonModule
import com.larsreimann.api_editor.mutable_model.MutablePythonParameter
import java.util.Objects

/**
 * Builds a string containing the formatted module content
 * @receiver The module whose adapter content should be built
 * @return The string containing the formatted module content
 */
fun MutablePythonModule.toPythonCode(): String {
    var formattedImport = buildNamespace(this)
    var formattedClasses = buildAllClasses(this)
    var formattedFunctions = buildAllFunctions(this)
    val separators = buildSeparators(
        formattedImport, formattedClasses, formattedFunctions
    )
    formattedImport += separators[0]
    formattedClasses += separators[1]
    formattedFunctions += separators[2]
    return (formattedImport
        + formattedClasses
        + formattedFunctions)
}

private fun buildNamespace(pythonModule: MutablePythonModule): String {
    val importedModules = HashSet<String>()
    pythonModule.functions.forEach { pythonFunction: MutablePythonFunction ->
        importedModules.add(
            buildParentDeclarationName(pythonFunction.originalDeclaration!!.qualifiedName)
        )
    }
    pythonModule.classes.forEach { pythonClass: MutablePythonClass ->
        importedModules.add(
            buildParentDeclarationName(pythonClass.originalDeclaration!!.qualifiedName)
        )
    }
    val imports: MutableList<String> = ArrayList()
    importedModules.forEach { moduleName: String -> imports.add("import $moduleName") }
    return imports.joinToString("\n".repeat(1))
}

private fun buildParentDeclarationName(qualifiedName: String): String {
    val pathSeparator = "."
    val separationPosition = qualifiedName.lastIndexOf(pathSeparator)
    return qualifiedName.substring(0, separationPosition)
}

private fun buildAllClasses(pythonModule: MutablePythonModule): String {
    return pythonModule.classes.joinToString("\n".repeat(2)) { it.toPythonCode() }
}

private fun buildAllFunctions(pythonModule: MutablePythonModule): String {
    return pythonModule.functions.joinToString("\n".repeat(2)) { it.toPythonCode() }
}

private fun buildSeparators(
    formattedImports: String,
    formattedClasses: String,
    formattedFunctions: String
): Array<String> {
    val importSeparator: String = if (formattedImports.isBlank()) {
        ""
    } else if (formattedClasses.isBlank() && formattedFunctions.isBlank()) {
        "\n"
    } else {
        "\n\n"
    }
    val classesSeparator: String = if (formattedClasses.isBlank()) {
        ""
    } else if (formattedFunctions.isBlank()) {
        "\n"
    } else {
        "\n\n"
    }
    val functionSeparator: String = if (formattedFunctions.isBlank()) {
        ""
    } else {
        "\n"
    }
    return arrayOf(importSeparator, classesSeparator, functionSeparator)
}

/**
 * Builds a string containing the formatted class content
 * @receiver The module whose adapter content should be built
 * @return The string containing the formatted class content
 */
fun MutablePythonClass.toPythonCode(): String {
    var formattedClass = "class $name:"
    if (!methods.isEmpty()) {
        formattedClass = """
                $formattedClass

                """.trimIndent()
        formattedClass = (formattedClass
            + buildAllFunctions(this).joinToString("\n".repeat(2)).prependIndent("    "))
    }
    return formattedClass
}

private fun buildAllFunctions(pythonClass: MutablePythonClass): List<String> {
    val formattedFunctions: MutableList<String> = ArrayList()
    pythonClass.methods
        .forEach { pythonFunction: MutablePythonFunction ->
            formattedFunctions.add(pythonFunction.toPythonCode())
        }
    return formattedFunctions
}

/**
 * Builds a string containing the formatted function content
 * @receiver The function whose adapter content should be built
 * @return The string containing the formatted function content
 */
fun MutablePythonFunction.toPythonCode(): String {
    var constructorSuffix = ""
    if (isConstructor()) {
        var constructorSeparator = ""
        val assignments = buildAttributeAssignments(this).joinToString("\n".repeat(1))
        if (assignments.isNotBlank()) {
            constructorSeparator = "\n"
        }
        constructorSuffix = constructorSeparator + assignments
    }
    return """
              def $name(${buildParameters(this)}):
              ${(buildFunctionBody(this) + constructorSuffix).prependIndent("    ")}
              """.trimIndent()
}

private fun buildAttributeAssignments(pythonFunction: MutablePythonFunction): List<String> {
    val attributeAssignments: MutableList<String> = ArrayList()
    for ((name, defaultValue, assignedBy) in pythonFunction.parameters) {
        if (assignedBy == PythonParameterAssignment.ATTRIBUTE) {
            attributeAssignments.add(
                "self."
                    + name
                    + " = "
                    + defaultValue
            )
        }
    }
    return attributeAssignments
}

private fun buildParameters(pythonFunction: MutablePythonFunction): String {
    var formattedFunctionParameters = ""
    val implicitParameters: MutableList<String> = ArrayList()
    val positionOnlyParameters: MutableList<String> = ArrayList()
    val positionOrNameParameters: MutableList<String> = ArrayList()
    val nameOnlyParameters: MutableList<String> = ArrayList()
    pythonFunction.parameters.forEach { pythonParameter: MutablePythonParameter ->
        when (pythonParameter.assignedBy) {
            PythonParameterAssignment.IMPLICIT -> implicitParameters
                .add(buildFormattedParameter(pythonParameter))
            PythonParameterAssignment.POSITION_ONLY -> positionOnlyParameters
                .add(buildFormattedParameter(pythonParameter))
            PythonParameterAssignment.POSITION_OR_NAME -> positionOrNameParameters
                .add(buildFormattedParameter(pythonParameter))
            PythonParameterAssignment.NAME_ONLY -> nameOnlyParameters
                .add(buildFormattedParameter(pythonParameter))
            else -> {}
        }
    }
    assert(implicitParameters.size < 2)
    val hasImplicitParameter = implicitParameters.isNotEmpty()
    val hasPositionOnlyParameters = positionOnlyParameters.isNotEmpty()
    val hasPositionOrNameParameters = positionOrNameParameters.isNotEmpty()
    val hasNameOnlyParameters = nameOnlyParameters.isNotEmpty()
    if (hasImplicitParameter) {
        formattedFunctionParameters = (formattedFunctionParameters
            + implicitParameters[0])
        if (hasPositionOnlyParameters ||
            hasPositionOrNameParameters ||
            hasNameOnlyParameters
        ) {
            formattedFunctionParameters = "$formattedFunctionParameters, "
        }
    }
    if (hasPositionOnlyParameters) {
        formattedFunctionParameters = (formattedFunctionParameters
            + java.lang.String.join(", ", positionOnlyParameters))
        formattedFunctionParameters = if (hasPositionOrNameParameters) {
            (formattedFunctionParameters
                + ", /, ")
        } else if (hasNameOnlyParameters) {
            (formattedFunctionParameters
                + ", /")
        } else {
            (formattedFunctionParameters
                + ", /")
        }
    }
    if (hasPositionOrNameParameters) {
        formattedFunctionParameters =
            (formattedFunctionParameters + java.lang.String.join(", ", positionOrNameParameters))
    }
    if (hasNameOnlyParameters) {
        formattedFunctionParameters = if (hasPositionOnlyParameters || hasPositionOrNameParameters) {
            "$formattedFunctionParameters, *, "
        } else {
            "$formattedFunctionParameters*, "
        }
        formattedFunctionParameters = (formattedFunctionParameters
            + java.lang.String.join(", ", nameOnlyParameters))
    }
    return formattedFunctionParameters
}

private fun buildFormattedParameter(pythonParameter: MutablePythonParameter): String {
    var formattedParameter = pythonParameter.name
    val defaultValue = pythonParameter.defaultValue
    if (defaultValue != null) {
        formattedParameter = "$formattedParameter=$defaultValue"
    }
    return formattedParameter
}

private fun buildFunctionBody(pythonFunction: MutablePythonFunction): String {
    var formattedBoundaries = buildBoundaryChecks(pythonFunction).joinToString("\n".repeat(1))
    if (formattedBoundaries.isNotBlank()) {
        formattedBoundaries = """
                $formattedBoundaries

                """.trimIndent()
    }
    return (formattedBoundaries
        + Objects.requireNonNull(pythonFunction.originalDeclaration)!!.qualifiedName
        + "("
        + buildParameterCall(pythonFunction)
        + ")")
}

private fun buildBoundaryChecks(pythonFunction: MutablePythonFunction): List<String> {
    val formattedBoundaries: MutableList<String> = ArrayList()
    pythonFunction
        .parameters
        .filter { (_, _, _, _, _, _, boundary): MutablePythonParameter -> boundary != null }
        .forEach { (name, _, _, _, _, _, boundary) ->
            assert(boundary != null)
            if (boundary!!.isDiscrete) {
                formattedBoundaries.add(
                    """
                            if not (isinstance($name, int) or (isinstance($name, float) and $name.is_integer())):

                            """.trimIndent()
                        + ("raise ValueError('"
                        + name
                        + " needs to be an integer, but {} was assigned."
                        + "'.format("
                        + name
                        + "))"
                        ).prependIndent("    ")
                )
            }
            if (boundary.lowerLimitType !== ComparisonOperator.UNRESTRICTED && boundary.upperLimitType !== ComparisonOperator.UNRESTRICTED) {
                formattedBoundaries.add(
                    """if not ${boundary.lowerIntervalLimit} ${boundary.lowerLimitType.operator} $name ${boundary.upperLimitType.operator} ${boundary.upperIntervalLimit}:
    """
                        + ("raise ValueError('Valid values of "
                        + name
                        + " must be in "
                        + boundary.asInterval()
                        + ", but {} was assigned."
                        + "'.format("
                        + name
                        + "))"
                        ).prependIndent("    ")
                )
            } else if (boundary.lowerLimitType === ComparisonOperator.LESS_THAN) {
                formattedBoundaries.add(
                    """if not ${boundary.lowerIntervalLimit} < $name:
    """
                        + ("raise ValueError('Valid values of "
                        + name
                        + " must be greater than "
                        + boundary.lowerIntervalLimit
                        + ", but {} was assigned."
                        + "'.format("
                        + name
                        + "))"
                        ).prependIndent("    ")
                )
            } else if (boundary.lowerLimitType === ComparisonOperator.LESS_THAN_OR_EQUALS) {
                formattedBoundaries.add(
                    """if not ${boundary.lowerIntervalLimit} <= $name:
    """
                        + ("raise ValueError('Valid values of "
                        + name
                        + " must be greater than or equal to "
                        + boundary.lowerIntervalLimit
                        + ", but {} was assigned."
                        + "'.format("
                        + name
                        + "))"
                        ).prependIndent("    ")
                )
            }
            if (boundary.upperLimitType === ComparisonOperator.LESS_THAN) {
                formattedBoundaries.add(
                    """if not $name < ${boundary.upperIntervalLimit}:
    """
                        + ("raise ValueError('Valid values of "
                        + name
                        + " must be less than "
                        + boundary.upperIntervalLimit
                        + ", but {} was assigned."
                        + "'.format("
                        + name
                        + "))"
                        ).prependIndent("    ")
                )
            } else if (boundary.upperLimitType === ComparisonOperator.LESS_THAN) {
                formattedBoundaries.add(
                    """if not $name <= ${boundary.upperIntervalLimit}:
    """
                        + ("raise ValueError('Valid values of "
                        + name
                        + " must be less than or equal to "
                        + boundary.upperIntervalLimit
                        + ", but {} was assigned."
                        + "'.format("
                        + name
                        + "))"
                        ).prependIndent("    ")
                )
            }
        }
    return formattedBoundaries
}

private fun buildParameterCall(pythonFunction: MutablePythonFunction): String {
    val formattedParameters: MutableList<String?> = ArrayList()
    val originalNameToValueMap: MutableMap<String, String?> = HashMap()
    pythonFunction.parameters.forEach { (name, defaultValue, assignedBy, _, _, _, _, _, originalDeclaration): MutablePythonParameter ->
        val value: String? =
            if (assignedBy == PythonParameterAssignment.CONSTANT || assignedBy == PythonParameterAssignment.ATTRIBUTE) {
                defaultValue
            } else {
                name
            }
        originalNameToValueMap[originalDeclaration!!.name] = value
    }
    pythonFunction.originalDeclaration!!.parameters.stream()
        .filter { (_, _, _, assignedBy): SerializablePythonParameter -> assignedBy != PythonParameterAssignment.IMPLICIT }
        .forEach { (name, _, _, assignedBy): SerializablePythonParameter ->
            if (assignedBy === PythonParameterAssignment.NAME_ONLY) {
                formattedParameters.add(name + "=" + originalNameToValueMap[name])
            } else {
                formattedParameters.add(originalNameToValueMap[name])
            }
        }
    return java.lang.String.join(", ", formattedParameters)
}
