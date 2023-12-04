plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.skid.newsapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.skid.newsapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", "${project.properties["API_KEY"]}")
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
        buildConfig = true
    }
}

dependencies {

    implementation(Dependencies.androidxCore)
    implementation(Dependencies.appcompat)
    implementation(Dependencies.material)
    implementation(Dependencies.constraintLayout)


    implementation(Dependencies.lottie)

    implementation(Dependencies.dagger)
    kapt(Dependencies.daggerCompiler)

    implementation(Dependencies.retrofit)
    implementation(Dependencies.retrofitGsonConverter)
    implementation(Dependencies.retrofitRxJava3Adapter)

    implementation(Dependencies.cicerone)

    implementation(Dependencies.fragmentKtx)

    implementation(Dependencies.lifecycle)

    implementation(Dependencies.roomRuntime)
    ksp(Dependencies.roomCompiler)
    implementation(Dependencies.roomKtx)

    implementation(Dependencies.rxJava)
    implementation(Dependencies.rxAndroid)

    implementation(Dependencies.moxy)
    kapt(Dependencies.moxyCompiler)
    implementation(Dependencies.moxyAndroidx)


    implementation(project(Dependencies.coreDatabase))
    implementation(project(Dependencies.coreNetwork))
    implementation(project(Dependencies.corePaging))
    implementation(project(Dependencies.coreUi))
    implementation(project(Dependencies.coreUtils))

    implementation(project(Dependencies.dataFilters))
    implementation(project(Dependencies.dataNews))
    implementation(project(Dependencies.dataSources))

    implementation(project(Dependencies.featureArticle))
    implementation(project(Dependencies.featureError))
    implementation(project(Dependencies.featureFilters))
    implementation(project(Dependencies.featureHeadlines))
    implementation(project(Dependencies.featureNewsListBySource))
    implementation(project(Dependencies.featureSaved))
    implementation(project(Dependencies.featureSources))

    testImplementation(Dependencies.jUnit)
    androidTestImplementation(Dependencies.androidxJUnit)
    androidTestImplementation(Dependencies.androidxEspresso)
}