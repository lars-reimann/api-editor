package com.larsreimann.api_editor.codegen;

import com.larsreimann.api_editor.model.AnnotatedPythonClass;
import com.larsreimann.api_editor.model.AnnotatedPythonFunction;
import com.larsreimann.api_editor.model.AnnotatedPythonParameter;
import com.larsreimann.api_editor.model.AttributeAnnotation;
import com.larsreimann.api_editor.model.DefaultString;
import com.larsreimann.api_editor.model.PythonParameterAssignment;
import com.larsreimann.api_editor.model.RenameAnnotation;
import com.larsreimann.api_editor.transformation.OriginalDeclarationProcessor;
import com.larsreimann.api_editor.transformation.RenameAnnotationProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class ClassAdapterContentBuilderTest {
    @Test
    void buildClassReturnsFormattedClassWithNoFunctions() {
        // given
        AnnotatedPythonClass testClass = new AnnotatedPythonClass(
            "test-class",
            "test-module.test-class",
            List.of("test-decorator"),
            List.of("test-superclass"),
            Collections.emptyList(),
            true,
            "Lorem ipsum",
            "Lorem ipsum",
            Collections.emptyList()
        );
        testClass.accept(OriginalDeclarationProcessor.INSTANCE);

        // when
        ClassAdapterContentBuilder classAdapterContentBuilder =
            new ClassAdapterContentBuilder(testClass);
        String formattedClass = classAdapterContentBuilder.buildClass();

        // then
        String expectedFormattedClass = "class test-class:";
        Assertions.assertEquals(expectedFormattedClass, formattedClass);
    }

    @Test
    void buildClassReturnsFormattedClassWithOneFunction() {
        // given
        AnnotatedPythonClass testClass = new AnnotatedPythonClass(
            "test-class",
            "test-module.test-class",
            List.of("test-decorator"),
            List.of("test-superclass"),
            List.of(new AnnotatedPythonFunction(
                "test-class-function",
                "test-module.test-class.test-class-function",
                List.of("decorators"),
                List.of(
                    new AnnotatedPythonParameter(
                        "self",
                        "test-module.test-class.test-class-function.self",
                        null,
                        PythonParameterAssignment.POSITION_OR_NAME,
                        true,
                        "typeInDocs",
                        "description",
                        Collections.emptyList()
                    ),
                    new AnnotatedPythonParameter(
                        "only-param",
                        "test-module.test-class.test-class-function.only-param",
                        "'defaultValue'",
                        PythonParameterAssignment.POSITION_OR_NAME,
                        true,
                        "str",
                        "description",
                        List.of(new AttributeAnnotation(
                            new DefaultString("test")
                        ))
                    )
                ),
                Collections.emptyList(),
                true,
                "description",
                "fullDocstring",
                Collections.emptyList()
            )),
            true,
            "Lorem ipsum",
            "Lorem ipsum",
            Collections.emptyList()
        );
        testClass.accept(OriginalDeclarationProcessor.INSTANCE);

        // when
        ClassAdapterContentBuilder classAdapterContentBuilder =
            new ClassAdapterContentBuilder(testClass);
        String formattedClass = classAdapterContentBuilder.buildClass();

        // then
        String expectedFormattedClass = """
            class test-class:
                def test-class-function(self, only-param='defaultValue'):
                    test-module.test-class.test-class-function(only-param)""";
        Assertions.assertEquals(expectedFormattedClass, formattedClass);
    }

    @Test
    void buildClassReturnsFormattedClassWithTwoFunctions() {
        // given
        AnnotatedPythonClass testClass = new AnnotatedPythonClass(
            "test-class",
            "test-module.test-class",
            List.of("test-decorator"),
            List.of("test-superclass"),
            List.of(
                new AnnotatedPythonFunction(
                    "test-class-function1",
                    "test-module.test-class.test-class-function1",
                    List.of("decorators"),
                    List.of(
                        new AnnotatedPythonParameter(
                            "self",
                            "test-module.test-class.test-class-function1.self",
                            null,
                            PythonParameterAssignment.POSITION_OR_NAME,
                            true,
                            "typeInDocs",
                            "description",
                            Collections.emptyList()
                        ),
                        new AnnotatedPythonParameter(
                            "only-param",
                            "test-module.test-class.test-class-function1.only-param",
                            null,
                            PythonParameterAssignment.POSITION_OR_NAME,
                            true,
                            "typeInDocs",
                            "description",
                            Collections.emptyList()
                        )
                    ),
                    Collections.emptyList(),
                    true,
                    "description",
                    "fullDocstring",
                    Collections.emptyList()
                ),
                new AnnotatedPythonFunction(
                    "test-class-function2",
                    "test-module.test-class.test-class-function2",
                    List.of("decorators"),
                    List.of(
                        new AnnotatedPythonParameter(
                            "self",
                            "test-module.test-class.test-class-function2.self",
                            null,
                            PythonParameterAssignment.POSITION_OR_NAME,
                            true,
                            "typeInDocs",
                            "description",
                            Collections.emptyList()
                        ),
                        new AnnotatedPythonParameter(
                            "only-param",
                            "test-module.test-class.test-class-function2.only-param",
                            null,
                            PythonParameterAssignment.POSITION_OR_NAME,
                            true,
                            "typeInDocs",
                            "description",
                            Collections.emptyList()
                        )
                    ),
                    Collections.emptyList(),
                    true,
                    "description",
                    "fullDocstring",
                    Collections.emptyList()
                )
            ),
            true,
            "Lorem ipsum",
            "Lorem ipsum",
            Collections.emptyList()
        );
        testClass.accept(OriginalDeclarationProcessor.INSTANCE);

        // when
        ClassAdapterContentBuilder classAdapterContentBuilder =
            new ClassAdapterContentBuilder(testClass);
        String formattedClass = classAdapterContentBuilder.buildClass();

        // then
        String expectedFormattedClass = """
            class test-class:
                def test-class-function1(self, only-param):
                    test-module.test-class.test-class-function1(only-param)

                def test-class-function2(self, only-param):
                    test-module.test-class.test-class-function2(only-param)""";
        Assertions.assertEquals(expectedFormattedClass, formattedClass);
    }

    @Test
    void buildClassReturnsFormattedClassBasedOnOriginalDeclaration() {
        // given
        AnnotatedPythonFunction testFunction = new AnnotatedPythonFunction(
            "test-function",
            "test-module.test-class.test-function",
            List.of("test-decorator"),
            List.of(
                new AnnotatedPythonParameter(
                    "self",
                    "test-module.test-class.test-class-function.self",
                    null,
                    PythonParameterAssignment.POSITION_OR_NAME,
                    true,
                    "typeInDocs",
                    "description",
                    Collections.emptyList()
                ),
                new AnnotatedPythonParameter(
                    "second-param",
                    "test-module.test-class.test-class-function.second-param",
                    null,
                    PythonParameterAssignment.POSITION_OR_NAME,
                    true,
                    "typeInDocs",
                    "description",
                    List.of(
                        new RenameAnnotation("newSecondParamName")
                    )
                ),
                new AnnotatedPythonParameter(
                    "third-param",
                    "test-module.test-class.test-class-function.third-param",
                    null,
                    PythonParameterAssignment.NAME_ONLY,
                    true,
                    "typeInDocs",
                    "description",
                    List.of(
                        new RenameAnnotation("newThirdParamName")
                    )
                )
            ),
            Collections.emptyList(),
            true,
            "Lorem ipsum",
            "fullDocstring",
            List.of(
                new RenameAnnotation("newFunctionName")
            )
        );

        AnnotatedPythonClass testClass = new AnnotatedPythonClass(
            "test-class",
            "test-module.test-class",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(testFunction),
            true,
            "",
            "",
            List.of(
                new RenameAnnotation("newClassName")
            )
        );

        testClass.accept(OriginalDeclarationProcessor.INSTANCE);
        RenameAnnotationProcessor renameAnnotationProcessor =
            new RenameAnnotationProcessor();
        testClass = testClass.accept(renameAnnotationProcessor);

        // when
        ClassAdapterContentBuilder classAdapterContentBuilder =
            new ClassAdapterContentBuilder(testClass);
        String formattedClass = classAdapterContentBuilder.buildClass();

        // then
        String expectedFormattedClass = """
            class newClassName:
                def newFunctionName(self, newSecondParamName, *, newThirdParamName):
                    test-module.test-class.test-function(newSecondParamName, third-param=newThirdParamName)""";
        Assertions.assertEquals(expectedFormattedClass, formattedClass);
    }
}