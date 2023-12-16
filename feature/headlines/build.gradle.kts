plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    kotlin(Plugins.kapt)
    id(Plugins.ksp)
    id(Plugins.kover)
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

    implementation(Dependencies.androidxCore)
    implementation(Dependencies.appcompat)
    implementation(Dependencies.material)

    implementation(Dependencies.dagger)
    ksp(Dependencies.daggerCompiler)

    implementation(Dependencies.fragment)

    implementation(Dependencies.rxJava)
    implementation(Dependencies.rxAndroid)

    implementation(Dependencies.coil)

    implementation(Dependencies.moxy)
    kapt(Dependencies.moxyCompiler)
    implementation(Dependencies.moxyAndroidx)

    implementation(Dependencies.swipeRefreshLayout)

    implementation(project(Dependencies.coreUi))
    implementation(project(Dependencies.coreUtils))
    implementation(project(Dependencies.corePaging))

    implementation(project(Dependencies.dataFilters))
    implementation(project(Dependencies.dataNews))
    implementation(project(Dependencies.dataSources))


    testImplementation(Dependencies.jUnit)
    testImplementation(Dependencies.mockk)
    androidTestImplementation(Dependencies.androidxJUnit)
    androidTestImplementation(Dependencies.androidxEspresso)
}