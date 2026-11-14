import org.gradle.kotlin.dsl.implementation
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.composeMultiplatform.hotReload)
}

kotlin {
  jvm("desktop")

  sourceSets {
    val desktopMain = getByName("desktopMain")

    desktopMain.apply {
      dependencies {
        implementation(libs.composeMultiplatform.ui)
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
