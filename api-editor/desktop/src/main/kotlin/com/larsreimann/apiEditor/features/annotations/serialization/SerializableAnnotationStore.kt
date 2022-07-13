package com.larsreimann.apiEditor.features.annotations.serialization

import com.larsreimann.apiEditor.features.annotations.model.AnnotationStore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class SerializableAnnotationStore {
    abstract fun toAnnotationStore(): AnnotationStore
}

@Serializable
@SerialName("Annotations@v2")
class SerializableAnnotationStoreV2 : SerializableAnnotationStore() {
    override fun toAnnotationStore(): AnnotationStore {
        // TODO
        return AnnotationStore()
    }
}
