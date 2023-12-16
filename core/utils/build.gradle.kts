plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.kover)
}

android {
    namespace = "com.skid.utils"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    implementation(Dependencies.androidxCore)
    implementation(Dependencies.appcompat)
    implementation(Dependencies.material)

    implementation(Dependencies.fragmentKtx)

    implementation(Dependencies.rxJava)

    testImplementation(Dependencies.jUnit)
    testImplementation(Dependencies.mockk)
    testImplementation(Dependencies.jUnitParams)
    androidTestImplementation(Dependencies.androidxJUnit)
    androidTestImplementation(Dependencies.androidxEspresso)
}