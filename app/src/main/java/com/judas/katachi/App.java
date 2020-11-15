package com.judas.katachi;

import android.app.Application;

import com.judas.katachi.utils.log.Logger;

import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;

public class App extends Application {
    private static final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.setAppName(getString(R.string.app_name));
        log(DEBUG, TAG, "onCreate");
    }
}

// TODO License :
// SGF4J
// TILE DRAWABLE
// BUBBLE SEEKBAR