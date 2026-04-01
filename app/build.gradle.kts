import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { load(it) }
    }
}

fun localConfig(name: String, defaultValue: String = ""): String {
    return (localProperties.getProperty(name) ?: defaultValue)
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
}

android {
    namespace = "com.example.quanlycudan_utehome"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.quanlycudan_utehome"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SMTP_HOST", "\"${localConfig("mail.host")}\"")
        buildConfigField("int", "SMTP_PORT", localConfig("mail.port", "587"))
        buildConfigField("String", "SMTP_USERNAME", "\"${localConfig("mail.username")}\"")
        buildConfigField("String", "SMTP_PASSWORD", "\"${localConfig("mail.password")}\"")
        buildConfigField("String", "SMTP_FROM_NAME", "\"${localConfig("mail.fromName", "UTEHome")}\"")
        buildConfigField("String", "SMTP_FROM_ADDRESS", "\"${localConfig("mail.fromAddress")}\"")
        buildConfigField("String", "SMTP_SECURITY", "\"${localConfig("mail.security", "starttls")}\"")
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
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += setOf(
                "META-INF/NOTICE.md",
                "META-INF/LICENSE.md"
            )
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // Room database
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    // ZXing for QR code
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
