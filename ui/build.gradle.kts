import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
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
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser()
    compilerOptions {
      target.set("es2015")
    }
  }

  sourceSets {
    commonMain {
      dependencies {
        api(compose.runtime)
        api(compose.foundation)
        api(compose.material3)
        api(compose.ui)
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
