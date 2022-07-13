package com.larsreimann.apiEditor.features.ast.model

import kotlinx.serialization.Serializable

interface PythonFunction : PythonDeclaration {
    override val id: PythonFunctionId
    val decorators: List<PythonDecorator>
    val documentation: PythonFunctionDocumentation
    val parameters: List<PythonParameter>
    val results: List<PythonResult>

    override fun children() = sequence {
        yieldAll(parameters)
        yieldAll(results)
    }

    fun toMutablePythonFunction() = MutablePythonFunction(
        id = id,
        name = name,
        decorators = decorators,
        documentation = documentation,
        parameters = parameters.map { it.toMutablePythonParameter() },
        results = results.map { it.toMutablePythonResult() },
    )
}

class MutablePythonFunction(
    override val id: PythonFunctionId = PythonFunctionId(""),
    override var name: String = "",
    decorators: List<PythonDecorator> = emptyList(),
    override var documentation: PythonFunctionDocumentation = PythonFunctionDocumentation(),
    parameters: List<MutablePythonParameter> = listOf(),
    results: List<MutablePythonResult> = listOf(),
) : MutablePythonDeclaration(), PythonFunction {

    override val decorators = decorators.toMutableList()
    override val parameters = MutableContainmentList(parameters)
    override val results = MutableContainmentList(results)
}

typealias PythonFunctionId = PythonDeclarationId<PythonFunction>

@Serializable
data class PythonFunctionDocumentation(val description: String = "")
