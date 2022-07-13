package com.larsreimann.apiEditor.features.annotations.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class AnnotationList(private val annotations: PersistentList<Annotation> = persistentListOf())
