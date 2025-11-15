import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.composeHotReload)
}

kotlin {
  jvm("desktop")

  sourceSets {
    val desktopMain by getting

    desktopMain.apply {
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation(libs.kotlinx.coroutines.swing)

        implementation(project(":ui"))
      }
    }
  }
}

compose.desktop {
  application {
    mainClass = "org.jraf.linez0rz9000.ui.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "org.jraf.linez0rz9000"
      packageVersion = rootProject.version.toString()
    }
  }
}

tasks.withType<KotlinCompile>().all {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_17)
  }
}
