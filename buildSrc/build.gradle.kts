plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
    maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
}

object PluginsVersions {
    const val ANDROID = "8.9.3"
    const val KOTLIN = "2.2.0"
    const val PUBLISH = "0.34.0"
}

dependencies {
    implementation("com.android.tools.build:gradle:${PluginsVersions.ANDROID}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${PluginsVersions.KOTLIN}")
    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:${PluginsVersions.PUBLISH}")
}