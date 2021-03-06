package com.larsreimann.python_api_editor.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Settings {
    var heatmapMode by mutableStateOf(HeatmapMode.None)
    var darkMode by mutableStateOf(false)
}

enum class HeatmapMode {
    None,
    Usages,
    Annotations
}
