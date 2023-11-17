pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NewsApp"
include(":app")
include(":feature:sources")
include(":feature:filters")
include(":core:ui")
include(":core:network")
include(":core:utils")
include(":data:filters")
include(":data:sources")
include(":feature:saved")
include(":core:database")
include(":feature:newslistbysource")
include(":data:news")
include(":paging")
include(":feature:article")
include(":feature:error")
