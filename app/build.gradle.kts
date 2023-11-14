plugins {
    id(Plugins.androidApp)
    id(Plugins.kotlin)
}

android {
    namespace = AppConfig.namespace
    buildToolsVersion = AppConfig.buildToolsVersion

    defaultConfig {
        applicationId = AppConfig.applicationId
        versionName = AppConfig.versionName
        versionCode = AppConfig.versionCode

        minSdk = AppConfig.minSdkVersion
        targetSdk = AppConfig.targetSdkVersion
        compileSdk = AppConfig.compileSdkVersion

        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true

        buildConfigField("String", "GO4GO_ENDPOINT", "<GO4GO_API_ENDPOINT>")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = AppConfig.javaVersion
        targetCompatibility = AppConfig.javaVersion
    }

    kotlinOptions {
        jvmTarget = AppConfig.javaVersion.toString()
    }

    lint {
        quiet = true
        abortOnError = true
        ignoreWarnings = false
    }

    signingConfigs {
        create("keystoreConfig") {
            keyAlias = "key-alias"
            keyPassword = "<KEYSTORE_ALIAS_PASSWORD>"
            storeFile = file("katachi.jks")
            storePassword = "<KEYSTORE_PASSWORD>"
        }
    }

    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("keystoreConfig")
        }
    }
}

dependencies {
    implementation(project(":sgf4k"))

    implementation(Dependencies.Library.androidxAppCompat)
    implementation(Dependencies.Library.colorPicker)
    implementation(Dependencies.Library.gson)
    implementation(Dependencies.Library.kotlinStdlib)
    implementation(Dependencies.Library.kotlinCoroutines)
    implementation(Dependencies.Library.material)
    implementation(Dependencies.Library.okHttpLoggingInterceptor)
    implementation(Dependencies.Library.retrofit)
    implementation(Dependencies.Library.retrofitGsonConverter)
}
