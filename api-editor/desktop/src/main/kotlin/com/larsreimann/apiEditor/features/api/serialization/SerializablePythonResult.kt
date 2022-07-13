package com.larsreimann.apiEditor.features.api.serialization

import com.larsreimann.apiEditor.features.ast.model.MutablePythonResult
import com.larsreimann.apiEditor.features.ast.model.PythonResultDocumentation
import com.larsreimann.apiEditor.features.ast.model.PythonResultId
import com.larsreimann.apiEditor.features.ast.model.PythonType
import kotlinx.serialization.Serializable

@Serializable
class SerializablePythonResult(
    private val id: String,
    private val name: String,
    private val type: PythonType? = null,
    private val documentation: PythonResultDocumentation = PythonResultDocumentation(),
) {
    fun toMutablePythonResult() = MutablePythonResult(
        id = PythonResultId(id),
        name = name,
        type = type,
        documentation = documentation,
    )
}
