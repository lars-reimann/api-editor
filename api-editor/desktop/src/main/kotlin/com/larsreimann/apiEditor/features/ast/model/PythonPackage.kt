package com.larsreimann.apiEditor.features.ast.model

interface PythonPackage : PythonDeclaration {
    override val id: PythonPackageId
    val modules: List<PythonModule>

    override fun children() = sequence {
        yieldAll(modules)
    }

    fun toMutablePythonPackage() = MutablePythonPackage(
        id = id,
        modules = modules.map { it.toMutablePythonModule() },
    )
}

class MutablePythonPackage(
    override val id: PythonPackageId = PythonPackageId(""),
    override var name: String = "",
    modules: List<MutablePythonModule> = emptyList(),
) : MutablePythonDeclaration(), PythonPackage {

    override val modules = MutableContainmentList(modules)
}

typealias PythonPackageId = PythonDeclarationId<PythonPackage>
