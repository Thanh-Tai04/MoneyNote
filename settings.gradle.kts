// Tệp: settings.gradle.kts (ở thư mục gốc)

pluginManagement {
    repositories {
        // BA KHO LƯU TRỮ QUAN TRỌNG NHẤT CHO PLUGIN
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // KHO LƯU TRỮ CHO THƯ VIỆN
        google()
        mavenCentral()
    }
}

rootProject.name = "MoneyNote" // Tên dự án của bạn
include(":app")