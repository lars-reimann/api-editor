package com.larsreimann.apiEditor.features.ast.model

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class PythonDecorator(private val name: String) {
    override fun toString() = "@$name"
}

fun String.asPythonDecorator() = PythonDecorator(this)
