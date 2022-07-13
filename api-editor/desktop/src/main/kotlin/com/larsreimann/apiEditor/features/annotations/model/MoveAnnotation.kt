package com.larsreimann.apiEditor.features.annotations.model

import com.larsreimann.apiEditor.features.ast.model.PythonDeclarationId
import com.larsreimann.apiEditor.features.ast.model.PythonQualifiedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("@Move")
data class MoveAnnotation(
    override val target: PythonDeclarationId<*>,
    val destination: PythonQualifiedName,
    override val changelog: List<AnnotationChange> = emptyList(),
    override val review: AnnotationReview? = null,
) : Annotation()
