object Dependencies {
    object Build {
        const val gradle = "com.android.tools.build:gradle:${Versions.gradle}"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    }

    object Library {
        const val androidxAppCompat = "androidx.appcompat:appcompat:${Versions.androidxAppCompat}"
        const val androidxConstraintLayout =
            "androidx.constraintlayout:constraintlayout:${Versions.androidxConstraintLayout}"
        const val bubbleSeekBar = "com.xw.repo:bubbleseekbar:${Versions.bubbleSeekBar}"
        const val colorPicker = "com.github.QuadFlask:colorpicker:${Versions.colorPicker}"
        const val gson = "com.google.code.gson:gson:${Versions.gson}"
        const val insetter = "dev.chrisbanes.insetter:insetter:${Versions.insetter}"
        const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
        const val kotlinCoroutines =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutines}"
        const val okHttpLoggingInterceptor =
            "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"
        const val material = "com.google.android.material:material:${Versions.material}"
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val retrofitGsonConverter =
            "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    }
}
