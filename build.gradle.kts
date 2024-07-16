// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}
buildscript{

        repositories {
            google()
            mavenCentral()
        }
        dependencies {
            classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
            classpath("com.android.tools.build:gradle:8.5.1")
            classpath("io.realm:realm-gradle-plugin:10.15.1")
        }
    }

