package com.larsreimann.apiEditor.features.api.serialization

import com.larsreimann.apiEditor.features.api.model.ApiStore
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.nio.file.Path
import kotlin.io.path.inputStream

private val json = Json { classDiscriminator = "\$discriminator" }

@OptIn(ExperimentalSerializationApi::class)
fun createApiStoreFromFile(path: Path): ApiStore {
    return json
        .decodeFromStream<SerializableApiStore>(path.inputStream())
        .toApiStore()
}
