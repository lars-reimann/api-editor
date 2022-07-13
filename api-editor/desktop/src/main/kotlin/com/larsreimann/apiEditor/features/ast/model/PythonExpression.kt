package com.larsreimann.apiEditor.features.ast.model

sealed interface PythonExpression : PythonAstNode

sealed class MutablePythonExpression : MutablePythonAstNode(), PythonExpression
