plugins {
  alias(libs.plugins.android.application).apply(false)
  alias(libs.plugins.android.kmp.library).apply(false)
  alias(libs.plugins.composeMultiplatform.hotReload).apply(false)
  alias(libs.plugins.composeMultiplatform).apply(false)
  alias(libs.plugins.kotlin.compose).apply(false)
  alias(libs.plugins.kotlin.multiplatform).apply(false)
}

group = "org.jraf"
version = "1.0.0"
