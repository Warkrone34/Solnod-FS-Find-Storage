// Eklentilerin (Plugins) indirileceği uzak depoları (repositories) belirliyoruz.
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// Projedeki kütüphanelerin (Dependencies) indirileceği depoları belirliyoruz.
// FAIL_ON_PROJECT_REPOS güvenlik içindir; alt modüllerin kendi depolarını tanımlamasını engeller, tek merkezden yönetimi zorlar.
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FindAndStorage"
include(":app")