package com.larsreimann.apiEditor.features.annotations.serialization

import com.larsreimann.apiEditor.testUtils.relativeResourcePathOrNull
import com.larsreimann.apiEditor.testUtils.walkResourceDirectory
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import kotlinx.serialization.SerializationException
import java.nio.file.Path

class SerializableAnnotationStoreTest : FunSpec(
    {
        val rootName = "/annotations/serialization"

        context("should parse usage store JSON files") {
            val resourceNames = javaClass.walkResourceDirectory(rootName, "json")

            withData<Path>(
                { javaClass.relativeResourcePathOrNull(rootName, it).toString() },
                resourceNames,
            ) { path ->
                shouldNotThrow<SerializationException> {
                    createAnnotationStoreFromFile(path)
                }
            }
        }

        context("should create correct annotations") {
            // TODO
        }
    },
)
