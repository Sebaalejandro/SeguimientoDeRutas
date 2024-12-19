plugins {
    id("com.android.application")
    id("com.google.gms.google-services")  // Para habilitar los servicios de Google, como Firebase
}

android {
    namespace = "com.example.seguimientoderutas"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.seguimientoderutas"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Dependencias comunes
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Dependencias de Firebase (solo las necesarias y actualizadas)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0")) // BOM de Firebase
    implementation("com.google.firebase:firebase-firestore") // Firestore
    implementation("com.google.firebase:firebase-auth") // Autenticación
    implementation("com.google.firebase:firebase-database") // Realtime Database
    implementation("com.google.firebase:firebase-storage") // Almacenamiento
    implementation("com.google.firebase:firebase-analytics") // Analytics
    implementation ("com.google.firebase:firebase-firestore:24.5.0")
    implementation ("com.google.firebase:firebase-auth:21.2.0")
    implementation ("com.google.android.gms:play-services-location:18.0.0")
    implementation ("com.google.android.gms:play-services-maps:18.0.0")
    implementation ("com.google.firebase:firebase-firestore:24.1.1")

    // Dependencias para el mapa y localización
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    // En build.gradle (App)



}
