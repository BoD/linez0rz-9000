rootProject.name = "linez0rz-9000"

pluginManagement {
  repositories {
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
  }
}

plugins {
  // See https://splitties.github.io/refreshVersions
  id("de.fayard.refreshVersions").version("0.60.6")
}

include(
  ":engine",
  ":ui",
  ":desktopApp",
  ":androidApp",
  ":browserApp",
)
