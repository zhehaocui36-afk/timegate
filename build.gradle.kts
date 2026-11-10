// 顶层构建文件：只声明插件版本，不写具体配置。
// 单模块工程（ADR-0005），业务配置都在 app/build.gradle.kts。
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
