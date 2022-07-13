package com.larsreimann.apiEditor.features.ast.model

import kotlinx.serialization.Serializable

@Serializable
data class PythonFromImport(val moduleName: String, val declarationName: String, val alias: String? = null) {
    override fun toString(): String {
        return when (alias) {
            null -> "from $moduleName import $declarationName"
            else -> "from $moduleName import $declarationName as $alias"
        }
    }
}

@Serializable
data class PythonImport(val moduleName: String, val alias: String? = null) {
    override fun toString(): String {
        return when (alias) {
            null -> "import $moduleName"
            else -> "import $moduleName as $alias"
        }
    }
}
