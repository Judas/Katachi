package com.judas.katachi.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.judas.katachi.R;
import com.judas.katachi.core.theme.KatachiTheme;
import com.xw.repo.BubbleSeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.graphics.Bitmap.CompressFormat.PNG;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.Level.ERROR;
import static com.judas.katachi.utils.log.Logger.log;
import static com.judas.katachi.utils.log.Logger.logError;
import static com.judas.katachi.utils.view.ViewUtils.showSnackbar;
import static com.judas.katachi.utils.view.ViewUtils.showToast;
import static java.lang.System.currentTimeMillis;

public class SgfToPngActivity extends BoardActivity {
    private static final String TAG = SgfToPngActivity.class.getSimpleName();
    private static final String EXTRA_FILE_URI = TAG + ".EXTRA_FILE_URI";

    private BubbleSeekBar movePicker;

    public static void start(final Context context, final Uri sgfFileUri) {
        log(DEBUG, TAG, "start");

        final Intent intent = new Intent(context, SgfToPngActivity.class);
        intent.putExtra(EXTRA_FILE_URI, sgfFileUri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log(DEBUG, TAG, "onCreate");

        setContentView(R.layout.sgf_to_png);

        // Toolbar
        final MaterialToolbar toolbar = findViewById(R.id.sgf_to_png_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.sgf_to_png_menu_themes) {
                showThemeDialog(theme -> {
                    preview.setTheme(theme);
                    preview.postInvalidate();
                });
                return true;
            } else if (itemId == R.id.sgf_to_png_menu_export) {
                export();
                return true;
            }
            return false;
        });

        preview = findViewById(R.id.sgf_to_png_preview);
        movePicker = findViewById(R.id.move_picker);

        final KatachiTheme theme = savedInstanceState != null ? savedInstanceState.getParcelable(EXTRA_THEME) : null;
        final int move = savedInstanceState != null ? savedInstanceState.getInt(EXTRA_MOVE, -1) : -1;
        loadGame(getIntent().getParcelableExtra(EXTRA_FILE_URI), theme, move);
    }

    private void loadGame(final Uri sgfFileUri, final KatachiTheme theme, final int move) {
        log(DEBUG, TAG, "loadGame");

        try {
            final InputStream is = getContentResolver().openInputStream(sgfFileUri);
            initGame(is, theme, move);
            is.close();

            preview.postInvalidate();
            loadMovePicker();
        } catch (final IOException e) {
            logError(TAG, "loadGame", e);
            showToast(this, R.string.sgf_loading_failure);
        }
    }

    private void loadMovePicker() {
        log(DEBUG, TAG, "loadMovePicker");

        movePicker.getConfigBuilder()
                .min(0)
                .max(preview.getLastMoveNumber())
                .progress(preview.getMoveNumber())
                .build();

        movePicker.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(final BubbleSeekBar bubbleSeekBar, final int progress, final float progressFloat, final boolean fromUser) {
                preview.setMoveNumber(progress);
                preview.postInvalidate();
            }
        });
    }

    private void export() {
        log(DEBUG, TAG, "export");

        try {
            final File file = new File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "katachi-" + currentTimeMillis() + ".png");
            file.createNewFile();

            final FileOutputStream fos = new FileOutputStream(file);
            preview.getBitmap().compress(PNG, 90, fos);
            fos.close();

            showSnackbar(this, R.string.export_success);
        } catch (final IOException e) {
            log(ERROR, TAG, "export FAILURE");
            showSnackbar(this, R.string.export_failure);
        }
    }
}
