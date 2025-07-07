import io.gitlab.arturbosch.detekt.Detekt

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.detekt)
}

detekt {
    buildUponDefaultConfig = true
    allRules = true
    parallel = true
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
}

tasks.register("detektAll") {
    description = "Runs Detekt on all subprojects"
    group = "verification"

    // For each subproject, add all Detekt tasks as dependencies
    rootProject.subprojects.forEach { subproject ->
        subproject.tasks.matching { it is Detekt }.configureEach {
            this@register.dependsOn(this)
        }
    }
}
