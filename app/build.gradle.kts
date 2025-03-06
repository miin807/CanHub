plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.canhub.canhub"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.canhub.canhub"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "1.4"

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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.airbnb.android:lottie:6.6.0")
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    implementation ("com.google.firebase:firebase-firestore:24.9.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.recyclerview:recyclerview:1.3.0")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.google.code.gson:gson:2.8.9")

    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.google.android.material:material:1.5.0")


    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.google.code.gson:gson:2.10.1")


    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

}