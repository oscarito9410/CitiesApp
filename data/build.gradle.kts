import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.mokkery)
}


kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":domain"))
                implementation(libs.kotlin.stdlib)
                implementation(project.dependencies.platform(libs.koin.bom))
                api(libs.koin.core)
                implementation(libs.coroutines.core)

                api(libs.ktor.core)
                implementation(libs.ktor.contentNegotiation)
                implementation(libs.ktor.encoding)
                implementation(libs.ktor.json)
                implementation(libs.ktor.logging)

                implementation(libs.kotlinX.serializationJson)


                implementation(libs.room.runtime)
                implementation(libs.sqlite.bundled)
                implementation(libs.kermit)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.turbine)
                implementation(libs.coroutines.test)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.turbine)
                implementation(libs.coroutines.test)
                implementation(libs.mockk)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
                implementation(libs.room.runtime)
                implementation(libs.room.ktx)
                implementation(libs.ktor.android)
                implementation(libs.ktor.okhttp)
                implementation(libs.ktor.encoding)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.darwin)
                implementation(libs.ktor.encoding)
            }
        }
    }
    sourceSets.androidInstrumentedTest.dependencies {
        implementation(kotlin("test"))
    }
}

android {
    namespace = "com.oscarp.citiesapp.data"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}


dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

kover {
    currentProject {
        createVariant("coverage") {
            addWithDependencies("debug")
        }
    }
}