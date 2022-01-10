package com.larsreimann.api_editor.codegen

import com.larsreimann.api_editor.model.Boundary
import com.larsreimann.api_editor.model.ComparisonOperator
import com.larsreimann.api_editor.model.PythonFromImport
import com.larsreimann.api_editor.model.PythonImport
import com.larsreimann.api_editor.model.PythonParameterAssignment
import com.larsreimann.api_editor.mutable_model.OriginalPythonClass
import com.larsreimann.api_editor.mutable_model.PythonArgument
import com.larsreimann.api_editor.mutable_model.PythonAttribute
import com.larsreimann.api_editor.mutable_model.PythonBoolean
import com.larsreimann.api_editor.mutable_model.PythonCall
import com.larsreimann.api_editor.mutable_model.PythonClass
import com.larsreimann.api_editor.mutable_model.PythonConstructor
import com.larsreimann.api_editor.mutable_model.PythonEnum
import com.larsreimann.api_editor.mutable_model.PythonEnumInstance
import com.larsreimann.api_editor.mutable_model.PythonFloat
import com.larsreimann.api_editor.mutable_model.PythonFunction
import com.larsreimann.api_editor.mutable_model.PythonInt
import com.larsreimann.api_editor.mutable_model.PythonMemberAccess
import com.larsreimann.api_editor.mutable_model.PythonModule
import com.larsreimann.api_editor.mutable_model.PythonNamedType
import com.larsreimann.api_editor.mutable_model.PythonParameter
import com.larsreimann.api_editor.mutable_model.PythonReference
import com.larsreimann.api_editor.mutable_model.PythonResult
import com.larsreimann.api_editor.mutable_model.PythonString
import com.larsreimann.api_editor.mutable_model.PythonStringifiedType
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PythonCodeGeneratorTest {
    @Test
    fun buildModuleContentReturnsFormattedModuleContent() { // TODO
        // given
        val testMethodParameter = PythonParameter(
            name = "only-param",
            defaultValue = "'defaultValue'",
            assignedBy = PythonParameterAssignment.NAME_ONLY
        )
        val testClass = PythonClass(
            name = "test-class",
            methods = listOf(
                PythonFunction(
                    name = "test-class-function",
                    parameters = listOf(
                        PythonParameter(
                            name = "self",
                            assignedBy = PythonParameterAssignment.IMPLICIT
                        ),
                        testMethodParameter
                    ),
                    callToOriginalAPI = PythonCall(
                        receiver = "self.instance.test-class-function",
                        arguments = listOf(
                            PythonArgument(
                                value = PythonReference(testMethodParameter)
                            )
                        )
                    )
                )
            ),
            originalClass = OriginalPythonClass(qualifiedName = "test-module.test-class")
        )
        val testFunction1Parameter1 = PythonParameter(name = "param1")
        val testFunction1Parameter2 = PythonParameter(name = "param2")
        val testFunction1Parameter3 = PythonParameter(name = "param3")
        val testFunction2Parameter = PythonParameter(
            name = "test-parameter",
            defaultValue = "42",
            assignedBy = PythonParameterAssignment.NAME_ONLY
        )
        val testModule = PythonModule(
            name = "test-module",
            classes = mutableListOf(testClass),
            functions = listOf(
                PythonFunction(
                    name = "function_module",
                    parameters = listOf(
                        testFunction1Parameter1,
                        testFunction1Parameter2,
                        testFunction1Parameter3
                    ),
                    results = listOf(
                        PythonResult(
                            name = "test-result",
                            type = PythonStringifiedType("str")
                        )
                    ),
                    callToOriginalAPI = PythonCall(
                        receiver = "test-module.function_module",
                        arguments = listOf(
                            PythonArgument(
                                name = "param1",
                                value = PythonReference(testFunction1Parameter1)
                            ),
                            PythonArgument(
                                name = "param2",
                                value = PythonReference(testFunction1Parameter2)
                            ),
                            PythonArgument(
                                name = "param3",
                                value = PythonReference(testFunction1Parameter3)
                            )
                        )
                    )
                ),
                PythonFunction(
                    name = "test-function",
                    parameters = listOf(
                        testFunction2Parameter
                    ),
                    results = listOf(
                        PythonResult(
                            "test-result",
                            PythonStringifiedType("str"),
                            "str"
                        )
                    ),
                    callToOriginalAPI = PythonCall(
                        receiver = "test-module.test-function",
                        arguments = listOf(
                            PythonArgument(
                                name = "test-parameter",
                                value = PythonReference(testFunction2Parameter)
                            )
                        )
                    )
                )
            )
        )

        // when
        val moduleContent = testModule.toPythonCode()

        // then
        val expectedModuleContent: String =
            """
            |import test-module
            |
            |class test-class:
            |    def test-class-function(self, *, only-param='defaultValue'):
            |        return self.instance.test-class-function(only-param)
            |
            |def function_module(param1, param2, param3):
            |    return test-module.function_module(param1=param1, param2=param2, param3=param3)
            |
            |def test-function(*, test-parameter=42):
            |    return test-module.test-function(test-parameter=test-parameter)
            |
            """.trimMargin()

        moduleContent shouldBe expectedModuleContent
    }

    @Test
    fun buildModuleContentWithNoClassesReturnsFormattedModuleContent() { // TODO
        // given
        val testFunction1Parameter1 = PythonParameter(name = "param1")
        val testFunction1Parameter2 = PythonParameter(name = "param2")
        val testFunction1Parameter3 = PythonParameter(name = "param3")
        val testFunction2Parameter = PythonParameter(
            "test-parameter",
            defaultValue = "42",
            assignedBy = PythonParameterAssignment.NAME_ONLY
        )
        val testModule = PythonModule(
            name = "test-module",
            functions = mutableListOf(
                PythonFunction(
                    name = "function_module",
                    parameters = mutableListOf(
                        testFunction1Parameter1,
                        testFunction1Parameter2,
                        testFunction1Parameter3
                    ),
                    results = mutableListOf(
                        PythonResult(
                            "test-result",
                            PythonStringifiedType("str"),
                            "Lorem ipsum"
                        )
                    ),
                    callToOriginalAPI = PythonCall(
                        receiver = "test-module.function_module",
                        arguments = listOf(
                            PythonArgument(
                                name = "param1",
                                value = PythonReference(testFunction1Parameter1)
                            ),
                            PythonArgument(
                                name = "param2",
                                value = PythonReference(testFunction1Parameter2)
                            ),
                            PythonArgument(
                                name = "param3",
                                value = PythonReference(testFunction1Parameter3)
                            )
                        )
                    )
                ),
                PythonFunction(
                    name = "test-function",
                    parameters = mutableListOf(
                        testFunction2Parameter
                    ),
                    results = mutableListOf(
                        PythonResult(
                            "test-result",
                            PythonStringifiedType("str"),
                            "Lorem ipsum"
                        )
                    ),
                    callToOriginalAPI = PythonCall(
                        receiver = "test-module.test-function",
                        arguments = listOf(
                            PythonArgument(
                                name = "test-parameter",
                                value = PythonReference(testFunction2Parameter)
                            )
                        )
                    )
                )
            )
        )

        // when
        val moduleContent = testModule.toPythonCode()

        // then
        val expectedModuleContent: String =
            """
            |import test-module
            |
            |def function_module(param1, param2, param3):
            |    return test-module.function_module(param1=param1, param2=param2, param3=param3)
            |
            |def test-function(*, test-parameter=42):
            |    return test-module.test-function(test-parameter=test-parameter)
            |
            """.trimMargin()

        moduleContent shouldBe expectedModuleContent
    }

    @Test
    fun buildModuleContentWithNoFunctionsReturnsFormattedModuleContent() { // TODO
        // given
        val testClass = PythonClass(
            name = "test-class",
            constructor = PythonConstructor(
                callToOriginalAPI = PythonCall(receiver = "test-module.test-class")
            ),
            originalClass = OriginalPythonClass("test-module.test-class")
        )
        val testModule = PythonModule(
            name = "test-module",
            imports = mutableListOf(
                PythonImport(
                    "test-import1",
                    "test-alias"
                )
            ),
            fromImports = mutableListOf(
                PythonFromImport(
                    "test-from-import1",
                    "test-declaration1",
                    null
                )
            ),
            classes = mutableListOf(testClass)
        )

        // when
        val moduleContent = testModule.toPythonCode()

        // then
        val expectedModuleContent: String =
            """
            |import test-module
            |
            |class test-class:
            |    def __init__():
            |        self.instance = test-module.test-class()
            |
            """.trimMargin()

        moduleContent shouldBe expectedModuleContent
    }

    @Test
    fun buildModuleContentWithEmptyModuleReturnsEmptyString() { // TODO
        // given
        val testModule = PythonModule(
            name = "test-module",
            imports = mutableListOf(
                PythonImport(
                    "test-import1",
                    "test-alias"
                )
            ),
            fromImports = mutableListOf(
                PythonFromImport(
                    "test-from-import1",
                    "test-declaration1",
                    null
                )
            ),
            classes = mutableListOf(),
            enums = mutableListOf(),
            functions = mutableListOf()
        )

        // when
        val moduleContent = testModule.toPythonCode()

        // then
        val expectedModuleContent = ""

        moduleContent shouldBe expectedModuleContent
    }

    @Test
    fun buildModuleContentWithBoundaryAnnotationReturnsFormattedModuleContent1() { // TODO
        // given
        val testParameter1 = PythonParameter(
            name = "param1",
            defaultValue = "5",
            assignedBy = PythonParameterAssignment.NAME_ONLY
        )
        testParameter1.boundary = Boundary(
            true,
            2.0,
            ComparisonOperator.LESS_THAN,
            10.0,
            ComparisonOperator.LESS_THAN_OR_EQUALS
        )
        val testParameter2 = PythonParameter(
            "param2",
            defaultValue = "5",
            assignedBy = PythonParameterAssignment.NAME_ONLY
        )
        testParameter2.boundary = Boundary(
            false,
            5.0,
            ComparisonOperator.LESS_THAN_OR_EQUALS,
            0.0,
            ComparisonOperator.UNRESTRICTED
        )
        val testParameter3 = PythonParameter(
            "param3",
            defaultValue = "5",
            assignedBy = PythonParameterAssignment.NAME_ONLY
        )
        testParameter3.boundary = Boundary(
            false,
            0.0,
            ComparisonOperator.UNRESTRICTED,
            10.0,
            ComparisonOperator.LESS_THAN
        )
        val testFunction = PythonFunction(
            name = "function_module",
            parameters = mutableListOf(testParameter1, testParameter2, testParameter3),
            callToOriginalAPI = PythonCall(
                receiver = "test-module.function_module",
                arguments = listOf(
                    PythonArgument(
                        name = "param1",
                        value = PythonReference(testParameter1)
                    ),
                    PythonArgument(
                        name = "param2",
                        value = PythonReference(testParameter2)
                    ),
                    PythonArgument(
                        name = "param3",
                        value = PythonReference(testParameter3)
                    )
                )
            )
        )
        val testModule = PythonModule(
            name = "test-module",
            functions = mutableListOf(testFunction),
        )

        // when
        val moduleContent = testModule.toPythonCode()

        // then
        val expectedModuleContent: String =
            """
            |import test-module
            |
            |def function_module(*, param1=5, param2=5, param3=5):
            |    if not (isinstance(param1, int) or (isinstance(param1, float) and param1.is_integer())):
            |        raise ValueError('param1 needs to be an integer, but {} was assigned.'.format(param1))
            |    if not 2.0 < param1 <= 10.0:
            |        raise ValueError('Valid values of param1 must be in (2.0, 10.0], but {} was assigned.'.format(param1))
            |    if not 5.0 <= param2:
            |        raise ValueError('Valid values of param2 must be greater than or equal to 5.0, but {} was assigned.'.format(param2))
            |    if not param3 < 10.0:
            |        raise ValueError('Valid values of param3 must be less than 10.0, but {} was assigned.'.format(param3))
            |    return test-module.function_module(param1=param1, param2=param2, param3=param3)
            |
            """.trimMargin()

        moduleContent shouldBe expectedModuleContent
    }

    @Test
    fun buildModuleContentWithBoundaryAnnotationReturnsFormattedModuleContent2() { // TODO
        // given
        val testParameter = PythonParameter(
            name = "param1",
            defaultValue = "5",
            assignedBy = PythonParameterAssignment.NAME_ONLY
        )
        testParameter.boundary = Boundary(
            false,
            2.0,
            ComparisonOperator.LESS_THAN_OR_EQUALS,
            0.0,
            ComparisonOperator.UNRESTRICTED
        )
        val testFunction = PythonFunction(
            name = "function_module",
            parameters = listOf(testParameter),
            callToOriginalAPI = PythonCall(
                receiver = "test-module.function_module",
                arguments = listOf(
                    PythonArgument(
                        name = "param1",
                        value = PythonReference(testParameter)
                    )
                )
            )
        )
        val testModule = PythonModule(
            name = "test-module",
            functions = listOf(testFunction),
        )

        // when
        val moduleContent = testModule.toPythonCode()

        // then
        val expectedModuleContent: String =
            """
            |import test-module
            |
            |def function_module(*, param1=5):
            |    if not 2.0 <= param1:
            |        raise ValueError('Valid values of param1 must be greater than or equal to 2.0, but {} was assigned.'.format(param1))
            |    return test-module.function_module(param1=param1)
            |
            """.trimMargin()

        moduleContent shouldBe expectedModuleContent
    }

    @Test
    fun `should create valid code for empty classes`() { // TODO
        val testClass = PythonClass(
            name = "TestClass",
            constructor = PythonConstructor(
                callToOriginalAPI = PythonCall(
                    receiver = "testModule.TestClass"
                )
            )
        )

        testClass.toPythonCode() shouldBe """
            |class TestClass:
            |    def __init__():
            |        self.instance = testModule.TestClass()
        """.trimMargin()
    }

    @Test
    fun buildClassReturnsFormattedClassWithOneFunction() { // TODO
        // given
        val testParameter = PythonParameter(
            name = "only-param",
            defaultValue = "'defaultValue'",
            assignedBy = PythonParameterAssignment.NAME_ONLY
        )
        val testClass = PythonClass(
            name = "test-class",
            constructor = PythonConstructor(
                parameters = mutableListOf(
                    PythonParameter(
                        name = "self",
                        assignedBy = PythonParameterAssignment.IMPLICIT
                    ),
                    testParameter
                ),
                callToOriginalAPI = PythonCall(
                    receiver = "test-module.test-class",
                    arguments = listOf(
                        PythonArgument(
                            value = PythonReference(testParameter)
                        )
                    )
                )
            ),
            originalClass = OriginalPythonClass(qualifiedName = "test-module.test-class")
        )

        // when
        val formattedClass = testClass.toPythonCode()

        // then
        val expectedFormattedClass: String =
            """
            |class test-class:
            |    def __init__(self, *, only-param='defaultValue'):
            |        self.instance = test-module.test-class(only-param)
            """.trimMargin()
        formattedClass shouldBe expectedFormattedClass
    }

    @Test
    fun buildClassReturnsFormattedClassWithTwoFunctions() { // TODO
        // given
        val testMethod1Parameter = PythonParameter(
            name = "only-param",
            assignedBy = PythonParameterAssignment.POSITION_OR_NAME,
        )
        val testMethod2Parameter = PythonParameter(
            name = "only-param",
            assignedBy = PythonParameterAssignment.POSITION_OR_NAME,
        )
        val testClass = PythonClass(
            name = "test-class",
            methods = mutableListOf(
                PythonFunction(
                    name = "test-class-function1",
                    parameters = mutableListOf(
                        PythonParameter(
                            name = "self",
                            assignedBy = PythonParameterAssignment.IMPLICIT,
                        ),
                        testMethod1Parameter
                    ),
                    callToOriginalAPI = PythonCall(
                        receiver = "self.instance.test-class-function1",
                        arguments = listOf(
                            PythonArgument(value = PythonReference(testMethod1Parameter))
                        )
                    )
                ),
                PythonFunction(
                    name = "test-class-function2",
                    parameters = mutableListOf(
                        PythonParameter(
                            name = "self",
                            assignedBy = PythonParameterAssignment.IMPLICIT
                        ),
                        testMethod2Parameter
                    ),
                    callToOriginalAPI = PythonCall(
                        receiver = "self.instance.test-class-function2",
                        arguments = listOf(
                            PythonArgument(value = PythonReference(testMethod2Parameter))
                        )
                    )
                )
            )
        )

        // when
        val formattedClass = testClass.toPythonCode()

        // then
        val expectedFormattedClass: String =
            """
            |class test-class:
            |    def test-class-function1(self, only-param):
            |        return self.instance.test-class-function1(only-param)
            |
            |    def test-class-function2(self, only-param):
            |        return self.instance.test-class-function2(only-param)""".trimMargin()

        formattedClass shouldBe expectedFormattedClass
    }

    @Test
    fun buildClassReturnsFormattedClassBasedOnOriginalDeclaration() { // TODO
        // given
        val testParameter1 = PythonParameter(name = "second-param")
        val testParameter2 = PythonParameter(name = "third-param")
        val testFunction = PythonFunction(
            name = "test-function",
            parameters = mutableListOf(
                PythonParameter(
                    name = "self",
                    assignedBy = PythonParameterAssignment.IMPLICIT
                ),
                testParameter1,
                testParameter2
            ),
            callToOriginalAPI = PythonCall(
                receiver = "self.instance.test-function",
                arguments = listOf(
                    PythonArgument(value = PythonReference(testParameter1)),
                    PythonArgument(
                        name = "third-param",
                        value = PythonReference(testParameter2)
                    )
                )
            )
        )
        val testClass = PythonClass(
            name = "test-class",
            methods = mutableListOf(testFunction)
        )

        // when
        val formattedClass = testClass.toPythonCode()

        // then
        val expectedFormattedClass: String =
            """
            |class test-class:
            |    def test-function(self, second-param, third-param):
            |        return self.instance.test-function(second-param, third-param=third-param)""".trimMargin()
        formattedClass shouldBe expectedFormattedClass
    }

    @Test
    fun buildFunctionReturnsFormattedFunctionWithNoParameters() { // TODO
        // given
        val testFunction = PythonFunction(
            name = "test-function",
            callToOriginalAPI = PythonCall(receiver = "test-module.test-function")
        )

        // when
        val formattedFunction = testFunction.toPythonCode()

        // then
        val expectedFormattedFunction: String =
            """
            |def test-function():
            |    return test-module.test-function()""".trimMargin()
        formattedFunction shouldBe expectedFormattedFunction
    }

    @Test
    fun buildFunctionReturnsFormattedFunctionWithPositionOnlyParameter() { // TODO
        // given
        val testParameter = PythonParameter(
            name = "only-param",
            defaultValue = "13",
            assignedBy = PythonParameterAssignment.NAME_ONLY
        )
        val testFunction = PythonFunction(
            name = "test-function",
            parameters = mutableListOf(
                testParameter
            ),
            callToOriginalAPI = PythonCall(
                receiver = "test-module.test-function",
                arguments = listOf(
                    PythonArgument(value = PythonReference(testParameter))
                )
            )
        )

        // when
        val formattedFunction = testFunction.toPythonCode()

        // then
        val expectedFormattedFunction: String =
            """
            |def test-function(*, only-param=13):
            |    return test-module.test-function(only-param)""".trimMargin()
        formattedFunction shouldBe expectedFormattedFunction
    }

    @Test
    fun buildFunctionReturnsFormattedFunctionWithPositionOrNameParameter() { // TODO
        // given
        val testParameter = PythonParameter(
            name = "only-param",
            defaultValue = "False",
            assignedBy = PythonParameterAssignment.NAME_ONLY
        )
        val testFunction = PythonFunction(
            name = "test-function",
            parameters = mutableListOf(
                testParameter
            ),
            callToOriginalAPI = PythonCall(
                receiver = "test-module.test-function",
                arguments = listOf(
                    PythonArgument(value = PythonReference(testParameter))
                )
            )
        )

        // when
        val formattedFunction = testFunction.toPythonCode()

        // then
        val expectedFormattedFunction: String =
            """
            |def test-function(*, only-param=False):
            |    return test-module.test-function(only-param)""".trimMargin()
        formattedFunction shouldBe expectedFormattedFunction
    }

    @Test
    fun buildFunctionReturnsFormattedFunctionWithNameOnlyParameter() { // TODO
        // given
        val testParameter = PythonParameter(name = "only-param")
        val testFunction = PythonFunction(
            name = "test-function",
            parameters = mutableListOf(
                testParameter
            ),
            callToOriginalAPI = PythonCall(
                receiver = "test-module.test-function",
                arguments = listOf(
                    PythonArgument(
                        name = "only-param",
                        value = PythonReference(testParameter)
                    )
                )
            )
        )

        // when
        val formattedFunction = testFunction.toPythonCode()

        // then
        val expectedFormattedFunction: String =
            """
            |def test-function(only-param):
            |    return test-module.test-function(only-param=only-param)""".trimMargin()
        formattedFunction shouldBe expectedFormattedFunction
    }

    @Test
    fun buildFunctionReturnsFormattedFunctionWithPositionAndPositionOrNameParameter() { // TODO
        // given

        val testParameter1 = PythonParameter(name = "first-param")
        val testParameter2 = PythonParameter(name = "second-param")
        val testFunction = PythonFunction(
            name = "test-function",
            parameters = mutableListOf(
                testParameter1,
                testParameter2
            ),
            callToOriginalAPI = PythonCall(
                receiver = "test-module.test-function",
                arguments = listOf(
                    PythonArgument(value = PythonReference(testParameter1)),
                    PythonArgument(value = PythonReference(testParameter2))
                )
            )
        )

        // when
        val formattedFunction = testFunction.toPythonCode()

        // then
        val expectedFormattedFunction: String =
            """
            |def test-function(first-param, second-param):
            |    return test-module.test-function(first-param, second-param)""".trimMargin()
        formattedFunction shouldBe expectedFormattedFunction
    }

    @Test
    fun buildFunctionReturnsFormattedFunctionWithPositionAndPositionOrNameAndNameOnlyParameter() { // TODO
        // given
        val testParameter1 = PythonParameter(name = "first-param")
        val testParameter2 = PythonParameter(name = "second-param")
        val testParameter3 = PythonParameter(name = "third-param")
        val testFunction = PythonFunction(
            name = "test-function",
            parameters = listOf(
                testParameter1,
                testParameter2,
                testParameter3
            ),
            callToOriginalAPI = PythonCall(
                receiver = "test-module.test-function",
                arguments = listOf(
                    PythonArgument(value = PythonReference(testParameter1)),
                    PythonArgument(value = PythonReference(testParameter2)),
                    PythonArgument(
                        name = "third-param",
                        value = PythonReference(testParameter3)
                    )
                )
            )
        )

        // when
        val formattedFunction = testFunction.toPythonCode()

        // then
        val expectedFormattedFunction: String =
            """
            |def test-function(first-param, second-param, third-param):
            |    return test-module.test-function(first-param, second-param, third-param=third-param)""".trimMargin()
        formattedFunction shouldBe expectedFormattedFunction
    }

    @Test
    fun buildFunctionReturnsFormattedFunctionWithPositionAndNameOnlyParameter() { // TODO
        // given
        val testParameter1 = PythonParameter(name = "first-param")
        val testParameter2 = PythonParameter(name = "second-param")
        val testFunction = PythonFunction(
            name = "test-function",
            parameters = listOf(
                testParameter1,
                testParameter2
            ),
            callToOriginalAPI = PythonCall(
                receiver = "test-module.test-function",
                arguments = listOf(
                    PythonArgument(value = PythonReference(testParameter1)),
                    PythonArgument(
                        name = "second-param",
                        value = PythonReference(testParameter2)
                    )
                )
            )
        )

        // when
        val formattedFunction = testFunction.toPythonCode()

        // then
        val expectedFormattedFunction: String =
            """
            |def test-function(first-param, second-param):
            |    return test-module.test-function(first-param, second-param=second-param)""".trimMargin()
        formattedFunction shouldBe expectedFormattedFunction
    }

    @Test
    fun buildFunctionReturnsFormattedFunctionWithPositionOrNameAndNameOnlyParameter() { // TODO
        // given
        val testParameter1 = PythonParameter(name = "first-param")
        val testParameter2 = PythonParameter(name = "second-param")
        val testFunction = PythonFunction(
            name = "test-function",
            parameters = listOf(
                testParameter1,
                testParameter2
            ),
            callToOriginalAPI = PythonCall(
                receiver = "test-module.test-function",
                arguments = listOf(
                    PythonArgument(
                        value = PythonReference(testParameter1)
                    ),
                    PythonArgument(
                        name = "second-param",
                        value = PythonReference(testParameter2)
                    )
                )
            )
        )

        // when
        val formattedFunction = testFunction.toPythonCode()

        // then
        val expectedFormattedFunction: String =
            """
            |def test-function(first-param, second-param):
            |    return test-module.test-function(first-param, second-param=second-param)
            """.trimMargin()

        formattedFunction shouldBe expectedFormattedFunction
    }

    @Test
    fun buildFunctionsReturnsFormattedFunctionBasedOnOriginalDeclaration() { // TODO
        // given
        val testParameter1 = PythonParameter(name = "first-param")
        val testParameter2 = PythonParameter(name = "second-param")
        val testParameter3 = PythonParameter(name = "third-param")
        val testFunction = PythonFunction(
            name = "test-function",
            parameters = mutableListOf(
                testParameter1,
                testParameter2,
                testParameter3
            ),
            callToOriginalAPI = PythonCall(
                receiver = "test-module.test-function",
                arguments = listOf(
                    PythonArgument(value = PythonReference(testParameter1)),
                    PythonArgument(value = PythonReference(testParameter2)),
                    PythonArgument(
                        name = "third-param",
                        value = PythonReference(testParameter3)
                    )
                )
            )
        )

        // when
        val formattedFunction = testFunction.toPythonCode()

        // then
        val expectedFormattedFunction: String =
            """
            |def test-function(first-param, second-param, third-param):
            |    return test-module.test-function(first-param, second-param, third-param=third-param)""".trimMargin()
        formattedFunction shouldBe expectedFormattedFunction
    }

    @Test
    fun buildClassReturnsFormattedClassWithStaticMethodDecorator() {
        // given
        val testParameter = PythonParameter(name = "only-param")
        val testClass = PythonClass(
            name = "test-class",
            methods = listOf(
                PythonFunction(
                    name = "test-class-function1",
                    decorators = mutableListOf("staticmethod"),
                    parameters = listOf(testParameter),
                    callToOriginalAPI = PythonCall(
                        receiver = "test-module.test-class.test-class-function1",
                        arguments = listOf(
                            PythonArgument(value = PythonReference(testParameter))
                        )
                    )
                )
            )
        )

        // when
        val formattedClass: String = testClass.toPythonCode()

        // then
        val expectedFormattedClass: String =
            """
           |class test-class:
           |    @staticmethod
           |    def test-class-function1(only-param):
           |        return test-module.test-class.test-class-function1(only-param)""".trimMargin()

        formattedClass shouldBe expectedFormattedClass
    }

    @Nested
    inner class ModuleToPythonCode {

        @Test
        fun `should import Enum if the module contains enums`() {
            val testModule = PythonModule(
                name = "testModule",
                enums = listOf(
                    PythonEnum(name = "TestEnum")
                )
            )

            testModule.toPythonCode() shouldBe """
                |from enum import Enum
                |
                |class TestEnum(Enum):
                |    pass
                |
            """.trimMargin()
        }

        @Test
        fun `should not import Enum if the module does not contain enums`() {
            val testModule = PythonModule(name = "testModule")

            testModule.toPythonCode() shouldBe ""
        }
    }

    @Nested
    inner class FunctionToPythonCode {

        @Test
        fun `should access value of enum parameters`() {
            val testParameter = PythonParameter(name = "testParameter")
            val testFunction = PythonFunction(
                name = "testFunction",
                parameters = listOf(
                    testParameter
                ),
                callToOriginalAPI = PythonCall(
                    receiver = "testModule.testFunction",
                    arguments = listOf(
                        PythonArgument(
                            value = PythonMemberAccess(
                                receiver = PythonReference(testParameter),
                                member = PythonReference(PythonAttribute(name = "value"))
                            )
                        )
                    )
                )
            )

            testFunction.toPythonCode() shouldBe """
                |def testFunction(testParameter):
                |    return testModule.testFunction(testParameter.value)
            """.trimMargin()
        }

        @Test
        fun `should access attribute of parameter objects`() {
            val testParameter = PythonParameter(name = "testGroup")
            val testFunction = PythonFunction(
                name = "testFunction",
                parameters = listOf(
                    testParameter
                ),
                callToOriginalAPI = PythonCall(
                    receiver = "testModule.testFunction",
                    arguments = listOf(
                        PythonArgument(
                            value = PythonMemberAccess(
                                receiver = PythonReference(testParameter),
                                member = PythonReference(
                                    PythonAttribute(name = "newParameter1")
                                )
                            )
                        ),
                        PythonArgument(
                            name = "oldParameter2",
                            value = PythonMemberAccess(
                                receiver = PythonReference(testParameter),
                                member = PythonReference(
                                    PythonAttribute(name = "newParameter2")
                                )
                            )
                        )
                    )
                )
            )

            testFunction.toPythonCode() shouldBe """
                |def testFunction(testGroup):
                |    return testModule.testFunction(testGroup.newParameter1, oldParameter2=testGroup.newParameter2)
            """.trimMargin()
        }
    }

    @Nested
    inner class ParameterToPythonCode {

        @Test
        fun `should handle parameters without type and default value`() {
            val testParameter = PythonParameter(
                name = "param"
            )

            testParameter.toPythonCode() shouldBe "param"
        }

        @Test
        fun `should handle parameters with type but without default value`() {
            val testParameter = PythonParameter(
                name = "param",
                type = PythonStringifiedType("int")
            )

            testParameter.toPythonCode() shouldBe "param: int"
        }

        @Test
        fun `should handle parameters without type but with default value`() {
            val testParameter = PythonParameter(
                name = "param",
                defaultValue = "1"
            )

            testParameter.toPythonCode() shouldBe "param=1"
        }

        @Test
        fun `should handle parameters with type and default value`() {
            val testParameter = PythonParameter(
                name = "param",
                type = PythonStringifiedType("int"),
                defaultValue = "1"
            )

            testParameter.toPythonCode() shouldBe "param: int = 1"
        }
    }

    @Nested
    inner class EnumToPythonCode {

        @Test
        fun `should create valid Python code for enums without instances`() {
            val testEnum = PythonEnum(name = "TestEnum")

            testEnum.toPythonCode() shouldBe """
                |class TestEnum(Enum):
                |    pass
            """.trimMargin()
        }

        @Test
        fun `should create valid Python code for enums with instances`() {
            val testEnum = PythonEnum(
                name = "TestEnum",
                instances = listOf(
                    PythonEnumInstance(
                        name = "TestEnumInstance1",
                        value = "inst1"
                    ),
                    PythonEnumInstance(
                        name = "TestEnumInstance2",
                        value = "inst2"
                    )
                )
            )

            testEnum.toPythonCode() shouldBe """
                |class TestEnum(Enum):
                |    TestEnumInstance1 = "inst1",
                |    TestEnumInstance2 = "inst2"
            """.trimMargin()
        }
    }

    @Nested
    inner class ArgumentToPythonCode {

        @Test
        fun `should handle positional arguments`() {
            val testArgument = PythonArgument(value = PythonInt(1))

            testArgument.toPythonCode() shouldBe "1"
        }

        @Test
        fun `should handle named arguments`() {
            val testArgument = PythonArgument(
                name = "arg",
                value = PythonInt(1)
            )

            testArgument.toPythonCode() shouldBe "arg=1"
        }
    }

    @Nested
    inner class ExpressionToPythonCode {

        @Test
        fun `should handle false boolean`() {
            val expression = PythonBoolean(false)
            expression.toPythonCode() shouldBe "False"
        }

        @Test
        fun `should handle true boolean`() {
            val expression = PythonBoolean(true)
            expression.toPythonCode() shouldBe "True"
        }

        @Test
        fun `should handle calls`() {
            val expression = PythonCall(
                receiver = "function",
                arguments = listOf(
                    PythonArgument(value = PythonInt(1)),
                    PythonArgument(
                        name = "param",
                        value = PythonInt(1)
                    )
                )
            )
            expression.toPythonCode() shouldBe "function(1, param=1)"
        }

        @Test
        fun `should handle floats`() {
            val expression = PythonFloat(1.0)
            expression.toPythonCode() shouldBe "1.0"
        }

        @Test
        fun `should handle ints`() {
            val expression = PythonInt(1)
            expression.toPythonCode() shouldBe "1"
        }

        @Test
        fun `should handle member accesses`() {
            val expression = PythonMemberAccess(
                receiver = PythonReference(PythonParameter(name = "param")),
                member = PythonReference(PythonAttribute(name = "value"))
            )
            expression.toPythonCode() shouldBe "param.value"
        }

        @Test
        fun `should handle references`() {
            val expression = PythonReference(PythonParameter("param"))
            expression.toPythonCode() shouldBe "param"
        }

        @Test
        fun `should handle strings`() {
            val expression = PythonString("string")
            expression.toPythonCode() shouldBe "'string'"
        }
    }

    @Nested
    inner class TypeToPythonCodeOrNull {

        @Test
        fun `should handle named types`() {
            val type = PythonNamedType(PythonEnum("TestEnum"))
            type.toPythonCodeOrNull() shouldBe "TestEnum"
        }

        @Test
        fun `should convert stringified type 'bool' to Boolean`() {
            val smlType = PythonStringifiedType("bool")
            smlType.toPythonCodeOrNull() shouldBe "bool"
        }

        @Test
        fun `should convert stringified type 'float' to Float`() {
            val smlType = PythonStringifiedType("float")
            smlType.toPythonCodeOrNull() shouldBe "float"
        }

        @Test
        fun `should convert stringified type 'int' to Int`() {
            val smlType = PythonStringifiedType("int")
            smlType.toPythonCodeOrNull() shouldBe "int"
        }

        @Test
        fun `should convert stringified type 'str' to String`() {
            val smlType = PythonStringifiedType("str")
            smlType.toPythonCodeOrNull() shouldBe "str"
        }

        @Test
        fun `should return null for other types`() {
            val type = PythonStringifiedType("")
            type.toPythonCodeOrNull().shouldBeNull()
        }
    }
}
