plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.gms.google.service)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
    kotlin("kapt")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.chat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chat"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.play.services.places)
    implementation(libs.places)
    implementation(libs.play.services.fitness)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.core2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //mesibo sdk
    implementation(libs.mesiboAPI)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    //Hilt Dependency
    implementation(libs.hilt.android)
    kapt(libs.hilt.kapt.compiler)
    implementation(libs.hilt.navigation.compose)
    //OkHttp
    implementation(libs.okHttp)
    //retrofit Rest API
    implementation(libs.retrofit)
    //Gson converter factory
    implementation(libs.gson.converter.factory)
    //logging interceptor
    implementation(libs.logging.interceptor)
    //
    implementation(libs.roomdb)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.runtime.livedata)
    ksp(libs.room.compiler)
    //glide image loading
    implementation(libs.glide)
    implementation(libs.guava)
    //log
    implementation (libs.timber)


}
kapt {
    correctErrorTypes = true
}