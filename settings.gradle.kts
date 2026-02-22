pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm").version("2.0.21")
        id("org.jetbrains.compose").version("1.7.0")
        id("org.jetbrains.kotlin.plugin.compose").version("2.0.21")
    }
}

rootProject.name = "ADBCard"
