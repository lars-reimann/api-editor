package com.larsreimann.apiEditor.features.annotations.model

import com.larsreimann.apiEditor.features.users.model.Username
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class AnnotationReview(

    /**
     * Who reviewed the annotation.
     */
    val reviewer: Username,

    /**
     * When the review was done.
     */
    val instant: Instant,

    /**
     * The general conclusion of the review.
     */
    val result: AnnotationReviewResult,

    /**
     * Additional notes about the review.
     */
    val comment: String = "",
)

/**
 * The general conclusion of an [AnnotationReview].
 */
enum class AnnotationReviewResult {

    /**
     * The annotation is correct and does not require changes.
     */
    Approved,

    /**
     * The reviewer cannot decide whether the annotation is correct or not and needs assistance.
     */
    Unsure,

    /**
     * The annotation is wrong and should not be processed further until it's changed.
     */
    Rejected
}
