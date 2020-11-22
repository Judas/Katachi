package com.judas.katachi.core.wallpaper;

import android.content.Context;

import com.judas.katachi.R;

public enum WallpaperContent {
    JOSEKI(R.string.wallpaper_content_joseki),
    PRO_GAMES(R.string.wallpaper_content_pro_games);

    private final int stringResId;

    WallpaperContent(final int stringResId) {
        this.stringResId = stringResId;
    }

    public String label(final Context context) {
        return context.getString(stringResId);
    }
}
