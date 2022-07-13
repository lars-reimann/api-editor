package com.larsreimann.apiEditor

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.larsreimann.apiEditor.data.Settings
import com.larsreimann.apiEditor.theme.PythonApiEditorTheme
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.awt.Cursor

private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
@Preview
fun Content(settings: Settings) {
    PythonApiEditorTheme(darkTheme = settings.darkMode) {
        val splitterState = rememberSplitPaneState(initialPositionPercentage = 0.4f)
        HorizontalSplitPane(
            splitPaneState = splitterState,
        ) {
            first(160.dp) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize(),
                ) {
//                    Column(modifier = Modifier.padding(5.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
//                        HeatmapTag(0, 10000, interpolation = HeatmapInterpolation.Logarithmic)
//                        HeatmapTag(1, 10000, interpolation = HeatmapInterpolation.Logarithmic)
//                        HeatmapTag(10, 10000, interpolation = HeatmapInterpolation.Logarithmic)
//                        HeatmapTag(100, 10000, interpolation = HeatmapInterpolation.Logarithmic)
//                        HeatmapTag(1000, 10000, interpolation = HeatmapInterpolation.Logarithmic)
//                        HeatmapTag(10000, 10000, interpolation = HeatmapInterpolation.Logarithmic)
//                    }
                    AnnotationDropdown(
                        showAttribute = true,
                        showBoundary = true,
                        showCalledAfter = true,
                    )
                }
            }
            second(160.dp) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize(),
                ) {
//                    Column(modifier = Modifier.padding(5.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
//                        HeatmapTag(0, 10000)
//                        HeatmapTag(1, 10000)
//                        HeatmapTag(10, 10000)
//                        HeatmapTag(100, 10000)
//                        HeatmapTag(1000, 10000)
//                        HeatmapTag(10000, 10000)
//                    }
                }
            }
            splitter {
                visiblePart {
                    Box(
                        Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(SolidColor(Color.Gray), alpha = 0.50f),
                    )
                }
                handle {
                    Box(
                        Modifier
                            .markAsHandle()
                            .cursorForHorizontalResize()
                            .width(9.dp)
                            .fillMaxHeight(),
                    )
                }
            }
        }
    }
}
