import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.kotlin.compose)
}

kotlin {
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser()
    binaries.executable()
    compilerOptions {
      target.set("es2015")
    }
  }

  sourceSets {
    wasmJsMain {
      dependencies {
        implementation(libs.kotlinx.browser)
        implementation(project(":ui"))
      }
    }
  }
}

// `./gradlew wasmJsBrowserDevelopmentRun --continuous` to run the dev server in continuous mode (should open `http://localhost:8080/`)
// `./gradlew wasmJsBrowserDevelopmentExecutableDistribution` to build the dev distribution, results are in `build/dist/js/developmentExecutable`
// `./gradlew wasmJsBrowserDistribution` to build the release distribution, results are in `build/dist/js/productionExecutable`
