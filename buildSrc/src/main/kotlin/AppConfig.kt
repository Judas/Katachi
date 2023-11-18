import org.gradle.api.JavaVersion

object AppConfig {
    const val minSdkVersion = 26
    const val targetSdkVersion = 34
    const val compileSdkVersion = 34
    const val buildToolsVersion = "34.0.0"
    val javaVersion = JavaVersion.VERSION_11

    const val namespace = "com.judas.katachi"
    const val applicationId = "com.judas.katachi"
    const val versionName = "1.3"
    const val versionCode = 4
}
