package com.larsreimann.apiEditor.transformation.processingExceptions

class ConflictingEnumException(enumName: String, moduleName: String, qualifiedParameterName: String) :
    Exception(
        "Enum '" +
            enumName.replaceFirstChar { firstChar -> firstChar.uppercase() } +
            "' for parameter '" +
            qualifiedParameterName +
            "' already exists in module '" +
            moduleName +
            "' with conflicting instances.",
    )
