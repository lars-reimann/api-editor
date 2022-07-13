package com.larsreimann.apiEditor.features.ast.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ImportsTest : FunSpec(
    {
        context("FromImport") {
            context("toString") {
                data class TestCase(
                    val fromImport: PythonFromImport,
                    val expectedString: String,
                )

                withData(
                    TestCase(
                        PythonFromImport(moduleName = "test_module", declarationName = "TestClass"),
                        "from test_module import TestClass",
                    ),
                    TestCase(
                        PythonFromImport(moduleName = "test_module", declarationName = "TestClass", alias = "TestClassAlias"),
                        "from test_module import TestClass as TestClassAlias",
                    ),
                ) { (fromImport, expectedString) ->
                    fromImport.toString() shouldBe expectedString
                }
            }
        }

        context("Import") {
            context("toString") {
                data class TestCase(
                    val `import`: PythonImport,
                    val expectedString: String,
                )

                withData(
                    TestCase(
                        PythonImport(moduleName = "test_module"),
                        "import test_module",
                    ),
                    TestCase(
                        PythonImport(moduleName = "test_module", alias = "tm"),
                        "import test_module as tm",
                    ),
                ) { (`import`, expectedString) ->
                    `import`.toString() shouldBe expectedString
                }
            }
        }
    },
)
