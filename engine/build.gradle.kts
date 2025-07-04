plugins {
  alias(libs.plugins.kotlinMultiplatform)
}

// Generate a Version.kt file with a constant for the version name
val generateVersionKtTask = tasks.register("generateVersionKt") {
  val outputDir = layout.buildDirectory.dir("generated/source/kotlin").get().asFile
  outputs.dir(outputDir)
  doFirst {
    val outputWithPackageDir = File(outputDir, "org/jraf/linez0rz9000/engine").apply { mkdirs() }
    File(outputWithPackageDir, "Version.kt").writeText(
      """
        package org.jraf.linez0rz9000.engine
        const val VERSION = "v${rootProject.version}"
      """.trimIndent()
    )
  }
}

kotlin {
  jvm()

  sourceSets {
    commonMain {
      kotlin.srcDir(generateVersionKtTask)

      dependencies {
      }
    }
  }
}
