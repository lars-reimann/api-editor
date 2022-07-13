package com.larsreimann.apiEditor

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import com.larsreimann.apiEditor.data.HeatmapMode
import com.larsreimann.apiEditor.data.Settings
import java.util.ResourceBundle
import androidx.compose.ui.window.MenuBar as ComposeMenuBar

private val labels: ResourceBundle = ResourceBundle.getBundle("i18n.labels")

@Composable
fun FrameWindowScope.MenuBar(settings: Settings) {
    ComposeMenuBar {
        Menu(labels.getString("MenuBar.File"), mnemonic = 'F') {
            Menu(labels.getString("MenuBar.File.Import"), mnemonic = 'I') {
                Item(labels.getString("MenuBar.File.Import.API"), onClick = {})
                Item(labels.getString("MenuBar.File.Import.Usages"), onClick = {})
                Item(labels.getString("MenuBar.File.Import.Annotations"), onClick = {})
            }
            Menu(labels.getString("MenuBar.File.Export"), mnemonic = 'E') {
                Item(labels.getString("MenuBar.File.Export.Annotations"), onClick = {})
            }
            Separator()
            Item(labels.getString("MenuBar.File.Exit"), mnemonic = 'X', onClick = {})
        }
        Menu(labels.getString("MenuBar.Actions"), mnemonic = 'A') {
            Item(labels.getString("MenuBar.Actions.GenerateAdapters"), onClick = {})
            Item(labels.getString("MenuBar.Actions.DeleteAllAnnotations"), onClick = {})
        }
        Menu(labels.getString("MenuBar.View"), mnemonic = 'V') {
            Menu(labels.getString("MenuBar.View.Heatmap")) {
                RadioButtonItem(
                    labels.getString("MenuBar.View.Heatmap.None"),
                    selected = settings.heatmapMode == HeatmapMode.None,
                    onClick = { settings.heatmapMode = HeatmapMode.None },
                )
                RadioButtonItem(
                    labels.getString("MenuBar.View.Heatmap.Usages"),
                    selected = settings.heatmapMode == HeatmapMode.Usages,
                    onClick = { settings.heatmapMode = HeatmapMode.Usages },
                )
                RadioButtonItem(
                    labels.getString("MenuBar.View.Heatmap.Annotations"),
                    selected = settings.heatmapMode == HeatmapMode.Annotations,
                    onClick = { settings.heatmapMode = HeatmapMode.Annotations },
                )
            }
            Separator()
            CheckboxItem(
                labels.getString("MenuBar.View.DarkMode"),
                checked = settings.darkMode,
                onCheckedChange = { settings.darkMode = !settings.darkMode },
            )
        }
    }
}
