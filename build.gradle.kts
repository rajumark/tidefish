import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.0.21"
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "com.tidefish.app"
version = "1.0.${getCommitCount()}"

fun getCommitCount(): String {
    return "git rev-list --count HEAD".runCommand()
}

fun String.runCommand(): String {
    return ProcessBuilder(split(" "))
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .start()
        .inputStream
        .bufferedReader()
        .readText()
        .trim()
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {

    implementation(compose.desktop.currentOs)
    implementation ("com.formdev:flatlaf-intellij-themes:3.5.2")
    implementation ("com.formdev:flatlaf:3.5.2")
    implementation ("com.mixpanel:mixpanel-java:1.5.2")
    implementation ("org.json:json:20240303")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
    implementation ("org.ocpsoft.prettytime:prettytime:5.0.2.Final")
    implementation("org.simpleframework:simple-xml:2.7.1")
    implementation("com.google.code.gson:gson:2.8.7")

    //login with Google
//    implementation("com.google.api-client:google-api-client:1.34.1")
//    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
//    implementation("com.google.apis:google-api-services-oauth2:v2-rev124-1.22.0")
//    implementation("com.google.http-client:google-http-client-gson:1.43.3")

    //implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
//    implementation("com.google.http-client:google-http-client-jackson2:1.40.1")
    implementation("com.sun.mail:jakarta.mail:2.0.1")

    //load svg
    implementation("org.apache.xmlgraphics:batik-transcoder:1.14")

   implementation("com.formdev:flatlaf-extras:3.1")

    implementation("com.fifesoft:rsyntaxtextarea:3.3.4")


    implementation("redis.clients:jedis:5.1.0")

}

compose.desktop {
    application {
        mainClass = "StartPage"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Tidefish"
            packageVersion = version.toString()
            //Gumroad: https://gumroad.com/products
            //Logs https://docs.google.com/spreadsheets/d/1lHfe-dSo6jFYZkQkVT5yILXG-sRkuFs9k2J78F8Ov7M/edit?resourcekey=&gid=954940293#gid=954940293

            val iconsRoot = project.file("desktop-icons")
            macOS {
                iconFile.set(iconsRoot.resolve("icon-mac.icns"))
            }
            windows {
                iconFile.set(iconsRoot.resolve("icon-windows.ico"))
                menuGroup = "Tidefish"
                installationPath = "C:\\Program Files\\Tidefish"

                upgradeUuid = "8f3d4c92-1b3a-7c8e-2a14-90f75b83d1ce"
            }
            linux {
                iconFile.set(iconsRoot.resolve("icon-linux.png"))
            }
        }
    }
}
