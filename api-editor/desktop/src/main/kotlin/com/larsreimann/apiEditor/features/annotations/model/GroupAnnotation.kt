package com.larsreimann.apiEditor.features.annotations.model

import com.larsreimann.apiEditor.features.ast.model.PythonFunctionId
import com.larsreimann.apiEditor.features.ast.model.PythonParameterId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("@Group")
data class GroupAnnotation(
    override val target: PythonFunctionId,
    val name: String,
    val parameters: List<PythonParameterId>,
    override val changelog: List<AnnotationChange> = emptyList(),
    override val review: AnnotationReview? = null,
) : Annotation()
