package com.larsreimann.apiEditor.features.api.serialization

import com.larsreimann.apiEditor.features.api.model.ApiCoordinates
import com.larsreimann.apiEditor.features.api.model.ApiStore
import com.larsreimann.apiEditor.features.ast.model.MutablePythonPackage
import com.larsreimann.apiEditor.features.ast.model.PythonPackageId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class SerializableApiStore {
    abstract fun toApiStore(): ApiStore
}

@Serializable
@SerialName("Api@v2")
class SerializableApiStoreV2(
    private val distribution: String,
    private val version: String,
    private val packageName: String,
    private val modules: List<SerializablePythonModule> = emptyList(),
    private val classes: List<SerializablePythonClass> = emptyList(),
    private val functions: List<SerializablePythonFunction> = emptyList(),
    private val parameters: List<SerializablePythonParameter> = emptyList(),
    private val results: List<SerializablePythonResult> = emptyList(),
) : SerializableApiStore() {

    override fun toApiStore(): ApiStore {
        val parameterById = parameters.map { it.toMutablePythonParameter() }.associateBy { it.id }
        val resultById = results.map { it.toMutablePythonResult() }.associateBy { it.id }
        val functionById = functions.map { it.toMutablePythonFunction(parameterById, resultById) }.associateBy { it.id }
        val classById = classes.map { it.toMutablePythonClass(functionById) }.associateBy { it.id }
        val moduleById = modules.map { it.toMutablePythonModule(classById, functionById) }.associateBy { it.id }

        // TODO
        return ApiStore(
            coordinates = ApiCoordinates(
                distribution = distribution,
                version = version,
            ),
            `package` = MutablePythonPackage(
                id = PythonPackageId(packageName),
                name = packageName,
                modules = moduleById.values.toList(),
            ),
        )
    }
}
