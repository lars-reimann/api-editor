package com.larsreimann.api_editor.codegen

import com.larsreimann.api_editor.model.AnnotatedPythonClass
import com.larsreimann.api_editor.model.AnnotatedPythonFunction
import com.larsreimann.api_editor.model.AnnotatedPythonModule
import com.larsreimann.api_editor.model.AnnotatedPythonParameter
import com.larsreimann.api_editor.model.AnnotatedPythonResult
import com.larsreimann.api_editor.model.PythonFromImport
import com.larsreimann.api_editor.model.PythonImport
import com.larsreimann.api_editor.model.PythonParameterAssignment
import de.unibonn.simpleml.SimpleMLStandaloneSetup
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CompilationUnitStubContentBuilderTest {

    @BeforeEach
    fun initSimpleML() {
        SimpleMLStandaloneSetup.doSetup()
    }

    @Test
    fun buildModuleContentReturnsFormattedModuleContent() {
        // given
        val testClass = AnnotatedPythonClass(
            "testClass",
            "testModule.testClass",
            listOf("testDecorator"),
            listOf("testSuperclass"),
            listOf(
                AnnotatedPythonFunction(
                    "testClassFunction",
                    "testModule.testClass.testClassFunction",
                    listOf("decorators"),
                    listOf(
                        AnnotatedPythonParameter(
                            "onlyParam",
                            "testModule.testClass.testClassFunction.onlyParam",
                            "'defaultValue'",
                            PythonParameterAssignment.POSITION_OR_NAME,
                            true,
                            "typeInDocs",
                            "description",
                            mutableListOf()
                        )
                    ),
                    emptyList(),
                    true,
                    "description",
                    "fullDocstring",
                    mutableListOf()
                ),
                AnnotatedPythonFunction(
                    "__init__",
                    "testModule.testClass.__init__",
                    listOf("decorators"),
                    listOf(
                        AnnotatedPythonParameter(
                            "onlyParam",
                            "testModule.testClass.__init__.onlyParam",
                            "'defaultValue'",
                            PythonParameterAssignment.POSITION_OR_NAME,
                            true,
                            "typeInDocs",
                            "description", mutableListOf()
                        )
                    ),
                    emptyList(),
                    true,
                    "description",
                    "fullDocstring", mutableListOf()
                )
            ),
            true,
            "Lorem ipsum",
            "Lorem ipsum", mutableListOf()
        )
        val testModule = AnnotatedPythonModule(
            "testModule", emptyList(), emptyList(),
            listOf(
                testClass
            ),
            listOf(
                AnnotatedPythonFunction(
                    "function_module_1",
                    "test.module_1.function_module_1",
                    listOf("testDecorator"),
                    listOf(
                        AnnotatedPythonParameter(
                            "param1",
                            "test.module_1.function_module_1.param1",
                            null,
                            PythonParameterAssignment.NAME_ONLY,
                            true,
                            "str",
                            "Lorem ipsum", mutableListOf()
                        ),
                        AnnotatedPythonParameter(
                            "param2",
                            "test.module_1.function_module_1.param2",
                            null,
                            PythonParameterAssignment.NAME_ONLY,
                            true,
                            "str",
                            "Lorem ipsum", mutableListOf()
                        ),
                        AnnotatedPythonParameter(
                            "param3",
                            "test.module_1.function_module_1.param3",
                            null,
                            PythonParameterAssignment.NAME_ONLY,
                            true,
                            "str",
                            "Lorem ipsum", mutableListOf()
                        )
                    ),
                    listOf(
                        AnnotatedPythonResult(
                            "testResult",
                            "str",
                            "str",
                            "Lorem ipsum", mutableListOf()
                        )
                    ),
                    true,
                    "Lorem ipsum",
                    "Lorem ipsum", mutableListOf()
                ),
                AnnotatedPythonFunction(
                    "testFunction",
                    "testModule.testFunction",
                    listOf("testDecorator"),
                    listOf(
                        AnnotatedPythonParameter(
                            "testParameter",
                            "testModule.testFunction.testParameter",
                            "42",
                            PythonParameterAssignment.NAME_ONLY,
                            true,
                            "int",
                            "Lorem ipsum", mutableListOf()
                        )
                    ),
                    listOf(
                        AnnotatedPythonResult(
                            "testResult",
                            "str",
                            "str",
                            "Lorem ipsum", mutableListOf()
                        )
                    ),
                    true,
                    "Lorem ipsum",
                    "Lorem ipsum", mutableListOf()
                )
            ),
            mutableListOf()
        )

        // when
        val moduleContent = buildCompilationUnitToString(testModule)

        // then
        val expectedModuleContent: String = """
            |package simpleml.testModule
            |
            |@Description("Lorem ipsum")
            |class testClass(@Description("description") onlyParam: Any? or "defaultValue") {
            |    @Description("description")
            |    attr onlyParam: Any?
            |
            |    @Description("description")
            |    fun testClassFunction(@Description("description") onlyParam: Any? or "defaultValue")
            |}
            |
            |@Description("Lorem ipsum")
            |fun function_module_1(@Description("Lorem ipsum") param1: String, @Description("Lorem ipsum") param2: String, @Description("Lorem ipsum") param3: String) -> @Description("Lorem ipsum") testResult: String
            |
            |@Description("Lorem ipsum")
            |fun testFunction(@Description("Lorem ipsum") testParameter: Int or 42) -> @Description("Lorem ipsum") testResult: String
            |""".trimMargin()
        Assertions.assertEquals(expectedModuleContent, moduleContent)
    }

    @Test
    fun buildModuleContentWithNoClassesReturnsFormattedModuleContent() {
        // given
        val testModule = AnnotatedPythonModule(
            "testModule", emptyList(), emptyList(), emptyList(),
            listOf(
                AnnotatedPythonFunction(
                    "function_module_1",
                    "test.module_1.function_module_1",
                    listOf("testDecorator"),
                    listOf(
                        AnnotatedPythonParameter(
                            "param1",
                            "test.module_1.function_module_1.param1",
                            null,
                            PythonParameterAssignment.NAME_ONLY,
                            true,
                            "str",
                            "Lorem ipsum", mutableListOf()
                        ),
                        AnnotatedPythonParameter(
                            "param2",
                            "test.module_1.function_module_1.param2",
                            null,
                            PythonParameterAssignment.NAME_ONLY,
                            true,
                            "str",
                            "Lorem ipsum", mutableListOf()
                        ),
                        AnnotatedPythonParameter(
                            "param3",
                            "test.module_1.function_module_1.param3",
                            null,
                            PythonParameterAssignment.NAME_ONLY,
                            true,
                            "str",
                            "Lorem ipsum", mutableListOf()
                        )
                    ),
                    listOf(
                        AnnotatedPythonResult(
                            "testResult",
                            "str",
                            "str",
                            "Lorem ipsum", mutableListOf()
                        )
                    ),
                    true,
                    "Lorem ipsum",
                    "Lorem ipsum", mutableListOf()
                ),
                AnnotatedPythonFunction(
                    "testFunction",
                    "testModule.testFunction",
                    listOf("testDecorator"),
                    listOf(
                        AnnotatedPythonParameter(
                            "testParameter",
                            "testModule.testFunction.testParameter",
                            "42",
                            PythonParameterAssignment.NAME_ONLY,
                            true,
                            "int",
                            "Lorem ipsum", mutableListOf()
                        )
                    ),
                    listOf(
                        AnnotatedPythonResult(
                            "testResult",
                            "str",
                            "str",
                            "Lorem ipsum", mutableListOf()
                        )
                    ),
                    true,
                    "Lorem ipsum",
                    "Lorem ipsum", mutableListOf()
                )
            ),
            mutableListOf()
        )

        // when
        val moduleContent = buildCompilationUnitToString(testModule)

        // then
        val expectedModuleContent: String = """
            |package simpleml.testModule
            |
            |@Description("Lorem ipsum")
            |fun function_module_1(@Description("Lorem ipsum") param1: String, @Description("Lorem ipsum") param2: String, @Description("Lorem ipsum") param3: String) -> @Description("Lorem ipsum") testResult: String
            |
            |@Description("Lorem ipsum")
            |fun testFunction(@Description("Lorem ipsum") testParameter: Int or 42) -> @Description("Lorem ipsum") testResult: String
            |""".trimMargin()
        Assertions.assertEquals(expectedModuleContent, moduleContent)
    }

    @Test
    fun buildModuleContentWithOnlyConstructorReturnsFormattedModuleContent() {
        // given
        val testClass = AnnotatedPythonClass(
            "testClass",
            "testModule.testClass",
            listOf("testDecorator"),
            listOf("testSuperclass"),
            listOf(
                AnnotatedPythonFunction(
                    "__init__",
                    "testModule.testClass.__init__",
                    listOf("decorators"),
                    listOf(
                        AnnotatedPythonParameter(
                            "onlyParam",
                            "testModule.testClass.__init__.onlyParam",
                            "'defaultValue'",
                            PythonParameterAssignment.POSITION_OR_NAME,
                            true,
                            "typeInDocs",
                            "description", mutableListOf()
                        )
                    ),
                    emptyList(),
                    true,
                    "description",
                    "fullDocstring", mutableListOf()
                )
            ),
            true,
            "Lorem ipsum",
            "Lorem ipsum", mutableListOf()
        )
        val testModule = AnnotatedPythonModule(
            "testModule",
            listOf(
                PythonImport(
                    "testImport1",
                    "testAlias"
                )
            ),
            listOf(
                PythonFromImport(
                    "testFromImport1",
                    "testDeclaration1",
                    null
                )
            ),
            listOf(
                testClass
            ),
            emptyList(), mutableListOf()
        )

        // when
        val moduleContent = buildCompilationUnitToString(testModule)

        // then
        val expectedModuleContent: String = """
            |package simpleml.testModule
            |
            |@Description("Lorem ipsum")
            |class testClass(@Description("description") onlyParam: Any? or "defaultValue") {
            |    @Description("description")
            |    attr onlyParam: Any?
            |}
            |""".trimMargin()
        Assertions.assertEquals(expectedModuleContent, moduleContent)
    }

    @Test
    fun buildModuleContentWithNoFunctionsAndClassesReturnsFormattedModuleContent() {
        // given
        val testModule = AnnotatedPythonModule(
            "testModule", emptyList(), emptyList(), emptyList(), emptyList(), mutableListOf()
        )

        // when
        val moduleContent = buildCompilationUnitToString(testModule)

        // then
        val expectedModuleContent: String = """
            |package simpleml.testModule
            |""".trimMargin()
        Assertions.assertEquals(expectedModuleContent, moduleContent)
    }
}