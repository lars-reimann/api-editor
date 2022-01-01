package com.larsreimann.api_editor.codegen

import com.larsreimann.api_editor.model.AnnotatedPythonClass
import com.larsreimann.api_editor.model.AnnotatedPythonFunction
import com.larsreimann.api_editor.model.AnnotatedPythonParameter
import com.larsreimann.api_editor.model.AttributeAnnotation
import com.larsreimann.api_editor.model.DefaultString
import com.larsreimann.api_editor.model.PythonParameterAssignment
import de.unibonn.simpleml.SimpleMLStandaloneSetup
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ClassStubContentBuilderTest {

    @BeforeEach
    fun initSimpleML() {
        SimpleMLStandaloneSetup.doSetup()
    }

    @Test
    fun buildClassReturnsFormattedClassWithNoConstructorAndFunctions() {
        // given
        val testClass = AnnotatedPythonClass(
            "TestClass",
            "testModule.TestClass",
            listOf("TestDecorator"),
            listOf("TestSuperclass"), emptyList(),
            true,
            "Lorem ipsum",
            "Lorem ipsum", mutableListOf()
        )

        // when
        val formattedClass = buildClassToString(testClass)

        // then
        val expectedFormattedClass = """
            |@Description("Lorem ipsum")
            |class TestClass()
        """.trimMargin()
        Assertions.assertEquals(expectedFormattedClass, formattedClass)
    }

    @Test
    fun buildClassReturnsFormattedClassWithOneFunctionAndNoConstructor() {
        // given
        val testClass = AnnotatedPythonClass(
            "TestClass",
            "testModule.TestClass",
            listOf("TestDecorator"),
            listOf("TestSuperclass"),
            listOf(
                AnnotatedPythonFunction(
                    "testClassFunction",
                    "testModule.TestClass.testClassFunction",
                    listOf("decorators"),
                    listOf(
                        AnnotatedPythonParameter(
                            "onlyParam",
                            "testModule.TestClass.testClassFunction.onlyParam",
                            "'defaultValue'",
                            PythonParameterAssignment.POSITION_OR_NAME,
                            true,
                            "str",
                            "description",
                            mutableListOf(
                                AttributeAnnotation(
                                    DefaultString("test")
                                )
                            )
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

        // when
        val formattedClass = buildClassToString(testClass)

        // then
        val expectedFormattedClass: String = """
            |@Description("Lorem ipsum")
            |class TestClass() {
            |    @Description("description")
            |    fun testClassFunction(@Description("description") onlyParam: String or "defaultValue")
            |}""".trimMargin()

        formattedClass shouldBe expectedFormattedClass
    }

    @Test
    fun buildClassReturnsFormattedClassWithConstructorAndOneFunction() {
        // given
        val testClass = AnnotatedPythonClass(
            "TestClass",
            "testModule.TestClass",
            listOf("TestDecorator"),
            listOf("TestSuperclass"),
            listOf(
                AnnotatedPythonFunction(
                    "testClassFunction1",
                    "testModule.TestClass.testClassFunction1",
                    listOf("decorators"),
                    listOf(
                        AnnotatedPythonParameter(
                            "onlyParam",
                            "testModule.TestClass.testClassFunction.onlyParam",
                            null,
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
                ),
                AnnotatedPythonFunction(
                    "__init__",
                    "testModule.TestClass.__init__",
                    listOf("decorators"),
                    listOf(
                        AnnotatedPythonParameter(
                            "onlyParam",
                            "testModule.TestClass.__init__.onlyParam",
                            null,
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

        // when
        val formattedClass = buildClassToString(testClass)

        // then
        val expectedFormattedClass: String =
            """
            |@Description("Lorem ipsum")
            |class TestClass(@Description("description") onlyParam: Any?) {
            |    @Description("description")
            |    attr onlyParam: Any?
            |
            |    @Description("description")
            |    fun testClassFunction1(@Description("description") onlyParam: Any?)
            |}""".trimMargin()

        formattedClass shouldBe expectedFormattedClass
    }
}