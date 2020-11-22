package com.judas.katachi.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.judas.katachi.R;
import com.judas.katachi.core.prefs.PreferenceHelper;
import com.judas.katachi.core.theme.KatachiTheme;
import com.judas.katachi.core.wallpaper.KatachiWallpaperService;
import com.judas.katachi.core.wallpaper.WallpaperContent;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.List;

import static android.app.WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER;
import static android.app.WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT;
import static com.judas.katachi.core.prefs.PreferenceHelper.prefs;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;

public class WallpaperActivity extends BoardActivity {
    public interface OnContentSelectedListener {
        void onContentSelected(WallpaperContent content);
    }

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

        // Theme
        final WallpaperContent currentContent = prefs.getWallPaperContent();
        final AppCompatTextView contentPicker = findViewById(R.id.content_picker);
        contentPicker.setText(currentContent.label(this));
        contentPicker.setOnClickListener(v -> showContentDialog(content -> {
            contentPicker.setText(content.label(this));
            prefs.setWallpaperContent(content);
        }));

        // Submit button
        findViewById(R.id.wallpaper_submit).setOnClickListener(v -> {
            final Intent intent = new Intent(ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this, KatachiWallpaperService.class));
            startActivity(intent);
        });

        loadDefaultGame(currentTheme);
    }

    protected void showContentDialog(final OnContentSelectedListener listener) {
        log(DEBUG, TAG, "showContentDialog");

        final List<WallpaperContent> contents = new ArrayList<>();
        final List<String> contentNames = new ArrayList<>();
        for (final WallpaperContent c : WallpaperContent.values()) {
            contents.add(c);
            contentNames.add(c.label(this));
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contentNames);

        new AlertDialog.Builder(this)
                .setTitle(R.string.wallpaper_content)
                .setAdapter(adapter, (dialog, which) -> {
                    final int index = contentNames.indexOf(adapter.getItem(which));
                    final WallpaperContent content = contents.get(index);
                    if (listener != null) {
                        listener.onContentSelected(content);
                    }
                    dialog.dismiss();
                })
                .show();
    }
}
