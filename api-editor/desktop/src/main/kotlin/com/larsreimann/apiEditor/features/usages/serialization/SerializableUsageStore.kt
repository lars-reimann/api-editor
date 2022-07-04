package com.larsreimann.apiEditor.features.usages.serialization

import com.larsreimann.apiEditor.features.api.ClassId
import com.larsreimann.apiEditor.features.api.FunctionId
import com.larsreimann.apiEditor.features.api.ModuleId
import com.larsreimann.apiEditor.features.api.ParameterId
import com.larsreimann.apiEditor.features.usages.UsageStore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class SerializableUsageStore {
    abstract fun toUsageStore(): UsageStore
}

@Serializable
@SerialName("2")
class SerializableUsageStoreV2(
    private val moduleCounts: Map<String, Int> = emptyMap(),
    private val classCounts: Map<String, Int> = emptyMap(),
    private val functionCounts: Map<String, Int> = emptyMap(),
    private val parameterCounts: Map<String, Int> = emptyMap(),
    private val valueCounts: Map<String, Map<String, Int>> = emptyMap(),
) : SerializableUsageStore() {

    override fun toUsageStore(): UsageStore {
        return UsageStore(
            moduleCounts.mapKeys { (id, _) -> ModuleId(id) },
            classCounts.mapKeys { (id, _) -> ClassId(id) },
            functionCounts.mapKeys { (id, _) -> FunctionId(id) },
            parameterCounts.mapKeys { (id, _) -> ParameterId(id) },
            valueCounts.mapKeys { (id, _) -> ParameterId(id) },
        )
    }
}
