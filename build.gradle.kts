buildscript {
    repositories {
        google()
        maven(url = uri("https://plugins.gradle.org/m2/"))
        maven(url = uri("https://jitpack.io"))
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath(Dependencies.Build.gradle)
        classpath(Dependencies.Build.kotlin)
    }
}

allprojects {
    repositories {
        google()
        maven(url = uri("https://plugins.gradle.org/m2/"))
        maven(url = uri("https://jitpack.io"))
        mavenCentral()
        gradlePluginPortal()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
