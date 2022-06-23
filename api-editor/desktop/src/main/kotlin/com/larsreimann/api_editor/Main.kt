package com.larsreimann.api_editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.larsreimann.api_editor.app.Content
import com.larsreimann.api_editor.app.MenuBar
import com.larsreimann.api_editor.features.settings.Settings
import java.util.ResourceBundle

private val labels = ResourceBundle.getBundle("i18n.labels")

fun main() = application {
    val settings by remember { mutableStateOf(Settings()) }

    Window(
        onCloseRequest = ::exitApplication,
        title = labels.getString("App.Window.Title"),
        icon = painterResource("img/icon.svg")
    ) {
        MenuBar(settings)
        Content(settings)
    }
}