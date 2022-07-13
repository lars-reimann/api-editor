package com.larsreimann.apiEditor.utils

import androidx.compose.ui.graphics.Color
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ColorTest {
    @Nested
    inner class ContentColorForBackgroundColor {

        @Test
        fun `should return white content color for black background color`() {
            contentColorForBackgroundColor(Color.Black) shouldBe Color.White
        }

        @Test
        fun `should return black content color for white background color`() {
            contentColorForBackgroundColor(Color.White) shouldBe Color.Black
        }
    }

    @Nested
    inner class IsDark {

        @Test
        fun `should return true for black`() {
            Color.Black.isDark().shouldBeTrue()
        }

        @Test
        fun `should return false for white`() {
            Color.White.isDark().shouldBeFalse()
        }
    }

    @Nested
    inner class InterpolateColor {

        @Test
        fun `should return minColor for 0`() {
            interpolateColor(Color.Black, Color.White, 0f) shouldBe Color.Black
        }

        @Test
        fun `should return maxColor for 1`() {
            interpolateColor(Color.Black, Color.White, 1f) shouldBe Color.White
        }
    }
}
