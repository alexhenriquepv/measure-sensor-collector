import org.apache.tools.ant.util.JavaEnvUtils.VERSION_11

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "br.concy.demo"
    compileSdk = 34

    defaultConfig {
        applicationId = "br.concy.demo"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.play.services.wearable)
    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.concurrent.futures)
    implementation(libs.concurrent.futures.ktx)

    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)

    implementation(libs.horologist.compose.layout)
    implementation(libs.horologist.compose.material)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.navigation.fragment)
    implementation(libs.datastore.core.android)
    implementation(libs.datastore.preferences)
    implementation(libs.hilt.common)
    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.work)
    implementation(libs.lifecycle.service)

    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    implementation(files("libs/samsung-health-sensor-api-1.3.0.aar"))
    implementation(libs.androidx.navigation.compose)
    ksp(libs.androidx.room.compiler)

    ksp(libs.hilt.android.compiler)

    implementation(libs.health.services.client)
    implementation(libs.guava)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}

kapt {
    correctErrorTypes = true
}