// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false  // El plugin de Google Services
}

buildscript {
    repositories {
        google()  // Repositorio de Google
        mavenCentral()  // Repositorio Maven central
    }
    dependencies {
        // Clase de plugin de Android
        classpath ("com.android.tools.build:gradle:8.0.1")

        // Aquí agregamos el plugin de Google Services para Firebase
        classpath ("com.google.gms:google-services:4.4.2")
    }
}




