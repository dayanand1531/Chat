import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.konan.properties.loadProperties
import java.io.FileInputStream
import java.util.Properties

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
        val properties =  Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load( FileInputStream(localPropertiesFile))
        }
        val appKey = properties.getProperty("appKey","")
        buildConfigField("String", "AppKey", "\"${appKey}\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
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

    //Compose Ui
    implementation (libs.ui)
    implementation (libs.androidx.material)
    implementation (libs.ui.tooling.preview)
    debugImplementation (libs.androidx.ui.tooling)
    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.androidx.lifecycle.viewmodel.compose)
    implementation (libs.androidx.activity.compose)
    implementation (libs.maps.compose)

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
    //pull to refresh
    implementation (libs.accompanist.swiperefresh)
    //Constraint layout
    implementation (libs.androidx.constraintlayout.compose)
}
kapt {
    correctErrorTypes = true
}