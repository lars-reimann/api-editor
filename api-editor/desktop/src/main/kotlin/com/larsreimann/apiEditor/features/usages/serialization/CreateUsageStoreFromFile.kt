package com.larsreimann.apiEditor.features.usages.serialization

import com.larsreimann.apiEditor.features.usages.model.UsageStore
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.nio.file.Path
import kotlin.io.path.inputStream

private val json = Json { classDiscriminator = "\$discriminator" }

@OptIn(ExperimentalSerializationApi::class)
fun createUsageStoreFromFile(path: Path): UsageStore {
    return json
        .decodeFromStream<SerializableUsageStore>(path.inputStream())
        .toUsageStore()
}
