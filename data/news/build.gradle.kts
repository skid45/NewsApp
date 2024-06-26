plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.ksp)
    id(Plugins.kover)
}

android {
    namespace = "com.skid.news"
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

    implementation(Dependencies.dagger)
    ksp(Dependencies.daggerCompiler)

    implementation(Dependencies.retrofit)
    implementation(Dependencies.retrofitGsonConverter)
    implementation(Dependencies.retrofitRxJava3Adapter)

    implementation(Dependencies.rxJava)
    implementation(Dependencies.rxAndroid)

    implementation(project(Dependencies.coreDatabase))
    implementation(project(Dependencies.coreNetwork))
    implementation(project(Dependencies.corePaging))
    implementation(project(Dependencies.coreUi))
    implementation(project(Dependencies.coreUtils))

    testImplementation(Dependencies.jUnit)
    testImplementation(Dependencies.mockk)
    androidTestImplementation(Dependencies.androidxJUnit)
    androidTestImplementation(Dependencies.androidxEspresso)
}