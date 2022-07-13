package com.larsreimann.apiEditor.features.ast.model

interface PythonReference : PythonExpression {
    val declaration: PythonDeclaration?

    override fun children() = emptySequence<PythonAstNode>()
}

class MutablePythonReference(declaration: MutablePythonDeclaration?) : MutablePythonExpression(), PythonReference {
    override var declaration by CrossReference(declaration)
}
