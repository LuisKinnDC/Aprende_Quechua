plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.app.aprendequechua"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.aprendequechua"
        minSdk = 24
        targetSdk = 35
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    dependencies {
        // Unit tests
        testImplementation("junit:junit:4.13.2")

        // Mockito para mocks
        testImplementation ("org.mockito:mockito-core:4.8.0")

        // Para pruebas con Kotlin + Mockito
        testImplementation ("org.mockito.kotlin:mockito-kotlin:4.1.0")

        androidTestImplementation ("androidx.test.espresso:espresso-contrib:3.5.0")

        // Instrumented tests
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
        androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.0")



    }

    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.core.ktx)
    implementation(libs.androidx.ui.test.android)
}
