package com.larsreimann.apiEditor.features.api.model

import com.larsreimann.apiEditor.features.ast.model.MutablePythonClass
import com.larsreimann.apiEditor.features.ast.model.MutablePythonFunction
import com.larsreimann.apiEditor.features.ast.model.MutablePythonModule
import com.larsreimann.apiEditor.features.ast.model.MutablePythonPackage
import com.larsreimann.apiEditor.features.ast.model.MutablePythonParameter
import com.larsreimann.apiEditor.features.ast.model.PythonDeclaration
import com.larsreimann.apiEditor.features.ast.model.PythonDeclarationId
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ApiStoreTest : FunSpec(
    {
        val testParameter = MutablePythonParameter(
            id = PythonDeclarationId("test_parameter"),
        )
        val testMethod = MutablePythonFunction(
            id = PythonDeclarationId("test_method"),
            parameters = listOf(testParameter),
        )
        val testClass = MutablePythonClass(
            id = PythonDeclarationId("TestClass"),
            methods = listOf(testMethod),
        )
        val testModule = MutablePythonModule(
            id = PythonDeclarationId("test_module"),
            classes = listOf(testClass),
        )
        val testPackage = MutablePythonPackage(
            id = PythonDeclarationId("test_package"),
            modules = listOf(testModule),
        )
        val apiStore = ApiStore(
            `package` = testPackage,
        )

        context("getDeclarationByIdOrNull") {
            data class TestCase<T : PythonDeclaration>(
                val id: PythonDeclarationId<T>,
                val expectedDeclaration: T?,
            ) : WithDataTestName {
                override fun dataTestName(): String {
                    return "id = \"$id\""
                }
            }

            withData(
                TestCase(testParameter.id, testParameter),
                TestCase(testMethod.id, testMethod),
                TestCase(testClass.id, testClass),
                TestCase(testModule.id, testModule),
                TestCase(testPackage.id, testPackage),
                TestCase(PythonDeclarationId(""), null),
            ) { (id, expectedDeclaration) ->
                apiStore.getDeclarationByIdOrNull(id) shouldBe expectedDeclaration
            }
        }
    },
)
