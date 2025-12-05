import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.openappslabs.jotter"
    compileSdk = 36
    signingConfigs {
        create("release") {
            val localPropertiesFile = rootProject.file("local.properties")
            // Only load keys if local.properties exists
            if (localPropertiesFile.exists()) {
                val properties = Properties()
                properties.load(FileInputStream(localPropertiesFile))
                // READ PATH FROM LOCAL.PROPERTIES
                val keyPath = properties.getProperty("storeFile")
                // Only sign if the path was found
                if (keyPath != null) {
                    storeFile = file(keyPath)
                    storePassword = properties.getProperty("JOTTER_KEYSTORE_PASSWORD")
                    keyAlias = properties.getProperty("JOTTER_KEY_ALIAS")
                    keyPassword = properties.getProperty("JOTTER_KEY_PASSWORD")
                }
            }
        }
    }

    packaging {
        // Your requested syntax for Native Stripping fix
        jniLibs.keepDebugSymbols.add("**/*.so")
    }

    defaultConfig {
        applicationId = "com.openappslabs.jotter"
        minSdk = 29
        targetSdk = 36
        versionCode = 4
        versionName = "1.2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Fix: Check if local.properties exists to apply signing
            if (rootProject.file("local.properties").exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig  = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.reorderable)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.gson)

    implementation(libs.androidx.biometric)
}
