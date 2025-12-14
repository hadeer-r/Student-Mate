pluginManagement {
    val flutterSdkPath = java.util.Properties().apply {
        val propertiesFile = java.io.File("local.properties")
        if (propertiesFile.exists()) {
            load(java.io.FileInputStream(propertiesFile))
        }
    }.getProperty("flutter.sdk")


    if (flutterSdkPath != null) {
        includeBuild("$flutterSdkPath/packages/flutter_tools/gradle")
    } else {
        println("Warning: flutter.sdk not found in local.properties")
    }

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // PREFER_PROJECT is correct for Flutter integration
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "StudentMate"
include(":app")

// 3. Include the Flutter module script
apply(from = java.io.File("gpa_module/.android/include_flutter.groovy"))