import org.gradle.api.JavaVersion

object AppConfig {
    const val minSdkVersion = 21
    const val targetSdkVersion = 33
    const val compileSdkVersion = 33
    const val buildToolsVersion = "33.0.2"
    val javaVersion = JavaVersion.VERSION_11

    const val namespace = "com.judas.katachi"
    const val applicationId = "com.judas.katachi"
    const val versionName = "1.3"
    const val versionCode = 4
}
