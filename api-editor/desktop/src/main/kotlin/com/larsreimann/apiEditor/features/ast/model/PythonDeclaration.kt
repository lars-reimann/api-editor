package com.larsreimann.apiEditor.features.ast.model

import com.larsreimann.modeling.ancestorsOrSelf

/**
 * A named [PythonAstNode].
 */
sealed interface PythonDeclaration : PythonAstNode {

    /**
     * A unique identifier for this declaration.
     */
    val id: PythonDeclarationId<*>

    /**
     * The name of this declaration.
     */
    val name: String

    /**
     * The qualified name of this declaration.
     */
    val qualifiedName: PythonQualifiedName
        get() {
            return ancestorsOrSelf()
                .filterIsInstance<PythonDeclaration>()
                .filterNot { it is PythonPackage }
                .asIterable()
                .reversed()
                .joinToString(".") { it.name }
                .asPythonQualifiedName()
        }
}

sealed class MutablePythonDeclaration : MutablePythonAstNode(), PythonDeclaration
