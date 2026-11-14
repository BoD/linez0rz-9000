import com.android.build.gradle.ProguardFiles.getDefaultProguardFile

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "org.jraf.linez0rz9000"
  compileSdk {
    version = release(libs.versions.android.compileSdk.major.get().toInt()) {
      minorApiLevel = libs.versions.android.compileSdk.minor.get().toInt()
    }
  }
  defaultConfig {
    applicationId = "org.jraf.linez0rz9000"
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()
    versionCode = 1
    versionName = rootProject.version.toString()
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }

  signingConfigs {
    create("release") {
      storeFile = file(System.getenv("SIGNING_STORE_PATH") ?: ".")
      storePassword = System.getenv("SIGNING_STORE_PASSWORD")
      keyAlias = System.getenv("SIGNING_KEY_ALIAS")
      keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
    }
  }

  buildTypes {
    getByName("debug") {
      applicationIdSuffix = ".debug"
    }

    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt", layout.buildDirectory),
        "proguard-rules.pro"
      )
      signingConfig = signingConfigs.getByName("release")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}

dependencies {
  implementation(libs.composeMultiplatform.material3)
  implementation(libs.composeMultiplatform.ui.tooling.preview)
  implementation(libs.androidx.activity.compose)

  implementation(project(":ui"))

  debugImplementation(libs.composeMultiplatform.ui.tooling)
}
