package com.larsreimann.apiEditor.features.annotations.model

import com.larsreimann.apiEditor.features.ast.model.PythonDeclarationId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class PurityAnnotation : Annotation()

@Serializable
@SerialName("@NoSideEffects")
data class NoSideEffectsAnnotation(
    override val target: PythonDeclarationId<*>,
    override val changelog: List<AnnotationChange> = emptyList(),
    override val review: AnnotationReview? = null,
) : PurityAnnotation()

@Serializable
@SerialName("@Pure")
data class PureAnnotation(
    override val target: PythonDeclarationId<*>,
    override val changelog: List<AnnotationChange> = emptyList(),
    override val review: AnnotationReview? = null,
) : PurityAnnotation()
