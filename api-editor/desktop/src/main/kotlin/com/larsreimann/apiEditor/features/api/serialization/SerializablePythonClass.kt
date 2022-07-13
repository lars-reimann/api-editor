package com.larsreimann.apiEditor.features.api.serialization

import com.larsreimann.apiEditor.features.ast.model.MutablePythonClass
import com.larsreimann.apiEditor.features.ast.model.MutablePythonFunction
import com.larsreimann.apiEditor.features.ast.model.PythonClassDocumentation
import com.larsreimann.apiEditor.features.ast.model.PythonClassId
import com.larsreimann.apiEditor.features.ast.model.PythonDecorator
import com.larsreimann.apiEditor.features.ast.model.PythonFunctionId
import kotlinx.serialization.Serializable

@Serializable
class SerializablePythonClass(
    private val id: String,
    private val name: String,
    private val decorators: List<PythonDecorator> = emptyList(),
    private val superclassIds: List<PythonClassId> = emptyList(),
    private val documentation: PythonClassDocumentation = PythonClassDocumentation(),
    private val methodIds: List<PythonFunctionId> = emptyList(),
) {
    fun toMutablePythonClass(functionById: Map<PythonFunctionId, MutablePythonFunction>) = MutablePythonClass(
        id = PythonClassId(id),
        name = name,
        decorators = decorators,
        superclasses = superclassIds,
        documentation = documentation,
        methods = methodIds.mapNotNull { functionById[it] },
    )
}
