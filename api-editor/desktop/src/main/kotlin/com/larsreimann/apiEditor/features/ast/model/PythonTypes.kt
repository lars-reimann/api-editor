package com.larsreimann.apiEditor.features.ast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PythonType

@Serializable
@SerialName("NamedType")
data class NamedPythonType(private val name: String) : PythonType()
