plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.quickcash"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.quickcash"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Removed testInstrumentationRunner for unit tests
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // App dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.espresso.intents)
    implementation(libs.espresso.contrib)
    implementation("com.google.android.material:material:1.9.0")
    testImplementation ("org.mockito:mockito-core:4.0.0")
    testImplementation ("org.mockito:mockito-inline:4.0.0")



    // Unit testing dependencies
    testImplementation("junit:junit:4.13.2") // For JUnit tests
    testImplementation("org.mockito:mockito-core:3.9.0") // For Mockito

    // AndroidTest dependencies (instrumentation tests)
    androidTestImplementation("androidx.test.ext:junit:1.1.3") // For Android Instrumentation Tests
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0") // For Espresso UI testing

    // Optional: Mocking for inline mocking support
    testImplementation("org.mockito:mockito-inline:3.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
}
