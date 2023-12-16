// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(Plugins.androidApplication) version Versions.AGPVersion apply false
    id(Plugins.kotlinAndroid) version Versions.kotlinVersion apply false
    id(Plugins.androidLibrary) version Versions.AGPVersion apply false
    id(Plugins.kotlinJVM) version Versions.kotlinVersion apply false
    id(Plugins.ksp) version Versions.kspVersion apply false
    id(Plugins.kover) version Versions.koverVersion apply true
}