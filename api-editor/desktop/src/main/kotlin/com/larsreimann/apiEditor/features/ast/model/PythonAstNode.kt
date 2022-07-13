package com.larsreimann.apiEditor.features.ast.model

import com.larsreimann.modeling.ModelNode
import com.larsreimann.modeling.Traversable

sealed interface PythonAstNode : Traversable

sealed class MutablePythonAstNode : ModelNode(), PythonAstNode
