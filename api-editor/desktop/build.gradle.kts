import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val javaVersion: String by project

// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    `java-test-fixtures`
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersion
}

compose.desktop {
    application {
        mainClass = "com.larsreimann.apiEditor.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "api-editor"
            packageVersion = "1.0.0"
        }
    }
}

// Dependencies --------------------------------------------------------------------------------------------------------

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.larsreimann:modeling-core:3.1.1")
    implementation("org.jetbrains.compose.components:components-splitpane:1.1.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    testFixturesImplementation(compose.desktop.currentOs)

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.3.2")
    testImplementation("io.kotest:kotest-framework-datatest:5.3.2")
    testImplementation("io.kotest:kotest-property:5.3.2")
    testImplementation("io.kotest:kotest-runner-junit5:5.3.2")
    testImplementation("io.mockk:mockk:1.12.4")
}

// Tasks ---------------------------------------------------------------------------------------------------------------

tasks.test {
    useJUnitPlatform()
}
