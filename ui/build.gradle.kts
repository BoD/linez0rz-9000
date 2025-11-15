import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.composeHotReload)
}

kotlin {
  jvm()
  androidTarget()

  sourceSets {
    commonMain {
      dependencies {
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.ui)
        implementation(compose.components.resources)
        implementation(compose.components.uiToolingPreview)

        api(project(":engine"))
      }
    }
  }
}

android {
  namespace = "org.jraf.linez0rz9000.ui"
  compileSdk = libs.versions.android.compileSdk.get().toInt()
}

tasks.withType<KotlinCompile>().all {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_17)
  }
}
