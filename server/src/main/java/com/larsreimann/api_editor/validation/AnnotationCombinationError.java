package com.larsreimann.api_editor.validation;

public record AnnotationCombinationError(
    String qualifiedName,
    String firstAnnotationName,
    String secondAnnotationName
) implements AnnotationError {

    /**
     * Returns an error message specifying the annotation error.
     *
     * @return The constructed error message
     */
    @Override
    public String message() {
        return "(" + firstAnnotationName.toLowerCase() + ", " + secondAnnotationName.toLowerCase() + ") "
            + "cannot both be set for element: " + qualifiedName;
    }
}