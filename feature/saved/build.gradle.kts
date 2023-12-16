plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.ksp)
    id(Plugins.kover)
}

android {
    namespace = "com.skid.saved"
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

    implementation(Dependencies.androidxCore)
    implementation(Dependencies.appcompat)
    implementation(Dependencies.material)

    implementation(Dependencies.dagger)
    ksp(Dependencies.daggerCompiler)

    implementation(Dependencies.cicerone)

    implementation(Dependencies.fragmentKtx)

    implementation(Dependencies.coil)

    implementation(Dependencies.swipeRefreshLayout)

    implementation(project(Dependencies.coreUi))
    implementation(project(Dependencies.coreUtils))

    implementation(project(Dependencies.dataFilters))
    implementation(project(Dependencies.dataNews))

    testImplementation(Dependencies.jUnit)
    androidTestImplementation(Dependencies.androidxJUnit)
    androidTestImplementation(Dependencies.androidxEspresso)
}