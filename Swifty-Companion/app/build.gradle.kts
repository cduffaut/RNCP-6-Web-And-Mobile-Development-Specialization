import java.util.Properties
import java.io.FileReader

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Access to local.properties file at project root
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileReader(localPropertiesFile))
}

// Access to .env file at project root
val envProperties = Properties()
val envFile = rootProject.file(".env")
if (envFile.exists()) {
    envProperties.load(FileReader(envFile))
}

android {
    namespace = "com.cduffaut.swiftycompanion"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cduffaut.swiftycompanion"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false

            // Lire les valeurs depuis .env ou local.properties
            buildConfigField("String", "CLIENT_ID", "\"${envProperties.getProperty("CLIENT_ID") ?: localProperties.getProperty("client.id") ?: ""}\"")
            buildConfigField("String", "CLIENT_SECRET", "\"${envProperties.getProperty("CLIENT_SECRET") ?: localProperties.getProperty("client.secret") ?: ""}\"")
            buildConfigField("String", "REDIRECT_URI", "\"${envProperties.getProperty("REDIRECT_URI") ?: localProperties.getProperty("redirect.uri") ?: "com.cduffaut.swiftycompanion://oauth2callback"}\"")
            buildConfigField("String", "API_BASE_URL", "\"${envProperties.getProperty("API_BASE_URL") ?: localProperties.getProperty("api.base.url") ?: "https://api.intra.42.fr/"}\"")
            buildConfigField("String", "AUTH_URL", "\"${envProperties.getProperty("AUTH_URL") ?: localProperties.getProperty("auth.url") ?: "https://api.intra.42.fr/oauth/authorize"}\"")
            buildConfigField("String", "TOKEN_URL", "\"${envProperties.getProperty("TOKEN_URL") ?: localProperties.getProperty("token.url") ?: "https://api.intra.42.fr/oauth/token"}\"")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Même config que debug
            buildConfigField("String", "CLIENT_ID", "\"${envProperties.getProperty("CLIENT_ID") ?: localProperties.getProperty("client.id") ?: ""}\"")
            buildConfigField("String", "CLIENT_SECRET", "\"${envProperties.getProperty("CLIENT_SECRET") ?: localProperties.getProperty("client.secret") ?: ""}\"")
            buildConfigField("String", "REDIRECT_URI", "\"${envProperties.getProperty("REDIRECT_URI") ?: localProperties.getProperty("redirect.uri") ?: "com.cduffaut.swiftycompanion://oauth2callback"}\"")
            buildConfigField("String", "API_BASE_URL", "\"${envProperties.getProperty("API_BASE_URL") ?: localProperties.getProperty("api.base.url") ?: "https://api.intra.42.fr/"}\"")
            buildConfigField("String", "AUTH_URL", "\"${envProperties.getProperty("AUTH_URL") ?: localProperties.getProperty("auth.url") ?: "https://api.intra.42.fr/oauth/authorize"}\"")
            buildConfigField("String", "TOKEN_URL", "\"${envProperties.getProperty("TOKEN_URL") ?: localProperties.getProperty("token.url") ?: "https://api.intra.42.fr/oauth/token"}\"")
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // .env
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Retrofit pour les appels API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp pour la gestion des requêtes HTTP
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines pour la gestion asynchrone
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Coil pour le chargement des images
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Dépendances de test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}