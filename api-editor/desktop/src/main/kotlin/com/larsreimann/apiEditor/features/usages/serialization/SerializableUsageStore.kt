package com.larsreimann.apiEditor.features.usages.serialization

import com.larsreimann.apiEditor.features.ast.model.PythonClassId
import com.larsreimann.apiEditor.features.ast.model.PythonFunctionId
import com.larsreimann.apiEditor.features.ast.model.PythonModuleId
import com.larsreimann.apiEditor.features.ast.model.PythonParameterId
import com.larsreimann.apiEditor.features.usages.model.UsageStore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class SerializableUsageStore {
    abstract fun toUsageStore(): UsageStore
}

@Serializable
@SerialName("Usages@v2")
class SerializableUsageStoreV2(
    private val moduleCounts: Map<PythonModuleId, Int> = emptyMap(),
    private val classCounts: Map<PythonClassId, Int> = emptyMap(),
    private val functionCounts: Map<PythonFunctionId, Int> = emptyMap(),
    private val parameterCounts: Map<PythonParameterId, Int> = emptyMap(),
    private val valueCounts: Map<PythonParameterId, Map<String, Int>> = emptyMap(),
) : SerializableUsageStore() {

    override fun toUsageStore(): UsageStore {
        return UsageStore(
            moduleCounts,
            classCounts,
            functionCounts,
            parameterCounts,
            valueCounts,
        )
    }
}
