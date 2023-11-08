plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "com.skid.sources"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")

    val daggerVersion = "2.48.1"
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    val ciceroneVersion = "7.1"
    implementation("com.github.terrakok:cicerone:$ciceroneVersion")

    val fragmentVersion = "1.6.2"
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")

    val lifecycleVersion = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")


    implementation(project(":core:ui"))
    implementation(project(":core:utils"))

    implementation(project(":data:sources"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}