package com.larsreimann.apiEditor.features.ast.model

interface PythonModule : PythonDeclaration {
    override val id: PythonModuleId
    val imports: List<PythonImport>
    val fromImports: List<PythonFromImport>
    val classes: List<PythonClass>
    val functions: List<PythonFunction>

    override fun children() = sequence {
        yieldAll(classes)
        yieldAll(functions)
    }

    fun toMutablePythonModule() = MutablePythonModule(
        id = id,
        imports = imports,
        fromImports = fromImports,
        classes = classes.map { it.toMutablePythonClass() },
        functions = functions.map { it.toMutablePythonFunction() },
    )
}

class MutablePythonModule(
    override val id: PythonModuleId = PythonModuleId(""),
    override var name: String = "",
    imports: List<PythonImport> = emptyList(),
    fromImports: List<PythonFromImport> = emptyList(),
    classes: List<MutablePythonClass> = emptyList(),
    functions: List<MutablePythonFunction> = emptyList(),
) : MutablePythonDeclaration(), PythonModule {

    override val imports = imports.toMutableList()
    override val fromImports = fromImports.toMutableList()
    override val classes = MutableContainmentList(classes)
    override val functions = MutableContainmentList(functions)
}

typealias PythonModuleId = PythonDeclarationId<PythonModule>
