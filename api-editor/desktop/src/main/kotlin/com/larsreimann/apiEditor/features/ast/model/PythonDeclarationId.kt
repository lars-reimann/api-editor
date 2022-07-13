package com.larsreimann.apiEditor.features.ast.model

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
@Suppress("unused")
value class PythonDeclarationId<out T : PythonDeclaration>(private val value: String) {
    override fun toString() = value
}

fun <T : PythonDeclaration> String.asPythonDeclarationId(): PythonDeclarationId<T> = PythonDeclarationId(this)
