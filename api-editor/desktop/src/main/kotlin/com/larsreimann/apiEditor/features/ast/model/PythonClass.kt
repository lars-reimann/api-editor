package com.larsreimann.apiEditor.features.ast.model

import kotlinx.serialization.Serializable

interface PythonClass : PythonDeclaration {
    override val id: PythonClassId
    val decorators: List<PythonDecorator>
    val superclasses: List<PythonClassId>
    val documentation: PythonClassDocumentation
    val methods: List<PythonFunction>

    override fun children() = sequence {
        yieldAll(methods)
    }

    fun toMutablePythonClass() = MutablePythonClass(
        id = id,
        name = name,
        decorators = decorators,
        superclasses = superclasses,
        documentation = documentation,
        methods = methods.map { it.toMutablePythonFunction() },
    )
}

class MutablePythonClass(
    override val id: PythonClassId = PythonClassId(""),
    override var name: String = "",
    decorators: List<PythonDecorator> = emptyList(),
    superclasses: List<PythonClassId> = emptyList(),
    override var documentation: PythonClassDocumentation = PythonClassDocumentation(),
    methods: List<MutablePythonFunction> = listOf(),
) : MutablePythonDeclaration(), PythonClass {

    override val decorators = decorators.toMutableList()
    override val superclasses = superclasses.toMutableList()
    override val methods = MutableContainmentList(methods)
}

typealias PythonClassId = PythonDeclarationId<PythonClass>

@Serializable
data class PythonClassDocumentation(val description: String = "")
