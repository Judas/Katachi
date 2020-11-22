package com.judas.katachi.core.prefs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.judas.katachi.core.theme.KatachiTheme;
import com.judas.katachi.core.theme.PresetTheme;
import com.judas.katachi.core.wallpaper.WallpaperContent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.judas.katachi.core.theme.PresetTheme.CLASSIC;
import static com.judas.katachi.core.wallpaper.WallpaperContent.JOSEKI;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;
import static java.util.Collections.emptyList;

public class PreferenceHelper {
    private static final String TAG = PreferenceHelper.class.getSimpleName();
    public static final String ACTION_WALLPAPER_SETTINGS_CHANGED = PreferenceHelper.class.getName() + ".ACTION_WALLPAPER_SETTINGS_CHANGED";
    private static final String PREFERENCES_FILENAME = "preferences";
    private static final String KEY_THEME_TITLES = "themes-titles";
    private static final String KEY_WALLPAPER_SPEED = "wallpaper-speed";
    private static final String KEY_WALLPAPER_DELAY = "wallpaper-delay";
    private static final String KEY_WALLPAPER_THEME = "wallpaper-theme";
    private static final String KEY_WALLPAPER_CONTENT = "wallpaper-content";

    private static PreferenceHelper instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();
    private final LocalBroadcastManager broadcastManager;

    public static PreferenceHelper prefs(final Context context) {
        if (instance == null) {
            instance = new PreferenceHelper(context);
        }
        return instance;
    }

    private PreferenceHelper(final Context context) {
        log(DEBUG, TAG, "PreferenceHelper");

        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILENAME, MODE_PRIVATE);
        broadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public void saveUserTheme(final KatachiTheme theme) {
        log(DEBUG, TAG, "saveUserTheme " + theme.name);

        sharedPreferences.edit().putString(theme.name, gson.toJson(theme)).apply();

        Set<String> titles = sharedPreferences.getStringSet(KEY_THEME_TITLES, null);
        if (titles == null) {
            titles = new HashSet<>();
        }
        titles.add(theme.name);
        sharedPreferences.edit().putStringSet(KEY_THEME_TITLES, titles).apply();
    }

    public List<KatachiTheme> getUserThemes() {
        log(DEBUG, TAG, "getUserThemes");

        final Set<String> titles = sharedPreferences.getStringSet(KEY_THEME_TITLES, null);
        if (titles == null) {
            return emptyList();
        }

        final List<KatachiTheme> themes = new ArrayList<>();
        for (final String title : titles) {
            final String themeJson = sharedPreferences.getString(title, null);
            if (themeJson != null) {
                themes.add(gson.fromJson(themeJson, KatachiTheme.class));
            }
        }

        return themes;
    }

    public float getWallpaperSpeed() {
        log(DEBUG, TAG, "getWallpaperSpeed");
        return sharedPreferences.getFloat(KEY_WALLPAPER_SPEED, 1f);
    }

    public void setWallpaperSpeed(final float speed) {
        log(DEBUG, TAG, "setWallpaperSpeed " + speed);
        sharedPreferences.edit().putFloat(KEY_WALLPAPER_SPEED, speed).apply();
        broadcastManager.sendBroadcast(new Intent(ACTION_WALLPAPER_SETTINGS_CHANGED));
    }

    public float getWallpaperDelay() {
        log(DEBUG, TAG, "getWallpaperDelay");
        return sharedPreferences.getFloat(KEY_WALLPAPER_DELAY, 5f);
    }

    public void setWallpaperDelay(final float delay) {
        log(DEBUG, TAG, "setWallpaperDelay " + delay);
        sharedPreferences.edit().putFloat(KEY_WALLPAPER_DELAY, delay).apply();
        broadcastManager.sendBroadcast(new Intent(ACTION_WALLPAPER_SETTINGS_CHANGED));
    }

    public void setWallpaperTheme(final KatachiTheme theme) {
        log(DEBUG, TAG, "setWallpaperTheme " + theme.name);
        sharedPreferences.edit().putString(KEY_WALLPAPER_THEME, theme.name).apply();
        broadcastManager.sendBroadcast(new Intent(ACTION_WALLPAPER_SETTINGS_CHANGED));
    }

    public KatachiTheme getWallPaperTheme(final Context context) {
        log(DEBUG, TAG, "getWallPaperTheme");

        final String themeName = sharedPreferences.getString(KEY_WALLPAPER_THEME, CLASSIC.name());
        try {
            return new KatachiTheme(context, PresetTheme.valueOf(themeName.toUpperCase()));
        } catch (final IllegalArgumentException e) {
            for (final KatachiTheme theme : getUserThemes()) {
                if (theme.name.equals(themeName)) {
                    return new KatachiTheme(context, theme);
                }
            }
            return new KatachiTheme(context, CLASSIC);
        }
    }

    public void setWallpaperContent(final WallpaperContent content) {
        log(DEBUG, TAG, "setWallpaperContent " + content.name());
        sharedPreferences.edit().putString(KEY_WALLPAPER_CONTENT, content.name()).apply();
        broadcastManager.sendBroadcast(new Intent(ACTION_WALLPAPER_SETTINGS_CHANGED));
    }

    public WallpaperContent getWallPaperContent() {
        log(DEBUG, TAG, "getWallPaperContent");

        final String contentName = sharedPreferences.getString(KEY_WALLPAPER_CONTENT, JOSEKI.name());
        try {
            return WallpaperContent.valueOf(contentName);
        } catch (final IllegalArgumentException e) {
            return JOSEKI;
        }
    }
}
