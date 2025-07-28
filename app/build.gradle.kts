@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.qali.hesabi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.qali.hesabi"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(libs.bundles.androidx.lifeycle)

    implementation(libs.androidx.activity.compose)

    implementation(libs.bundles.compose.ui)
    implementation(libs.androidx.compose.material.icons)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.androidx.splash.screen)

    implementation(libs.vinchamp77.buildutils)
    // ZXing for barcode generation and scanning
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    // ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    
    // Gson for JSON serialization
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.samanzamani.persiandate:PersianDate:0.8.4")
}
