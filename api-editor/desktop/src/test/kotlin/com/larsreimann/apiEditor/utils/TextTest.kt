package com.larsreimann.apiEditor.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class TextTest : FunSpec(
    {
        context("pluralize") {
            data class TestCase(
                val count: Int,
                val singular: String,
                val plural: String,
                val expectedString: String,
            ) : WithDataTestName {
                override fun dataTestName(): String {
                    return "($count, \"$singular\", \"$plural\") should return \"$expectedString\""
                }
            }

            withData(
                TestCase(0, "person", "people", "0 people"),
                TestCase(1, "person", "people", "1 person"),
                TestCase(2, "person", "people", "2 people"),
            ) { (count, singular, plural, expectedString) ->
                pluralize(count, singular, plural) shouldBe expectedString
            }
        }
    },
)
