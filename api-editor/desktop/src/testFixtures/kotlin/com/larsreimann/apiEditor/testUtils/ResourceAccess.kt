package com.larsreimann.apiEditor.testUtils

import java.nio.file.Path
import kotlin.io.path.toPath

/**
 * Returns a [Sequence] of [Path]s to the files with the given [extension] inside the directory with name [rootName].
 * [extension] should not include a leading dot. This function also visits files inside nested directories.
 */
fun <T> Class<T>.walkResourceDirectory(rootName: String, extension: String): Sequence<Path> {
    return resourcePathOrNull(rootName)
        ?.toFile()
        ?.walk()
        ?.filter { it.isFile && it.extension == extension }
        ?.map { it.toPath() }
        ?: emptySequence()
}

/**
 * Returns the [Path] to the resource with the given [name] or `null` if the resource does not exist.
 */
fun <T> Class<T>.resourcePathOrNull(name: String): Path? {
    return this.getResource(name)
        ?.toURI()
        ?.toPath()
}

/**
 * Returns the relative [Path] from the resource directory named [rootName] to the resource named [resourceName].
 */
fun <T> Class<T>.relativeResourcePathOrNull(rootName: String, resourceName: String): Path? {
    val rootPath = resourcePathOrNull(rootName) ?: return null
    val resourcePath = resourcePathOrNull(resourceName) ?: return null

    return rootPath.relativize(resourcePath)
}

/**
 * Returns the relative [Path] from the resource directory named [rootName] to the resource with path [resourcePath].
 */
fun <T> Class<T>.relativeResourcePathOrNull(rootName: String, resourcePath: Path): Path? {
    return resourcePathOrNull(rootName)?.relativize(resourcePath)
}
