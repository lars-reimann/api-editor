package com.larsreimann.apiEditor.features.annotations.model

import com.larsreimann.apiEditor.features.api.serialization.SerializablePythonLiteral
import com.larsreimann.apiEditor.features.ast.model.PythonParameterId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("@Enum")
data class EnumAnnotation(
    override val target: PythonParameterId,
    val name: String,
    val instances: EnumAnnotationEnumInstance,
    override val changelog: List<AnnotationChange> = emptyList(),
    override val review: AnnotationReview? = null,
) : Annotation()

@Serializable
data class EnumAnnotationEnumInstance(
    val name: String,
    val value: SerializablePythonLiteral,
)
