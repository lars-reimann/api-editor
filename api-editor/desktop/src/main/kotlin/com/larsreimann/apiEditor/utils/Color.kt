package com.larsreimann.apiEditor.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Returns either white or black depending on which provides better contrast to the given [backgroundColor].
 */
fun contentColorForBackgroundColor(backgroundColor: Color): Color {
    return when {
        backgroundColor.isDark() -> Color.White
        else -> Color.Black
    }
}

/**
 * Returns if the color is dark based on its luminance.
 */
fun Color.isDark(): Boolean {
    return luminance() <= 0.229
}

/**
 * Interpolates a color between two colors. If the [ratio] is 0, [minColor] is returned. If the [ratio] is 1, [maxColor]
 * is returned.
 */
fun interpolateColor(minColor: Color, maxColor: Color, ratio: Float): Color {
    val inverseRatio = 1f - ratio
    val r = (minColor.red * inverseRatio) + (maxColor.red * ratio)
    val g = (minColor.green * inverseRatio) + (maxColor.green * ratio)
    val b = (minColor.blue * inverseRatio) + (maxColor.blue * ratio)
    return Color(r, g, b)
}
