package com.larsreimann.apiEditor.features.usages.serialization

import com.larsreimann.apiEditor.features.ast.model.PythonClassId
import com.larsreimann.apiEditor.features.ast.model.PythonFunctionId
import com.larsreimann.apiEditor.features.ast.model.PythonModuleId
import com.larsreimann.apiEditor.features.ast.model.PythonParameterId
import com.larsreimann.apiEditor.testUtils.relativeResourcePathOrNull
import com.larsreimann.apiEditor.testUtils.resourcePathOrNull
import com.larsreimann.apiEditor.testUtils.walkResourceDirectory
import com.larsreimann.apiEditor.utils.pluralize
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import java.nio.file.Path

class SerializableUsageStoreTest : FunSpec(
    {
        val rootName = "/usages/serialization"

        context("should parse usage store JSON files") {
            val resourceNames = javaClass.walkResourceDirectory(rootName, "json")

            withData<Path>(
                { javaClass.relativeResourcePathOrNull(rootName, it).toString() },
                resourceNames,
            ) { path ->
                shouldNotThrow<SerializationException> {
                    createUsageStoreFromFile(path)
                }
            }
        }

        context("should create correct counts") {
            val resourceNames = listOf(
                "/usages/serialization/v2/level2UsageStore.json",
            )

            withData<String>(
                { javaClass.relativeResourcePathOrNull(rootName, it).toString() },
                resourceNames,
            ) { resourceName ->
                val path = javaClass.resourcePathOrNull(resourceName).shouldNotBeNull()
                val usageStore = createUsageStoreFromFile(path)

                // Check module counts
                withData(
                    ModuleCountTest("test/test_module", 2),
                    ModuleCountTest("test/unknown_module", 0),
                ) { (id, expectedCount) ->
                    usageStore.getModuleCount(PythonModuleId(id)) shouldBe expectedCount
                }

                // Check class counts
                withData(
                    ClassCountTest("test/test_module/TestClass", 1),
                    ClassCountTest("test/test_module/UnknownClass", 0),
                ) { (id, expectedCount) ->
                    usageStore.getClassCount(PythonClassId(id)) shouldBe expectedCount
                }

                // Check function counts
                withData(
                    FunctionCountTest("test/test_module/test_function", 1),
                    FunctionCountTest("test/test_module/unknown_function", 0),
                ) { (id, expectedCount) ->
                    usageStore.getFunctionCount(PythonFunctionId(id)) shouldBe expectedCount
                }

                // Check parameter counts
                withData(
                    ParameterCountTest("test/test_module/test_function/test_parameter", 1),
                    ParameterCountTest("test/test_module/test_function/unknown_parameter", 0),
                ) { (id, expectedCount) ->
                    usageStore.getParameterCount(PythonParameterId(id)) shouldBe expectedCount
                }

                // Check value counts
                withData(
                    ValueCountTest("test/test_module/test_function/test_parameter", "test_value", 1),
                    ValueCountTest("test/test_module/test_function/test_parameter", "unknown_value", 0),
                    ValueCountTest("test/test_module/test_function/unknown_parameter", "unknown_value", 0),
                ) { (id, value, expectedCount) ->
                    usageStore.getValueCount(PythonParameterId(id), value) shouldBe expectedCount
                }
            }
        }
    },
)

sealed class DeclarationCountTest : WithDataTestName {
    abstract val apiElementType: String
    abstract val id: String
    abstract val expectedCount: Int

    override fun dataTestName(): String {
        return "$apiElementType `$id` should be used ${pluralize(expectedCount, "time", "times")}"
    }
}

data class ModuleCountTest(
    override val id: String,
    override val expectedCount: Int,
) : DeclarationCountTest() {
    override val apiElementType = "module"
}

data class ClassCountTest(
    override val id: String,
    override val expectedCount: Int,
) : DeclarationCountTest() {
    override val apiElementType = "class"
}

data class FunctionCountTest(
    override val id: String,
    override val expectedCount: Int,
) : DeclarationCountTest() {
    override val apiElementType = "function"
}

data class ParameterCountTest(
    override val id: String,
    override val expectedCount: Int,
) : DeclarationCountTest() {
    override val apiElementType = "parameter"
}

data class ValueCountTest(val parameterId: String, val value: String, val expectedCount: Int) : WithDataTestName {
    override fun dataTestName(): String {
        return "parameter `$parameterId` should have the value `$value` ${
        pluralize(
            expectedCount,
            "time",
            "times",
        )
        }"
    }
}
