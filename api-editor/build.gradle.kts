// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    id("org.jetbrains.kotlinx.kover") version "0.5.1"
    idea

    // Pin versions for subprojects
    kotlin("jvm") version "1.7.10" apply false
    kotlin("plugin.serialization") version "1.7.0" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
    id("com.github.node-gradle.node") version "3.4.0" apply false
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev741" apply false
}

repositories {
    mavenCentral()
    // Can be removed once compose-jb supports Kotlin 1.7.0
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

idea {
    module {
        excludeDirs.add(file("gradle"))
        excludeDirs.add(file("node_modules"))
    }
}

kover {
    coverageEngine.set(kotlinx.kover.api.CoverageEngine.INTELLIJ)
}

// Subprojects ---------------------------------------------------------------------------------------------------------

subprojects {
    group = "com.larsreimann.api-editor"
    version = "1.0.0"

    repositories {
        mavenCentral()
        // Can be removed once compose-jb supports Kotlin 1.7.0
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}

// Can be removed once compose-jb supports Kotlin 1.7.0
allprojects {
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.jetbrains.compose.compiler:compiler")).apply {
                using(module("androidx.compose.compiler:compiler:1.2.0"))
            }
        }
    }
}
