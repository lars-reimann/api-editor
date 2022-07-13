package com.larsreimann.apiEditor.features.api.serialization

import com.larsreimann.apiEditor.features.ast.model.MutablePythonClass
import com.larsreimann.apiEditor.features.ast.model.MutablePythonFunction
import com.larsreimann.apiEditor.features.ast.model.MutablePythonModule
import com.larsreimann.apiEditor.features.ast.model.PythonClassId
import com.larsreimann.apiEditor.features.ast.model.PythonFromImport
import com.larsreimann.apiEditor.features.ast.model.PythonFunctionId
import com.larsreimann.apiEditor.features.ast.model.PythonImport
import com.larsreimann.apiEditor.features.ast.model.PythonModuleId
import kotlinx.serialization.Serializable

@Serializable
class SerializablePythonModule(
    private val id: String,
    private val name: String,
    private val imports: List<PythonImport> = emptyList(),
    private val fromImports: List<PythonFromImport> = emptyList(),
    private val classIds: List<PythonClassId> = emptyList(),
    private val functionIds: List<PythonFunctionId> = emptyList(),
) {
    fun toMutablePythonModule(
        classById: Map<PythonClassId, MutablePythonClass>,
        functionById: Map<PythonFunctionId, MutablePythonFunction>,
    ) = MutablePythonModule(
        id = PythonModuleId(id),
        name = name,
        imports = imports,
        fromImports = fromImports,
        classes = classIds.mapNotNull { classById[it] },
        functions = functionIds.mapNotNull { functionById[it] },
    )
}
