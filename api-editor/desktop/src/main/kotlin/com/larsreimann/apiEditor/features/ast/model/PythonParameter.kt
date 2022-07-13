package com.larsreimann.apiEditor.features.ast.model

import kotlinx.serialization.Serializable

interface PythonParameter : PythonDeclaration {
    override val id: PythonParameterId
    val type: PythonType?
    val defaultValue: PythonExpression?
    val assignment: PythonParameterAssignment
    val documentation: PythonParameterDocumentation

    override fun children() = emptySequence<PythonAstNode>()

    fun toMutablePythonParameter() = MutablePythonParameter(
        id = id,
        name = name,
        type = type,
        defaultValue = defaultValue,
        assignment = assignment,
        documentation = documentation,
    )
}

class MutablePythonParameter(
    override val id: PythonParameterId = PythonParameterId(""),
    override var name: String = "",
    override var type: PythonType? = null,
    override var defaultValue: PythonExpression? = null,
    override var assignment: PythonParameterAssignment = PythonParameterAssignment.PositionalOrKeyword,
    override var documentation: PythonParameterDocumentation = PythonParameterDocumentation(),
) : MutablePythonDeclaration(), PythonParameter

typealias PythonParameterId = PythonDeclarationId<PythonParameter>

/**
 * Describes how a value can be assigned to a parameter when the containing function is called.
 */
enum class PythonParameterAssignment {

    /**
     * The parameter is set implicitly. This happens in two cases:
     *   * It's the first parameter of an instance method. These are usually called `self`.
     *   * It's the first parameter of a class method (`@classmethod` decorator). These are usually called `cls`.
     */
    Implicit,

    /**
     * The parameter can only be set with a positional argument. Such parameters are listed before a `/` in the
     * parameter list.
     */
    PositionalOnly,

    /**
     * The parameter can be set with a positional or a keyword argument. This the default behavior for parameters unless
     * they fall into one of the other categories.
     */
    PositionalOrKeyword,

    /**
     * The parameter is set to a tuple of all positional arguments that are not assigned to other parameters. The name
     * of such a parameter starts with `*`.
     */
    VariadicPositional,

    /**
     * The parameter can only be set with a keyword argument. Such parameters are listed after a `*` or a variadic
     * positional parameter in the parameter list.
     */
    KeywordOnly,

    /**
     * The parameter is set to a dictionary of all keyword arguments that are not assigned to other parameters. The name
     * of such a parameter starts with `**`.
     */
    VariadicKeyword,
}

@Serializable
data class PythonParameterDocumentation(
    val type: String = "",
    val defaultValue: String = "",
    val description: String = "",
)
