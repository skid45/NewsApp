plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "com.skid.headlines"
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

    val fragmentVersion = "1.6.2"
    //noinspection KtxExtensionAvailable
    implementation("androidx.fragment:fragment:$fragmentVersion")

    val rxJavaVersion = "3.1.8"
    implementation("io.reactivex.rxjava3:rxjava:$rxJavaVersion")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")

    implementation("com.squareup.picasso:picasso:2.8")

    val moxyVersion = "2.2.2"
    implementation ("com.github.moxy-community:moxy:$moxyVersion")
    annotationProcessor("com.github.moxy-community:moxy-compiler:$moxyVersion")
    implementation("com.github.moxy-community:moxy-androidx:$moxyVersion")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation(project(":core:ui"))
    implementation(project(":core:utils"))

    implementation(project(":data:filters"))
    implementation(project(":data:sources"))
    implementation(project(":data:news"))

    implementation(project(":paging"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}