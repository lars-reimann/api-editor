package com.larsreimann.apiEditor.features.annotations.serialization

import com.larsreimann.apiEditor.features.annotations.model.AnnotationStore
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.nio.file.Path
import kotlin.io.path.inputStream

private val json = Json { classDiscriminator = "\$discriminator" }

@OptIn(ExperimentalSerializationApi::class)
fun createAnnotationStoreFromFile(path: Path): AnnotationStore {
    return json
        .decodeFromStream<SerializableAnnotationStore>(path.inputStream())
        .toAnnotationStore()
}
