import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

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
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlinAndroid) apply false
    id("org.sonarqube") version "6.2.0.5505"
}

detekt {
    source.from(files(rootProject.rootDir))
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    parallel = true
    autoCorrect = true
    buildUponDefaultConfig = true
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}

tasks {
    fun SourceTask.config() {
        include("**/*.kt")
        exclude("**/*.kts")
        exclude("**/resources/**")
        exclude("**/generated/**")
        exclude("**/build/**")
    }
    withType<DetektCreateBaselineTask>().configureEach {
        config()
    }
    withType<Detekt>().configureEach {
        config()

        reports {
            sarif.required.set(true)
        }
    }
}

sonar {
    properties {
        property("sonar.projectKey", "oscarito9410_citiesapp")
        property("sonar.organization", "oscarito9410")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${project(":composeApp").buildDir}/reports/kover/reportCoverage.xml"
        )
        property(
            "sonar.coverage.exclusions",
            listOf(
                "**/di/**",
                "**/*Module.*",
                "**/AppDatabase*",
                "**/AppDatabaseConstructor*",
                "**/DatabaseBuilderKt*",
                "**/*Provider*",
                "**/KtorLogger.*",
                "**/KtorHttpClientProvider*",
                "**/CityApiService*",
                "**/ui/theme/**",
                "**/components/CityMapDetail*",
                "**/MainActivity*",
                "**/navigation/**",
                "**/App*",
                "**/utils/MultiWindowSizeLayout*",
                "**/generated/resources/**",
                "**/iosMain/**/CityDataImporterImpl.ios.kt",
                "**/commonMain/**/CityFtsEntity.kt",
                "**/androidMain/**/DatabaseBuilder.kt",
                "**/iosMain/**/DatabaseBuilder.kt",
                "**/iosMain/**/MainViewController.kt",
                "**/iosMain/**/SharedViewModel.ios.kt",
                "**/*Preview.kt",
                "**/*.preview.kt"
            ).joinToString(",")
        )
    }
}
