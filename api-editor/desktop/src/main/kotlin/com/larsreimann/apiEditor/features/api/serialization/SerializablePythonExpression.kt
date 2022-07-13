package com.larsreimann.apiEditor.features.api.serialization

import com.larsreimann.apiEditor.features.ast.model.PythonBooleanLiteral
import com.larsreimann.apiEditor.features.ast.model.PythonExpression
import com.larsreimann.apiEditor.features.ast.model.PythonFloatLiteral
import com.larsreimann.apiEditor.features.ast.model.PythonIntLiteral
import com.larsreimann.apiEditor.features.ast.model.PythonNoneLiteral
import com.larsreimann.apiEditor.features.ast.model.PythonStringLiteral
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class SerializablePythonExpression {
    abstract fun toPythonExpression(): PythonExpression
}

@Serializable
sealed class SerializablePythonLiteral : SerializablePythonExpression()

@Serializable
@SerialName("BooleanLiteral")
data class SerializablePythonBooleanLiteral(private val value: Boolean) : SerializablePythonLiteral() {
    override fun toPythonExpression() = PythonBooleanLiteral(value)
}

@Serializable
@SerialName("FloatLiteral")
data class SerializablePythonFloatLiteral(private val value: Double) : SerializablePythonLiteral() {
    override fun toPythonExpression() = PythonFloatLiteral(value)
}

@Serializable
@SerialName("IntLiteral")
data class SerializablePythonIntLiteral(private val value: Int) : SerializablePythonLiteral() {
    override fun toPythonExpression() = PythonIntLiteral(value)
}

@Serializable
@SerialName("NoneLiteral")
object SerializablePythonNoneLiteral : SerializablePythonLiteral() {
    override fun toPythonExpression() = PythonNoneLiteral()
}

@Serializable
@SerialName("StringLiteral")
data class SerializablePythonStringLiteral(private val value: String) : SerializablePythonLiteral() {
    override fun toPythonExpression() = PythonStringLiteral(value)
}
