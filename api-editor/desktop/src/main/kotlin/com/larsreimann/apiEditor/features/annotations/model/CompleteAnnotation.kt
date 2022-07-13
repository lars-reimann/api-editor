package com.larsreimann.apiEditor.features.annotations.model

import com.larsreimann.apiEditor.features.ast.model.PythonDeclarationId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("@Complete")
data class CompleteAnnotation(
    override val target: PythonDeclarationId<*>,
    override val changelog: List<AnnotationChange> = emptyList(),
    override val review: AnnotationReview? = null,
) : Annotation()
