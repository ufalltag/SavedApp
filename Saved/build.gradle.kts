plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktLint)
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
}
subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        debug.set(false)
        version.set("1.8.0")
        verbose.set(true)
        android.set(true)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)

        filter {
            exclude { element -> element.file.path.contains("build/generated") }
            exclude { element -> element.file.path.contains("generated") }
        }
    }
}
