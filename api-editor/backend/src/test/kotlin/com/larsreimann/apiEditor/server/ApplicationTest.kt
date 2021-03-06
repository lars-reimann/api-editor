package com.larsreimann.apiEditor.server

import com.larsreimann.apiEditor.model.BoundaryAnnotation
import com.larsreimann.apiEditor.model.CalledAfterAnnotation
import com.larsreimann.apiEditor.model.ComparisonOperator
import com.larsreimann.apiEditor.model.ConstantAnnotation
import com.larsreimann.apiEditor.model.DefaultNumber
import com.larsreimann.apiEditor.model.DefaultString
import com.larsreimann.apiEditor.model.EnumAnnotation
import com.larsreimann.apiEditor.model.EnumPair
import com.larsreimann.apiEditor.model.GroupAnnotation
import com.larsreimann.apiEditor.model.MoveAnnotation
import com.larsreimann.apiEditor.model.OptionalAnnotation
import com.larsreimann.apiEditor.model.PureAnnotation
import com.larsreimann.apiEditor.model.PythonFromImport
import com.larsreimann.apiEditor.model.PythonImport
import com.larsreimann.apiEditor.model.PythonParameterAssignment
import com.larsreimann.apiEditor.model.RemoveAnnotation
import com.larsreimann.apiEditor.model.RenameAnnotation
import com.larsreimann.apiEditor.model.RequiredAnnotation
import com.larsreimann.apiEditor.model.SerializablePythonClass
import com.larsreimann.apiEditor.model.SerializablePythonFunction
import com.larsreimann.apiEditor.model.SerializablePythonModule
import com.larsreimann.apiEditor.model.SerializablePythonPackage
import com.larsreimann.apiEditor.model.SerializablePythonParameter
import com.larsreimann.apiEditor.model.SerializablePythonResult
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testEcho() {
        testApplication {
            val testPythonPackage = SerializablePythonPackage(
                distribution = "test-distribution",
                name = "test-package",
                version = "1.0.0",
                modules = mutableListOf(
                    SerializablePythonModule(
                        name = "test-module",
                        imports = mutableListOf(
                            PythonImport(
                                module = "test-import",
                                alias = "test-alias",
                            ),
                        ),
                        fromImports = mutableListOf(
                            PythonFromImport(
                                module = "test-from-import",
                                declaration = "test-declaration",
                                alias = null,
                            ),
                        ),
                        classes = mutableListOf(
                            SerializablePythonClass(
                                name = "test-class",
                                qualifiedName = "test-module.test-class",
                                decorators = listOf("test-decorator"),
                                superclasses = listOf("test-superclass"),
                                methods = mutableListOf(),
                                isPublic = true,
                                description = "Lorem ipsum",
                                fullDocstring = "Lorem ipsum",
                                annotations = mutableListOf(),
                            ),
                        ),
                        functions = mutableListOf(
                            SerializablePythonFunction(
                                name = "test-function",
                                qualifiedName = "test-module.test-function",
                                decorators = listOf("test-decorator"),
                                parameters = mutableListOf(
                                    SerializablePythonParameter(
                                        name = "test-parameter",
                                        qualifiedName = "test-module.test-function.test-parameter",
                                        defaultValue = "42",
                                        assignedBy = PythonParameterAssignment.NAME_ONLY,
                                        isPublic = true,
                                        typeInDocs = "str",
                                        description = "Lorem ipsum",
                                        annotations = mutableListOf(),
                                    ),
                                ),
                                results = mutableListOf(
                                    SerializablePythonResult(
                                        name = "test-result",
                                        type = "str",
                                        typeInDocs = "str",
                                        description = "Lorem ipsum",
                                        annotations = mutableListOf(),
                                    ),
                                ),
                                isPublic = true,
                                description = "Lorem ipsum",
                                fullDocstring = "Lorem ipsum",
                                annotations = mutableListOf(),
                            ),
                        ),
                        annotations = mutableListOf(),
                    ),
                ),
                annotations = mutableListOf(
                    BoundaryAnnotation(
                        isDiscrete = false,
                        lowerIntervalLimit = 0.0,
                        lowerLimitType = ComparisonOperator.LESS_THAN_OR_EQUALS,
                        upperIntervalLimit = 1.0,
                        upperLimitType = ComparisonOperator.LESS_THAN,
                    ),
                    CalledAfterAnnotation("test-other-function"),
                    ConstantAnnotation(DefaultNumber(0.1)),
                    EnumAnnotation(
                        enumName = "test-enum",
                        pairs = listOf(
                            EnumPair("test-value", "TEST_VALUE"),
                        ),
                    ),
                    GroupAnnotation(
                        groupName = "test-group",
                        parameters = mutableListOf(
                            "test-parameter",
                        ),
                    ),
                    MoveAnnotation("test-destination-module"),
                    OptionalAnnotation(DefaultString("bla")),
                    PureAnnotation,
                    RenameAnnotation("test-new-name"),
                    RequiredAnnotation,
                    RemoveAnnotation,
                ),
            )

            val requestBody = Json.encodeToString(testPythonPackage)

            val response = client.post("/api-editor/echo") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(requestBody, response.body())
        }
    }
}
