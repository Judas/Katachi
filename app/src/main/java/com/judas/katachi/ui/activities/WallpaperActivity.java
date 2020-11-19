package com.judas.katachi.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.judas.katachi.R;
import com.judas.katachi.core.prefs.PreferenceHelper;
import com.judas.katachi.core.theme.KatachiTheme;
import com.judas.katachi.core.wallpaper.KatachiWallpaperService;
import com.xw.repo.BubbleSeekBar;

import static android.app.WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER;
import static android.app.WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT;
import static com.judas.katachi.core.prefs.PreferenceHelper.prefs;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;

public class WallpaperActivity extends BoardActivity {
    private static final String TAG = WallpaperActivity.class.getSimpleName();

    public static void start(final Context context) {
        log(DEBUG, TAG, "start");
        context.startActivity(new Intent(context, WallpaperActivity.class));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log(DEBUG, TAG, "onCreate");

        setContentView(R.layout.wallpaper);
        preview = findViewById(R.id.wallpaper_preview);

        // Toolbar
        final MaterialToolbar toolbar = findViewById(R.id.wallpaper_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        final PreferenceHelper prefs = prefs(this);

        // Move speed
        final BubbleSeekBar speedPicker = findViewById(R.id.move_speed_picker);
        speedPicker.getConfigBuilder()
                .min(0.5f)
                .max(10f)
                .progress(prefs.getWallpaperSpeed())
                .build();
        speedPicker.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(final BubbleSeekBar bubbleSeekBar, final int progress, final float progressFloat, final boolean fromUser) {
                prefs.setWallpaperSpeed(progressFloat);
            }
        });

        // End delay
        final BubbleSeekBar delayPicker = findViewById(R.id.end_delay_picker);
        delayPicker.getConfigBuilder()
                .min(1f)
                .max(10f)
                .progress(prefs.getWallpaperDelay())
                .build();
        delayPicker.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(final BubbleSeekBar bubbleSeekBar, final int progress, final float progressFloat, final boolean fromUser) {
                prefs.setWallpaperDelay(progressFloat);
            }
        });

        // Theme
        final KatachiTheme currentTheme = prefs.getWallPaperTheme(this);
        final AppCompatTextView themePicker = findViewById(R.id.theme_picker);
        themePicker.setText(currentTheme.name);
        themePicker.setOnClickListener(v -> showThemeDialog(theme -> {
            preview.setTheme(theme);
            preview.postInvalidate();
            themePicker.setText(theme.name);
            prefs.setWallpaperTheme(theme);
        }));

        // Submit button
        findViewById(R.id.wallpaper_submit).setOnClickListener(v -> {
            final Intent intent = new Intent(ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this, KatachiWallpaperService.class));
            startActivity(intent);
        });

        loadDefaultGame(currentTheme);
    }
}
