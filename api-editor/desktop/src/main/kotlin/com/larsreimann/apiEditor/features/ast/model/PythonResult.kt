package com.larsreimann.apiEditor.features.ast.model

import kotlinx.serialization.Serializable

interface PythonResult : PythonDeclaration {
    override val id: PythonResultId
    val type: PythonType?
    val documentation: PythonResultDocumentation

    override fun children() = emptySequence<PythonAstNode>()

    fun toMutablePythonResult() = MutablePythonResult(
        id = id,
        name = name,
        type = type,
        documentation = documentation,
    )
}

class MutablePythonResult(
    override val id: PythonResultId = PythonResultId(""),
    override var name: String = "",
    override var type: PythonType? = null,
    override var documentation: PythonResultDocumentation = PythonResultDocumentation(),
) : MutablePythonDeclaration(), PythonResult

typealias PythonResultId = PythonDeclarationId<PythonResult>

@Serializable
data class PythonResultDocumentation(val type: String = "", val description: String = "")
