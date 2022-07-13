package com.larsreimann.apiEditor

import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.ResourceBundle

private val labels = ResourceBundle.getBundle("i18n.labels")

@Composable
fun AnnotationDropdown(
    showAttribute: Boolean = false,
    showBoundary: Boolean = false,
    showCalledAfter: Boolean = false,
    showConstant: Boolean = false,
    showDescription: Boolean = false,
    showEnum: Boolean = false,
    showGroup: Boolean = false,
    showMove: Boolean = false,
    showOptional: Boolean = false,
    showPure: Boolean = false,
    showRemove: Boolean = false,
    showRename: Boolean = false,
    showRequired: Boolean = false,
    showTodo: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(labels.getString("AnnotationDropdown.Button"))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (showAttribute) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.Attribute"))
                }
            }
            if (showBoundary) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.Boundary"))
                }
            }
            if (showCalledAfter) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.CalledAfter"))
                }
            }
            if (showConstant) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.Constant"))
                }
            }
            if (showDescription) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.Description"))
                }
            }
            if (showEnum) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.Enum"))
                }
            }
            if (showGroup) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.Group"))
                }
            }
            if (showMove) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.Move"))
                }
            }
            if (showOptional) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.Optional"))
                }
            }
            if (showPure) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.Pure"))
                }
            }
            if (showRemove) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.Remove"))
                }
            }
            if (showRename) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.Rename"))
                }
            }
            if (showRequired) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.Required"))
                }
            }
            if (showTodo) {
                DropdownMenuItem(onClick = {}) {
                    Text(labels.getString("AnnotationDropdown.Option.Todo"))
                }
            }
        }
    }
}
