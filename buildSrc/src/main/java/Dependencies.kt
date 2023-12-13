object Dependencies {

    const val androidxCore = "androidx.core:core-ktx:${Versions.androidxCoreVersion}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompatVersion}"
    const val material = "com.google.android.material:material:${Versions.materialVersion}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayoutVersion}"


    const val jUnit = "junit:junit:${Versions.jUnitVersion}"
    const val androidxJUnit = "androidx.test.ext:junit:${Versions.androidxJUnitVersion}"
    const val androidxEspresso =
        "androidx.test.espresso:espresso-core:${Versions.androidxEspressoVersion}"
    const val mockk = "io.mockk:mockk:${Versions.mockkVersion}"

    const val lottie = "com.airbnb.android:lottie:${Versions.lottieVersion}"

    const val dagger = "com.google.dagger:dagger:${Versions.daggerVersion}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.daggerVersion}"
    const val javaxInject = "javax.inject:javax.inject:${Versions.javaxInject}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofitVersion}"
    const val retrofitGsonConverter =
        "com.squareup.retrofit2:converter-gson:${Versions.retrofitVersion}"
    const val retrofitRxJava3Adapter =
        "com.squareup.retrofit2:adapter-rxjava3:${Versions.retrofitVersion}"

    const val cicerone = "com.github.terrakok:cicerone:${Versions.ciceroneVersion}"

    const val fragment = "androidx.fragment:fragment:${Versions.fragmentVersion}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.fragmentVersion}"

    const val lifecycle = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycleVersion}"


    const val roomRuntime = "androidx.room:room-runtime:${Versions.roomVersion}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.roomVersion}"
    const val roomRxJava3 = "androidx.room:room-rxjava3:${Versions.roomVersion}"

    const val rxJava = "io.reactivex.rxjava3:rxjava:${Versions.rxJavaVersion}"
    const val rxAndroid = "io.reactivex.rxjava3:rxandroid:${Versions.rxAndroidVersion}"

    const val moxy = "com.github.moxy-community:moxy:${Versions.moxyVersion}"
    const val moxyCompiler = "com.github.moxy-community:moxy-compiler:${Versions.moxyVersion}"
    const val moxyAndroidx = "com.github.moxy-community:moxy-androidx:${Versions.moxyVersion}"

    const val coil = "io.coil-kt:coil:${Versions.coilVersion}"

    const val swipeRefreshLayout =
        "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefreshLayoutVersion}"

    const val coreDatabase = ":core:database"
    const val coreNetwork = ":core:network"
    const val corePaging = ":core:paging"
    const val coreUi = ":core:ui"
    const val coreUtils = ":core:utils"

    const val dataFilters = ":data:filters"
    const val dataNews = ":data:news"
    const val dataSources = ":data:sources"

    const val featureArticle = ":feature:article"
    const val featureError = ":feature:error"
    const val featureFilters = ":feature:filters"
    const val featureHeadlines = ":feature:headlines"
    const val featureNewsListBySource = ":feature:newslistbysource"
    const val featureSaved = ":feature:saved"
    const val featureSources = ":feature:sources"
}