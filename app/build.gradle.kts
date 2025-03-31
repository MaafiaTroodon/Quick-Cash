import java.util.Properties

plugins {
    alias(libs.plugins.android.application) // Android application plugin
    alias(libs.plugins.google.gms.google.services) // Google Services plugin for Firebase
}

android {
    namespace = "com.example.quickcash" // Application namespace
    compileSdk = 35 // Compile SDK version
    android.buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.example.quickcash" // Application ID
        minSdk = 30 // Minimum SDK version
        targetSdk = 35 // Target SDK version
        versionCode = 1 // Version code
        versionName = "1.0" // Version name

        // Test instrumentation runner for Android tests
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Read the API key from local.properties and make it available in BuildConfig
        val properties = Properties() // Initialize Properties object
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "MAPS_API_KEY", "\"${properties.getProperty("MAPS_API_KEY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Disable code shrinking for release builds
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), // Default ProGuard rules
                "proguard-rules.pro" // Custom ProGuard rules
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // Java 11 compatibility
        targetCompatibility = JavaVersion.VERSION_11 // Java 11 compatibility
    }
    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
        }
    }
}

dependencies {
    // App dependencies
    implementation(libs.appcompat) // AndroidX AppCompat
    implementation(libs.material) // Material Design components
    implementation(libs.activity) // AndroidX Activity
    implementation(libs.constraintlayout) // ConstraintLayout
    implementation(libs.firebase.database) // Firebase Realtime Database
    implementation(libs.firebase.auth) // Firebase Authentication
    implementation(libs.espresso.intents) // Espresso Intents for testing
    implementation(libs.espresso.contrib) // Espresso Contrib for testing
    implementation("com.google.android.material:material:1.9.0") // Material Design library
    implementation(libs.recyclerview) // RecyclerView
    implementation(libs.play.services.maps) // Google Maps
    implementation(libs.play.services.location) // Google Location Services
    implementation(libs.uiautomator)
<<<<<<< app/build.gradle.kts
    implementation("com.paypal.sdk:paypal-android-sdk:2.16.0")
    implementation(libs.firebase.messaging)
    implementation(libs.volley)
    implementation("com.google.auth:google-auth-library-oauth2-http:1.14.0")
>>>>>>> app/build.gradle.kts

    // Unit testing dependencies
    testImplementation("junit:junit:4.13.2") // JUnit for unit tests
    testImplementation("org.mockito:mockito-core:4.0.0") // Mockito for mocking in unit tests
    testImplementation("org.mockito:mockito-inline:4.0.0") // Mockito inline for mocking final classes
    testImplementation("org.robolectric:robolectric:4.10")

    // AndroidTest dependencies (instrumentation tests)
    androidTestImplementation("androidx.test.ext:junit:1.1.3") // AndroidX JUnit for instrumentation tests
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0") // Espresso for UI testing
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.4.0") // Espresso Intents for testing
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0") // Espresso Contrib for testing
}
