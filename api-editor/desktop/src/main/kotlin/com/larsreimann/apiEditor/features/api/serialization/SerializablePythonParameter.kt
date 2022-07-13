package com.larsreimann.apiEditor.features.api.serialization

import com.larsreimann.apiEditor.features.ast.model.MutablePythonParameter
import com.larsreimann.apiEditor.features.ast.model.PythonParameterAssignment
import com.larsreimann.apiEditor.features.ast.model.PythonParameterDocumentation
import com.larsreimann.apiEditor.features.ast.model.PythonParameterId
import com.larsreimann.apiEditor.features.ast.model.PythonType
import kotlinx.serialization.Serializable

@Serializable
class SerializablePythonParameter(
    private val id: String,
    private val name: String,
    private val type: PythonType? = null,
    private val defaultValue: SerializablePythonExpression? = null,
    private val assignment: PythonParameterAssignment = PythonParameterAssignment.PositionalOrKeyword,
    private val documentation: PythonParameterDocumentation = PythonParameterDocumentation(),
) {
    fun toMutablePythonParameter() = MutablePythonParameter(
        id = PythonParameterId(id),
        name = name,
        type = type,
        defaultValue = defaultValue?.toPythonExpression(),
        assignment = assignment,
        documentation = documentation,
    )
}
