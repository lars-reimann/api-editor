package com.larsreimann.apiEditor.features.api.serialization

import com.larsreimann.apiEditor.features.api.model.ApiCoordinates
import com.larsreimann.apiEditor.features.ast.model.NamedPythonType
import com.larsreimann.apiEditor.features.ast.model.PythonClassDocumentation
import com.larsreimann.apiEditor.features.ast.model.PythonClassId
import com.larsreimann.apiEditor.features.ast.model.PythonDecorator
import com.larsreimann.apiEditor.features.ast.model.PythonFromImport
import com.larsreimann.apiEditor.features.ast.model.PythonFunctionDocumentation
import com.larsreimann.apiEditor.features.ast.model.PythonFunctionId
import com.larsreimann.apiEditor.features.ast.model.PythonImport
import com.larsreimann.apiEditor.features.ast.model.PythonIntLiteral
import com.larsreimann.apiEditor.features.ast.model.PythonModuleId
import com.larsreimann.apiEditor.features.ast.model.PythonPackageId
import com.larsreimann.apiEditor.features.ast.model.PythonParameterAssignment
import com.larsreimann.apiEditor.features.ast.model.PythonParameterDocumentation
import com.larsreimann.apiEditor.features.ast.model.PythonParameterId
import com.larsreimann.apiEditor.features.ast.model.PythonResultDocumentation
import com.larsreimann.apiEditor.features.ast.model.PythonResultId
import com.larsreimann.apiEditor.features.ast.model.PythonStringLiteral
import com.larsreimann.apiEditor.testUtils.relativeResourcePathOrNull
import com.larsreimann.apiEditor.testUtils.resourcePathOrNull
import com.larsreimann.apiEditor.testUtils.walkResourceDirectory
import io.kotest.assertions.asClue
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import java.nio.file.Path

class SerializableApiStoreTest : FunSpec(
    {
        val rootName = "/api/serialization"

        context("should parse usage store JSON files") {
            val resourceNames = javaClass.walkResourceDirectory(rootName, "json")

            withData<Path>(
                { javaClass.relativeResourcePathOrNull(rootName, it).toString() },
                resourceNames,
            ) { path ->
                shouldNotThrow<SerializationException> {
                    createApiStoreFromFile(path)
                }
            }
        }

        context("should create correct API") {
            val resourceNames = listOf(
                "/api/serialization/v2/level4ApiStore.json",
            )

            withData<String>(
                { javaClass.relativeResourcePathOrNull(rootName, it).toString() },
                resourceNames,
            ) { resourceName ->
                val path = javaClass.resourcePathOrNull(resourceName).shouldNotBeNull()
                val apiStore = createApiStoreFromFile(path)

                test("coordinates") {
                    apiStore.coordinates shouldBe ApiCoordinates(
                        distribution = "test-distribution",
                        version = "1.0.0",
                    )
                }

                test("package") {
                    apiStore.`package`.asClue { `package` ->
                        `package`.id shouldBe PythonPackageId("test_package")
                        `package`.name shouldBe "test_package"
                        `package`.modules.map { it.id }.shouldContainExactly(
                            PythonModuleId("test_package/test_module"),
                        )
                    }
                }

                test("modules") {
                    apiStore.getDeclarationByIdOrNull(PythonModuleId("test_package/test_module"))
                        .shouldNotBeNull()
                        .asClue { module ->
                            module.id shouldBe PythonModuleId("test_package/test_module")
                            module.name shouldBe "test_module"
                            module.imports.shouldContainExactly(
                                PythonImport(moduleName = "test_module_2", alias = "tm2"),
                            )
                            module.fromImports.shouldContainExactly(
                                PythonFromImport(
                                    moduleName = "test_module_2",
                                    declarationName = "TestClass2",
                                    alias = "TC2",
                                ),
                            )
                            module.classes.map { it.id }.shouldContainExactly(
                                PythonClassId("test_package/test_module/TestClass"),
                            )
                            module.functions.map { it.id }.shouldContainExactly(
                                PythonFunctionId("test_package/test_module/test_function"),
                            )
                        }
                }

                test("classes") {
                    apiStore.getDeclarationByIdOrNull(PythonClassId("test_package/test_module/TestClass"))
                        .shouldNotBeNull()
                        .asClue { `class` ->
                            `class`.id shouldBe PythonClassId("test_package/test_module/TestClass")
                            `class`.name shouldBe "TestClass"
                            `class`.decorators.shouldContainExactly(
                                PythonDecorator("test_decorator"),
                            )
                            `class`.superclasses.shouldContainExactly(
                                PythonClassId("test_package/test_module_2/TestClass2"),
                            )
                            `class`.documentation shouldBe PythonClassDocumentation(description = "TestClass description")
                            `class`.methods.map { it.id }.shouldContainExactly(
                                PythonFunctionId("test_package/test_module/TestClass/test_method"),
                            )
                        }
                }

                test("methods") {
                    apiStore.getDeclarationByIdOrNull(PythonFunctionId("test_package/test_module/TestClass/test_method"))
                        .shouldNotBeNull()
                        .asClue { function ->
                            function.id shouldBe PythonFunctionId("test_package/test_module/TestClass/test_method")
                            function.name shouldBe "test_method"
                            function.decorators.shouldContainExactly(
                                PythonDecorator("test_decorator"),
                            )
                            function.documentation shouldBe PythonFunctionDocumentation(description = "test_method description")
                            function.parameters.map { it.id }.shouldContainExactly(
                                PythonParameterId("test_package/test_module/TestClass/test_method/test_parameter_2"),
                            )
                            function.results.map { it.id }.shouldContainExactly(
                                PythonResultId("test_package/test_module/TestClass/test_method/test_result_2"),
                            )
                        }
                }

                test("parameters of methods") {
                    apiStore.getDeclarationByIdOrNull(PythonParameterId("test_package/test_module/TestClass/test_method/test_parameter_2"))
                        .shouldNotBeNull()
                        .asClue { parameter ->
                            parameter.id shouldBe PythonParameterId("test_package/test_module/TestClass/test_method/test_parameter_2")
                            parameter.name shouldBe "test_parameter_2"
                            parameter.type shouldBe NamedPythonType("int")
                            parameter.defaultValue shouldBe PythonIntLiteral(1)
                            parameter.assignment shouldBe PythonParameterAssignment.PositionalOrKeyword
                            parameter.documentation shouldBe PythonParameterDocumentation(
                                type = "int",
                                defaultValue = "1",
                                description = "test_parameter_2 description",
                            )
                        }
                }

                test("results of methods") {
                    apiStore.getDeclarationByIdOrNull(PythonResultId("test_package/test_module/TestClass/test_method/test_result_2"))
                        .shouldNotBeNull()
                        .asClue { result ->
                            result.id shouldBe PythonResultId("test_package/test_module/TestClass/test_method/test_result_2")
                            result.name shouldBe "test_result_2"
                            result.type shouldBe NamedPythonType("int")
                            result.documentation shouldBe PythonResultDocumentation(
                                type = "int",
                                description = "test_result_2 description",
                            )
                        }
                }

                test("global functions") {
                    apiStore.getDeclarationByIdOrNull(PythonFunctionId("test_package/test_module/test_function"))
                        .shouldNotBeNull()
                        .asClue { function ->
                            function.id shouldBe PythonFunctionId("test_package/test_module/test_function")
                            function.name shouldBe "test_function"
                            function.decorators.shouldContainExactly(
                                PythonDecorator("test_decorator"),
                            )
                            function.documentation shouldBe PythonFunctionDocumentation(description = "test_function description")
                            function.parameters.map { it.id }.shouldContainExactly(
                                PythonParameterId("test_package/test_module/test_function/test_parameter"),
                            )
                            function.results.map { it.id }.shouldContainExactly(
                                PythonResultId("test_package/test_module/test_function/test_result"),
                            )
                        }
                }

                test("parameters of global functions") {
                    apiStore.getDeclarationByIdOrNull(PythonParameterId("test_package/test_module/test_function/test_parameter"))
                        .shouldNotBeNull()
                        .asClue { parameter ->
                            parameter.id shouldBe PythonParameterId("test_package/test_module/test_function/test_parameter")
                            parameter.name shouldBe "test_parameter"
                            parameter.type shouldBe NamedPythonType("str")
                            parameter.defaultValue shouldBe PythonStringLiteral("test_default_value")
                            parameter.assignment shouldBe PythonParameterAssignment.PositionalOnly
                            parameter.documentation shouldBe PythonParameterDocumentation(
                                type = "str",
                                defaultValue = "'test_default_value'",
                                description = "test_parameter description",
                            )
                        }
                }

                test("results of global functions") {
                    apiStore.getDeclarationByIdOrNull(PythonResultId("test_package/test_module/test_function/test_result"))
                        .shouldNotBeNull()
                        .asClue { result ->
                            result.id shouldBe PythonResultId("test_package/test_module/test_function/test_result")
                            result.name shouldBe "test_result"
                            result.type shouldBe NamedPythonType("str")
                            result.documentation shouldBe PythonResultDocumentation(
                                type = "str",
                                description = "test_result description",
                            )
                        }
                }
            }
        }
    },
)
