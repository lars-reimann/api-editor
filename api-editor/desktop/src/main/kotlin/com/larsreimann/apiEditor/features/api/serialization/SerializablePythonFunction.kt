package com.larsreimann.apiEditor.features.api.serialization

import com.larsreimann.apiEditor.features.ast.model.MutablePythonFunction
import com.larsreimann.apiEditor.features.ast.model.MutablePythonParameter
import com.larsreimann.apiEditor.features.ast.model.MutablePythonResult
import com.larsreimann.apiEditor.features.ast.model.PythonDecorator
import com.larsreimann.apiEditor.features.ast.model.PythonFunctionDocumentation
import com.larsreimann.apiEditor.features.ast.model.PythonFunctionId
import com.larsreimann.apiEditor.features.ast.model.PythonParameterId
import com.larsreimann.apiEditor.features.ast.model.PythonResultId
import kotlinx.serialization.Serializable

@Serializable
class SerializablePythonFunction(
    private val id: String,
    private val name: String,
    private val decorators: List<PythonDecorator> = emptyList(),
    private val documentation: PythonFunctionDocumentation = PythonFunctionDocumentation(),
    private val parameterIds: List<PythonParameterId> = emptyList(),
    private val resultIds: List<PythonResultId> = emptyList(),
) {
    fun toMutablePythonFunction(
        idToParameter: Map<PythonParameterId, MutablePythonParameter>,
        idToResult: Map<PythonResultId, MutablePythonResult>,
    ) = MutablePythonFunction(
        id = PythonFunctionId(id),
        name = name,
        decorators = decorators,
        documentation = documentation,
        parameters = parameterIds.mapNotNull { idToParameter[it] },
        results = resultIds.mapNotNull { idToResult[it] },
    )
}
