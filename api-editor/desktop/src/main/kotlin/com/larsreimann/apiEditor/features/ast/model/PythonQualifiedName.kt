package com.larsreimann.apiEditor.features.ast.model

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class PythonQualifiedName(private val value: String) {
    override fun toString() = value
}

fun String.asPythonQualifiedName() = PythonQualifiedName(this)
