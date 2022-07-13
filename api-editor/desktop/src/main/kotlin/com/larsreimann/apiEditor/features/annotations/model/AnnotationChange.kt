package com.larsreimann.apiEditor.features.annotations.model

import com.larsreimann.apiEditor.features.users.model.Username
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class AnnotationChange(

    /**
     * Who made the change.
     */
    val author: Username,

    /**
     * When the change was made.
     */
    val instant: Instant,

    /**
     * Additional notes about the change.
     */
    val comment: String = "",
)
