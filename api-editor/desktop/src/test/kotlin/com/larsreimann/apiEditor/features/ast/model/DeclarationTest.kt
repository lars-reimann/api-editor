package com.larsreimann.apiEditor.features.ast.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class DeclarationTest : FunSpec(
    {
        context("qualifiedName") {
            data class TestCase(
                val declaration: PythonDeclaration,
                val expectedQualifiedName: String,
            ) : WithDataTestName {
                override fun dataTestName(): String {
                    return "${declaration::class.simpleName} \"${declaration.name}\" should have qualified name \"$expectedQualifiedName\""
                }
            }

            val testParameter = MutablePythonParameter(name = "test_parameter")
            val testFunction = MutablePythonFunction(name = "test_function", parameters = listOf(testParameter))
            val testClass = MutablePythonClass(name = "TestClass", methods = listOf(testFunction))
            val testModule = MutablePythonModule(name = "test_module", classes = listOf(testClass))
            val testPackage = MutablePythonPackage(name = "testPackage", modules = listOf(testModule))

            withData(
                TestCase(testParameter, "test_module.TestClass.test_function.test_parameter"),
                TestCase(testFunction, "test_module.TestClass.test_function"),
                TestCase(testClass, "test_module.TestClass"),
                TestCase(testModule, "test_module"),
                TestCase(testPackage, ""),
            ) { (declaration, expectedQualifiedName) ->
                declaration.qualifiedName shouldBe PythonQualifiedName(expectedQualifiedName)
            }
        }
    },
)
