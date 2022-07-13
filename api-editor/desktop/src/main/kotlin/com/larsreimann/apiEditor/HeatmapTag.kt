package com.larsreimann.apiEditor

import androidx.compose.material.Badge
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.em
import com.larsreimann.apiEditor.utils.contentColorForBackgroundColor
import com.larsreimann.apiEditor.utils.interpolateColor
import kotlin.math.log

@Composable
fun HeatmapTag(
    actualValue: Int,
    maxValue: Int,
    modifier: Modifier = Modifier,
    minColor: Color = Color.Blue,
    maxColor: Color = Color.Red,
    interpolation: HeatmapInterpolation = HeatmapInterpolation.Linear,
) {
    val ratio = computeRatio(actualValue, maxValue, interpolation)
    val backgroundColor = interpolateColor(minColor, maxColor, ratio)

    Badge(
        modifier = modifier,
        backgroundColor = backgroundColor,
        contentColor = contentColorForBackgroundColor(backgroundColor),
    ) {
        Text(text = actualValue.toString(), fontSize = 5.em)
    }
}

/**
 * Describes how the heatmap should interpolate between minColor and maxColor.
 */
enum class HeatmapInterpolation {

    /**
     * The heatmap interpolates linearly between minColor and maxColor.
     */
    Linear,

    /**
     * The heatmap interpolates logarithmically between minColor and maxColor.
     */
    Logarithmic
}

/**
 * Computes the ratio between the [actualValue] and the [maxValue] and applies the [interpolation] to the result.
 */
private fun computeRatio(actualValue: Int, maxValue: Int, interpolation: HeatmapInterpolation): Float {
    // Handle edge cases
    if (actualValue <= 0) {
        return 0f
    } else if (actualValue >= maxValue) {
        return 1f
    }

    return when (interpolation) {
        HeatmapInterpolation.Linear -> actualValue.toFloat() / maxValue.toFloat()
        HeatmapInterpolation.Logarithmic -> {
            // The case maxValue == 0 is handled by the if-clause above.
            log(actualValue.toDouble() + 1, maxValue.toDouble() + 1).toFloat()
        }
    }
}
