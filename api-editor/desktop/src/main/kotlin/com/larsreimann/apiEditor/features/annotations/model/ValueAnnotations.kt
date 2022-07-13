package com.larsreimann.apiEditor.features.annotations.model

import com.larsreimann.apiEditor.features.api.serialization.SerializablePythonLiteral
import com.larsreimann.apiEditor.features.ast.model.PythonParameterId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ValueAnnotation : Annotation()

@Serializable
@SerialName("@Constant")
data class ConstantAnnotation(
    override val target: PythonParameterId,
    val value: SerializablePythonLiteral,
    override val changelog: List<AnnotationChange> = emptyList(),
    override val review: AnnotationReview? = null,
) : ValueAnnotation()

@Serializable
@SerialName("@Omitted")
data class OmittedAnnotation(
    override val target: PythonParameterId,
    override val changelog: List<AnnotationChange> = emptyList(),
    override val review: AnnotationReview? = null,
) : ValueAnnotation()

@Serializable
@SerialName("@Optional")
data class OptionalAnnotation(
    override val target: PythonParameterId,
    val defaultValue: SerializablePythonLiteral,
    override val changelog: List<AnnotationChange> = emptyList(),
    override val review: AnnotationReview? = null,
) : ValueAnnotation()

@Serializable
@SerialName("@Required")
data class RequiredAnnotation(
    override val target: PythonParameterId,
    override val changelog: List<AnnotationChange> = emptyList(),
    override val review: AnnotationReview? = null,
) : ValueAnnotation()
