package com.judas.katachi.core.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.judas.katachi.core.theme.KatachiTheme;
import com.judas.katachi.ui.activities.EditActivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;
import static java.util.Collections.emptyMap;

public class PreferenceHelper {
    private static final String TAG = EditActivity.class.getSimpleName();
    private static final String PREFERENCES_FILENAME = "preferences";
    private static final String KEY_THEME_TITLES = "themes-titles";

    private static PreferenceHelper instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public static PreferenceHelper prefs(final Context context) {
        if (instance == null) {
            instance = new PreferenceHelper(context);
        }
        return instance;
    }

    private PreferenceHelper(final Context context) {
        log(DEBUG, TAG, "PreferenceHelper");

        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILENAME, MODE_PRIVATE);
    }

    public void saveUserTheme(final String title, final KatachiTheme theme) {
        log(DEBUG, TAG, "saveUserTheme " + title);

        sharedPreferences.edit().putString(title, gson.toJson(theme)).apply();

        Set<String> titles = sharedPreferences.getStringSet(KEY_THEME_TITLES, null);
        if (titles == null) {
            titles = new HashSet<>();
        }
        titles.add(title);
        sharedPreferences.edit().putStringSet(KEY_THEME_TITLES, titles).apply();
    }

    public Map<String, KatachiTheme> getUserThemes() {
        log(DEBUG, TAG, "getUserThemes");

        final Set<String> titles = sharedPreferences.getStringSet(KEY_THEME_TITLES, null);
        if (titles == null) {
            return emptyMap();
        }

        final Map<String, KatachiTheme> themes = new HashMap<>();
        for (final String title : titles) {
            final String themeJson = sharedPreferences.getString(title, null);
            if (themeJson != null) {
                themes.put(title, gson.fromJson(themeJson, KatachiTheme.class));
            }
        }

        return themes;
    }
}
