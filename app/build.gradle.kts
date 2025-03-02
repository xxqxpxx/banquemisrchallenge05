plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
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
            isDebuggable = true
            isMinifyEnabled = false

            buildConfigField("String", "BASE_URL", "\"https://api.themoviedb.org/3/\"")
            buildConfigField("String", "IMAGE_BASE_URL", "\"https://image.tmdb.org/t/p/w500\"")
            buildConfigField("String", "AUTH_TOKEN", "\"Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlNTBhNGI1NGVhMzdjNThkZjI1YjcyMzQyZjljMmNkYiIsIm5iZiI6MTc0MDc4MzEzMi4zMjQsInN1YiI6IjY3YzIzZTFjMWYzZjgxYjYwN2EyNGQyMCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.w4zPNQ2NkTgDYUNh95La2d7B48pXjcWcWha4Y9subu0\"")


            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isDebuggable = true
            isMinifyEnabled = false

            buildConfigField("String", "BASE_URL", "\"https://api.themoviedb.org/3/\"")
            buildConfigField("String", "IMAGE_BASE_URL", "\"https://image.tmdb.org/t/p/w500\"")
            buildConfigField("String", "AUTH_TOKEN", "\"Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlNTBhNGI1NGVhMzdjNThkZjI1YjcyMzQyZjljMmNkYiIsIm5iZiI6MTc0MDc4MzEzMi4zMjQsInN1YiI6IjY3YzIzZTFjMWYzZjgxYjYwN2EyNGQyMCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.w4zPNQ2NkTgDYUNh95La2d7B48pXjcWcWha4Y9subu0\"")
        }
    }

    configurations.implementation{
        exclude(group = "com.intellij", module = "annotations")
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
        buildConfig = true

    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // Handle the annotation processor conflict
            pickFirsts += "META-INF/gradle/incremental.annotation.processors"
            // Additional common conflicts to handle
            pickFirsts += "META-INF/DEPENDENCIES"
            pickFirsts += "META-INF/LICENSE.md"
            pickFirsts += "META-INF/LICENSE-notice.md"
            pickFirsts += "META-INF/LICENSE.txt"
            pickFirsts += "META-INF/NOTICE.txt"
            pickFirsts += "META-INF/NOTICE"
            pickFirsts += "META-INF/LICENSE"
            // Exclude kotlin module info
            excludes += "META-INF/*.kotlin_module"
        }
    }


    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
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

    // Core & UI
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Compose
    implementation(libs.bundles.compose)

    // Coil for image loading
    implementation(libs.coil.compose)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Retrofit + OkHttp
    implementation(libs.bundles.retrofit)

    // Room
    implementation(libs.bundles.room)
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Coroutines
    implementation(libs.bundles.coroutines)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.arch.testing)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
