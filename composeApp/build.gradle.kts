import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
    alias(libs.plugins.mokkery)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        dependencies {
            androidTestImplementation("androidx.compose.ui:ui-test-junit4-android:1.8.2")
            debugImplementation("androidx.compose.ui:ui-test-manifest:1.8.2")
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            // Koin-Dependency injection
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.compottie)
            implementation(libs.compottie.dot)
            implementation(libs.compottie.resources)
            implementation(libs.kermit)
            implementation(project(":data"))
            implementation(project(":domain"))
        }

        androidUnitTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("androidx.compose.ui:ui-test-junit4:1.8.2")
            implementation("org.robolectric:robolectric:4.11.1")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.turbine)
            implementation(libs.coroutines.test)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
    }
}

android {
    namespace = "com.oscarp.citiesapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.oscarp.citiesapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    kover(project(":composeApp"))
    kover(project(":data"))
    kover(project(":domain"))
}


kover {
    currentProject {
        createVariant("coverage") {
            addWithDependencies("debug")
        }
    }

    reports {
        // filters for all report types of all build variants
        filters {
            excludes {
                androidGeneratedClasses()
                annotatedBy("androidx.compose.ui.tooling.preview.Preview")
                classes(
                    listOf(
                        "*di.*",
                        "*Module.*",
                        "com.oscarp.citiesapp.data.local.AppDatabase*",
                        "com.oscarp.citiesapp.data.local.AppDatabaseConstructor*",
                        "com.oscarp.citiesapp.data.local.DatabaseBuilderKt*",
                        "com.oscarp.citiesapp.data.local.*Provider*",
                        "com.oscarp.citiesapp.data.local.dao.*",
                        "com.oscarp.citiesapp.data.remote.KtorLogger",
                        "com.oscarp.citiesapp.data.remote.KtorHttpClientProvider*",
                        "com.oscarp.citiesapp.data.remote.CityApiService",
                        "com.oscarp.citiesapp.data.remote.CityApiServiceImpl*",
                        "com.oscarp.citiesapp.ui.theme.*",
                        "com.oscarp.citiesapp.App"
                    )
                )
            }
        }
        verify {
            rule {
                minBound(80)
            }
        }
    }
}
