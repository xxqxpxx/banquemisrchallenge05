plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.banquemisr.challenge05"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.banquemisr.challenge05"
        minSdk = 28
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
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_7
        targetCompatibility = JavaVersion.VERSION_1_7
        isCoreLibraryDesugaringEnabled = true

    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
    buildFeatures {
        compose = true
        buildConfig =true

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8" // This version is better compatible with Kotlin 1.9.24
        //        kotlinCompilerExtensionVersion libs.versions.compose.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }



}

// Ensure Java compilation uses JVM 17
tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

            implementation(libs.hilt.compiler)

            implementation( libs.androidx.appcompat)
            implementation( libs.material)

            // Compose
            implementation( libs.bundles.compose)

            // Coil for image loading
            implementation( libs.coil.compose)

            // Lifecycle
            implementation( libs.bundles.lifecycle)

            // Hilt
            implementation( libs.hilt.android)
           // kapt libs.hilt.compiler
            implementation( libs.hilt.navigation.compose)

            // Retrofit + OkHttp
            implementation( libs.bundles.retrofit)

            // Room
            implementation( libs.bundles.room)
          //  kapt libs.room.compiler

            // Coroutines
            implementation( libs.bundles.coroutines)

            // Testing
            testImplementation( libs.junit)
            testImplementation( libs.mockk)
            testImplementation( libs.coroutines.test)
            testImplementation( libs.arch.testing)

          //  androidTestImplementation( libs.androidx.test.ext.junit)

        //   androidTestImplementation( libs.compose.ui.test)
        //    debugImplementation( libs.compose.ui.tooling)

}