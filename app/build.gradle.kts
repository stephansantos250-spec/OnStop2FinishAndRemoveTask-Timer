plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.github.takusan23.onstop2finishandremovetask"
    compileSdk {
        version = release(37)
    }

    buildToolsVersion = "37.0.0"

    defaultConfig {
        applicationId = "io.github.takusan23.onstop2finishandremovetask"
        minSdk = 31
        targetSdk = 37
        versionCode = 10
        versionName = "2.0.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            applicationIdSuffix = ".timer"
            versionNameSuffix = "-timer.3"
            optimization {
                enable = true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.all {
            it.maxHeapSize = "1024m"
            it.maxParallelForks = 1
            it.systemProperty("robolectric.dependency.repo.url", "https://repo.maven.apache.org/maven2")
            it.systemProperty("glass.screenshots.dir", layout.buildDirectory.dir("screenshots").get().asFile.absolutePath)
        }
    }
    buildFeatures {
        compose = true
        aidl = true
    }
    androidResources {
        generateLocaleConfig = true
    }
    // F-Droid で配信するため
    // https://gitlab.com/fdroid/fdroiddata/-/work_items/3330
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {

    // shizuku
    implementation(libs.shizuku.api)
    // Add this line if you want to support Shizuku
    implementation(libs.shizuku.provider)
    implementation(libs.hiddenapibypass)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    testImplementation("org.robolectric:robolectric:4.17")
    testImplementation("androidx.test:core:1.7.0")
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}