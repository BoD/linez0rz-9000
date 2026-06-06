import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.kmp.library)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.composeMultiplatform.hotReload)
}

kotlin {
  jvm()

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser()
    compilerOptions {
      target.set("es2015")
    }
  }

  android {
    namespace = "org.jraf.linez0rz9000.ui"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    androidResources {
      enable = true
    }
  }

  sourceSets {
    commonMain {
      dependencies {
        api(libs.composeMultiplatform.runtime)
        api(libs.composeMultiplatform.foundation)
        api(libs.composeMultiplatform.material3)
        api(libs.composeMultiplatform.ui)
        implementation(libs.composeMultiplatform.components.resources)
        implementation(libs.composeMultiplatform.ui.tooling.preview)

        api(project(":engine"))
      }
    }
  }
}

tasks.withType<KotlinCompile>().all {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_17)
  }
}
