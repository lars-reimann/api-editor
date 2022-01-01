package com.larsreimann.api_editor.transformation;

import com.larsreimann.api_editor.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Processor for Constant-, Optional- and RequiredAnnotations
 */
public class ParameterAnnotationProcessor extends AbstractPackageDataTransformer {
    @Override
    public boolean shouldVisitResultsIn(
        @NotNull AnnotatedPythonFunction pythonFunction
    ) {
        return false;
    }

    @Override
    public AnnotatedPythonParameter createNewParameter(
        @NotNull AnnotatedPythonParameter oldParameter
    ) {
        ArrayList<EditorAnnotation> annotations = new ArrayList<>();
        String defaultValue = oldParameter.getDefaultValue();
        PythonParameterAssignment assignedBy;
        /* preprocessing:
        required parameters -> position_or_name,
        optional parameters -> name_only
         */
        if (oldParameter.getDefaultValue() != null) {
            assignedBy = PythonParameterAssignment.NAME_ONLY;
        } else {
            assignedBy = PythonParameterAssignment.POSITION_OR_NAME;
        }
        for (EditorAnnotation editorAnnotation : oldParameter.getAnnotations()) {
            if (editorAnnotation instanceof AttributeAnnotation) {
                assignedBy = PythonParameterAssignment.ATTRIBUTE;
                defaultValue = (((AttributeAnnotation) editorAnnotation)
                    .getDefaultValue()).toString();
            } else if (editorAnnotation instanceof ConstantAnnotation) {
                assignedBy = PythonParameterAssignment.CONSTANT;
                defaultValue = (((ConstantAnnotation) editorAnnotation)
                    .getDefaultValue()).toString();
            } else if (editorAnnotation instanceof OptionalAnnotation) {
                defaultValue = (((OptionalAnnotation) editorAnnotation)
                    .getDefaultValue()).toString();
                assignedBy = PythonParameterAssignment.NAME_ONLY;
            } else if (editorAnnotation instanceof RequiredAnnotation) {
                defaultValue = null;
                assignedBy = PythonParameterAssignment.POSITION_OR_NAME;
            } else {
                annotations.add(editorAnnotation);
            }
        }
        return oldParameter.fullCopy(
            oldParameter.getName(),
            oldParameter.getQualifiedName(),
            defaultValue,
            assignedBy,
            oldParameter.isPublic(),
            oldParameter.getTypeInDocs(),
            oldParameter.getDescription(),
            annotations,
            oldParameter.getBoundary(),
            oldParameter.getOriginalDeclaration() != null ?
                oldParameter.getOriginalDeclaration() : oldParameter
        );
    }

    @Override
    public AnnotatedPythonFunction createNewFunctionOnLeave(
        @NotNull AnnotatedPythonFunction oldFunction,
        @NotNull List<AnnotatedPythonParameter> newParameters,
        @NotNull List<AnnotatedPythonResult> newResults
    ) {
        return oldFunction.fullCopy(
            oldFunction.getName(),
            oldFunction.getQualifiedName(),
            oldFunction.getDecorators(),
            reorderParameters(newParameters),
            newResults,
            oldFunction.isPublic(),
            oldFunction.getDescription(),
            oldFunction.getFullDocstring(),
            oldFunction.getAnnotations(),
            oldFunction.getCalledAfter(),
            oldFunction.isPure(),
            oldFunction.getOriginalDeclaration() != null ?
                oldFunction.getOriginalDeclaration() : oldFunction
        );
    }

    @Override
    public AnnotatedPythonClass createNewClassOnLeave(
        @NotNull AnnotatedPythonClass oldClass,
        @NotNull List<AnnotatedPythonAttribute> newAttributes,
        @NotNull List<AnnotatedPythonFunction> newMethods
    ) {
        List<AnnotatedPythonAttribute> processedNewAttributes = new ArrayList<>(newAttributes);

        for (AnnotatedPythonFunction pythonFunction : newMethods) {
            if (pythonFunction.isConstructor()) {
                for (AnnotatedPythonParameter constructorParameter :
                    pythonFunction.getParameters()) {
                    // Change default value of old attribute if assigned by of
                    // constructor parameter is of type attribute
                    if (constructorParameter.getAssignedBy()
                        == PythonParameterAssignment.ATTRIBUTE) {
                        Optional<AnnotatedPythonAttribute> unmodifiedAttribute =
                            processedNewAttributes.stream().filter(
                            pythonAttribute ->
                                Objects.requireNonNull(
                                    pythonAttribute.getOriginalDeclaration()
                                ).getName()
                                    .equals(
                                        Objects.requireNonNull(
                                            constructorParameter
                                                .getOriginalDeclaration()
                                            )
                                            .getName()
                                    )
                        ).findAny();
                        if (unmodifiedAttribute.isPresent()) {
                            AnnotatedPythonAttribute unmodifiedPresentAttribute
                                = unmodifiedAttribute.get();
                            processedNewAttributes.remove(unmodifiedPresentAttribute);
                            processedNewAttributes.add(
                                unmodifiedPresentAttribute.fullCopy(
                                    unmodifiedPresentAttribute.getName(),
                                    unmodifiedPresentAttribute.getQualifiedName(),
                                    constructorParameter.getDefaultValue(),
                                    unmodifiedPresentAttribute.isPublic(),
                                    unmodifiedPresentAttribute.getTypeInDocs(),
                                    unmodifiedPresentAttribute.getDescription(),
                                    unmodifiedPresentAttribute.getAnnotations(),
                                    unmodifiedPresentAttribute.getBoundary(),
                                    unmodifiedPresentAttribute.getOriginalDeclaration()
                                )
                            );
                        }
                    }
                    // Remove attribute if corresponding
                    // constructor parameter is of type constant
                    else if (constructorParameter.getAssignedBy()
                        == PythonParameterAssignment.CONSTANT) {
                        processedNewAttributes.removeIf(
                            pythonAttribute ->
                                Objects.requireNonNull(pythonAttribute
                                        .getOriginalDeclaration())
                                    .getName()
                                    .equals(
                                        Objects.requireNonNull(constructorParameter
                                                .getOriginalDeclaration())
                                            .getName()
                                    )
                        );
                    }
                }
            }
        }

        return oldClass.fullCopy(
            oldClass.getName(),
            oldClass.getQualifiedName(),
            oldClass.getDecorators(),
            oldClass.getSuperclasses(),
            processedNewAttributes,
            newMethods,
            oldClass.isPublic(),
            oldClass.getDescription(),
            oldClass.getFullDocstring(),
            oldClass.getAnnotations(),
            oldClass.getOriginalDeclaration()
        );
    }

    private List<AnnotatedPythonParameter> reorderParameters(
        List<AnnotatedPythonParameter> unorderedParameters
    ) {
        List<AnnotatedPythonParameter> orderedParameters = new ArrayList<>();
        List<AnnotatedPythonParameter> attributeParameters = new ArrayList<>();
        List<AnnotatedPythonParameter> constantParameters = new ArrayList<>();
        List<AnnotatedPythonParameter> implicitParameters = new ArrayList<>();
        List<AnnotatedPythonParameter> optionalParameters = new ArrayList<>();
        List<AnnotatedPythonParameter> requiredParameters = new ArrayList<>();
        unorderedParameters.forEach(pythonParameter -> {
            switch (pythonParameter.getAssignedBy()) {
                case IMPLICIT -> implicitParameters.add(pythonParameter);
                case POSITION_OR_NAME -> requiredParameters.add(pythonParameter);
                case NAME_ONLY -> optionalParameters.add(pythonParameter);
                case CONSTANT -> constantParameters.add(pythonParameter);
                case ATTRIBUTE -> attributeParameters.add(pythonParameter);
                case POSITION_ONLY -> throw new RuntimeException(
                    "Position_only parameters must not exist after executing ParameterAnnotationProcessor"
                );
            }
        });
        orderedParameters.addAll(implicitParameters);
        orderedParameters.addAll(requiredParameters);
        orderedParameters.addAll(optionalParameters);
        orderedParameters.addAll(constantParameters);
        orderedParameters.addAll(attributeParameters);

        return orderedParameters;
    }
}
