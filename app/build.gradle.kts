plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.pblmobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pblmobile"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Android UI Libraries
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation(libs.play.services.maps)
    implementation(libs.places)
    implementation(libs.androidx.activity)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("com.google.android.material:material:1.5.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("androidx.cardview:cardview:1.0.0")

    implementation("androidx.gridlayout:gridlayout:1.0.0") // Assuming libs.androidx.gridlayout resolves to this

    // Material Design Library
    implementation("com.google.android.material:material:1.9.0")

    // Core Android Libraries
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Lifecycle & Navigation Components
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.0") // Assuming libs.androidx.lifecycle.livedata.ktx resolves to this
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0") // Assuming libs.androidx.lifecycle.viewmodel.ktx resolves to this
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0") // Assuming libs.androidx.navigation.fragment.ktx resolves to this
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0") // Assuming libs.androidx.navigation.ui.ktx resolves to this

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // GIF Support
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.28")
    implementation("androidx.activity:activity-ktx:1.8.2")
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
