package com.larsreimann.apiEditor.codegen

import com.larsreimann.apiEditor.mutableModel.PythonModule
import com.larsreimann.apiEditor.mutableModel.PythonPackage
import com.larsreimann.safeds.constant.SdsFileExtension
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile

fun PythonPackage.generateCode(): File {
    val workingDirectory = createTempDirectory(prefix = "api-editor_inferredAPI").toFile()
    val zipFile = createTempFile(prefix = "api-editor_inferredAPI", suffix = ".zip").toFile()

    for (module in modules) {
        try {
            createPythonFile(workingDirectory, module)
            createStubFile(workingDirectory, module)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    createZipArchive(workingDirectory, zipFile)
    workingDirectory.deleteRecursively()

    return zipFile
}

private fun createPythonFile(workingDirectory: File, module: PythonModule) {
    workingDirectory
        .resolve("adapter")
        .resolve(module.name.replace('.', '/') + ".py")
        .createFile(module.toPythonCode())
}

private fun createStubFile(workingDirectory: File, module: PythonModule) {
    workingDirectory
        .resolve("stub")
        .resolve(module.name.replace('.', '/'))
        .resolve(module.name.split(".").last() + "." + SdsFileExtension.Stub)
        .createFile(module.toStubCode())
}

private fun File.createFile(content: String) {
    parentFile.mkdirs()
    createNewFile()
    bufferedWriter().use {
        it.write(content)
        it.flush()
    }
}

private fun createZipArchive(source: File, destination: File) {
    destination.zipOutputStream().use { outputStream ->
        source.recursivelyListFiles().forEach { file ->
            file.bufferedInputStream().use { inputStream ->
                outputStream.putNextEntry(ZipEntry(file.toRelativeString(source)))
                inputStream.copyTo(outputStream)
            }
        }
    }
}

private fun File.recursivelyListFiles() = walk().filter { it.isFile }
private fun File.zipOutputStream() = ZipOutputStream(this.bufferedOutputStream())
private fun File.bufferedOutputStream() = BufferedOutputStream(this.outputStream())
private fun File.bufferedInputStream() = BufferedInputStream(this.inputStream())
