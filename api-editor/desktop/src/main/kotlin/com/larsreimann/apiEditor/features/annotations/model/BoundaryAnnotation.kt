package com.larsreimann.apiEditor.features.annotations.model

import com.larsreimann.apiEditor.features.ast.model.PythonParameterId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("@Boundary")
data class BoundaryAnnotation(
    override val target: PythonParameterId,
    val baseType: BoundaryAnnotationBaseType,
    val lowerBound: BoundaryAnnotationBound,
    val upperBound: BoundaryAnnotationBound,
    override val changelog: List<AnnotationChange> = emptyList(),
    override val review: AnnotationReview? = null,
) : Annotation()

@Serializable
enum class BoundaryAnnotationBaseType {
    @SerialName("float")
    Float,

    @SerialName("int")
    Int,
}

@Serializable
sealed class BoundaryAnnotationBound

@Serializable
@SerialName("ExclusiveBound")
data class BoundaryAnnotationExclusiveBound(val value: Double) : BoundaryAnnotationBound()

@Serializable
@SerialName("InclusiveBound")
data class BoundaryAnnotationInclusiveBound(val value: Double) : BoundaryAnnotationBound()

@Serializable
@SerialName("Unbounded")
object BoundaryAnnotationUnbounded : BoundaryAnnotationBound()
