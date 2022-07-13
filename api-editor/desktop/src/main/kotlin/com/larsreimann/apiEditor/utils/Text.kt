package com.larsreimann.apiEditor.utils

/**
 * Concatenates the [count] with either the [singular] (if [count] is 1) or the [plural] (otherwise).
 */
fun pluralize(count: Int, singular: String, plural: String): String {
    return "$count ${if (count == 1) singular else plural}"
}
