package com.larsreimann.apiEditor.features.ast.model

sealed interface PythonLiteral : PythonExpression {
    override fun children() = emptySequence<PythonExpression>()
}

data class PythonBooleanLiteral(private val value: Boolean) : MutablePythonExpression(), PythonLiteral

data class PythonFloatLiteral(private val value: Double) : MutablePythonExpression(), PythonLiteral

data class PythonIntLiteral(private val value: Int) : MutablePythonExpression(), PythonLiteral

data class PythonNoneLiteral(private val value: Nothing? = null) : MutablePythonExpression(), PythonLiteral

data class PythonStringLiteral(private val value: String) : MutablePythonExpression(), PythonLiteral
