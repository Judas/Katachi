package com.judas.katachi

import android.app.Application
import com.judas.katachi.utility.CloseableCoroutineScope
import com.judas.katachi.utility.Logger
import com.judas.katachi.utility.PreferencesProvider
import com.judas.katachi.utility.log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Logger.appName = getString(R.string.app_name)
        Logger.logLevel = if (BuildConfig.DEBUG) Logger.Level.VERBOSE else Logger.Level.WARNING

        PreferencesProvider.init(this)
    }
}
