package com.larsreimann.apiEditor.features.api.model

import com.larsreimann.apiEditor.features.ast.model.MutablePythonPackage
import com.larsreimann.apiEditor.features.ast.model.PythonDeclaration
import com.larsreimann.apiEditor.features.ast.model.PythonDeclarationId
import com.larsreimann.apiEditor.features.ast.model.PythonPackage
import com.larsreimann.modeling.descendantsOrSelf

data class ApiStore(
    val coordinates: ApiCoordinates = ApiCoordinates(),
    val `package`: PythonPackage = MutablePythonPackage(),
) {
    inline fun <reified T : PythonDeclaration> getDeclarationByIdOrNull(id: PythonDeclarationId<T>): T? {
        return `package`
            .descendantsOrSelf()
            .filterIsInstance<T>()
            .firstOrNull { it.id == id }
    }
}

data class ApiCoordinates(
    val distribution: String = "",
    val version: String = "",
)
