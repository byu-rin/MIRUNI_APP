plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.convention.hilt)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.convention.retrofit)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.miruni"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.miruni"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"6060a4309ed695fc355f3fc8c292cc81\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
        buildConfig = true
    }


}

dependencies {
    implementation(project(":feature:home"))
    implementation(project(":feature:splash"))
    implementation(project(":feature:aiplanner"))
    implementation(project(":feature:calendar"))
    implementation(project(":feature:home"))
    implementation(project(":feature:login"))
    implementation(project(":feature:mypage"))
    implementation(project(":feature:onboard"))
    implementation(project(":feature:pwreset"))
    implementation(project(":feature:signup"))
    implementation(project(":feature:splash"))
    implementation(project(":feature:survey"))
    implementation(project(":core"))

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)

    // material for Icons. 지워도 상관x
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    // Splash (Android 12 이하 지원)
    implementation(libs.androidx.core.splashscreen)

    // The Preferences DataStore library
    implementation(libs.androidx.datastore.preferences)

    // compose dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // kakao dependencies
    implementation(libs.kakao.sdk.v2.common)

    // test dependencies
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.junit)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}