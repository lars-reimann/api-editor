package com.larsreimann.apiEditor.features.annotations.model

import com.larsreimann.apiEditor.features.ast.model.PythonDeclarationId
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf

data class AnnotationStore(
    private val annotationsByTarget: PersistentMap<PythonDeclarationId<*>, AnnotationList> = persistentHashMapOf(),
)
