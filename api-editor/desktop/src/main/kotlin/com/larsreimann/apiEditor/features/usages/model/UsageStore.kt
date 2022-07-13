package com.larsreimann.apiEditor.features.usages.model

import com.larsreimann.apiEditor.features.ast.model.PythonClassId
import com.larsreimann.apiEditor.features.ast.model.PythonFunctionId
import com.larsreimann.apiEditor.features.ast.model.PythonModuleId
import com.larsreimann.apiEditor.features.ast.model.PythonParameterId

/**
 * @param moduleCounts
 * How often each module is used.
 *
 * @param classCounts
 * How often each class is used.
 *
 * @param functionCounts
 * How often each function is used.
 *
 * @param parameterCounts
 * How often each parameter is used.
 *
 * @param valueCounts
 * How often each parameter is set to a specific value. This also includes the cases where an optional parameter is not
 * set explicitly by the user, so its default value is used implicitly.
 */
data class UsageStore(
    private val moduleCounts: Map<PythonModuleId, Int> = emptyMap(),
    private val classCounts: Map<PythonClassId, Int> = emptyMap(),
    private val functionCounts: Map<PythonFunctionId, Int> = emptyMap(),
    private val parameterCounts: Map<PythonParameterId, Int> = emptyMap(),
    private val valueCounts: Map<PythonParameterId, Map<String, Int>> = emptyMap(),
) {
    fun getModuleCount(moduleId: PythonModuleId): Int = moduleCounts[moduleId] ?: 0
    fun getClassCount(classId: PythonClassId): Int = classCounts[classId] ?: 0
    fun getFunctionCount(functionId: PythonFunctionId): Int = functionCounts[functionId] ?: 0
    fun getParameterCount(parameterId: PythonParameterId): Int = parameterCounts[parameterId] ?: 0
    fun getValueCount(parameterId: PythonParameterId, value: String): Int = valueCounts[parameterId]?.get(value) ?: 0
}
