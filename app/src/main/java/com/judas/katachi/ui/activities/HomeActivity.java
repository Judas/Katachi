package com.judas.katachi.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.judas.katachi.R;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static android.content.Intent.CATEGORY_OPENABLE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.Level.ERROR;
import static com.judas.katachi.utils.log.Logger.log;
import static com.judas.katachi.utils.view.ViewUtils.showSnackbar;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();

    private MaterialButton sgfAction;
    private MaterialButton wallpaperAction;
    private MaterialButton themeAction;
    private ProgressBar progress;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log(DEBUG, TAG, "onCreate");

        setContentView(R.layout.home);

        sgfAction = findViewById(R.id.sgf_to_png);
        sgfAction.setOnClickListener(v -> selectSgf());

        wallpaperAction = findViewById(R.id.wallpaper);
        wallpaperAction.setOnClickListener(v -> selectSgfFolder());

        themeAction = findViewById(R.id.edit_themes);
        themeAction.setOnClickListener(v -> openThemeEditing());

        progress = findViewById(R.id.progress);

        findViewById(R.id.about).setOnClickListener(v -> AboutActivity.start(this));

        setLoading(false);
    }

    private void setLoading(final boolean loading) {
        log(DEBUG, TAG, "setLoading " + loading);

        progress.setVisibility(loading ? VISIBLE : INVISIBLE);
        sgfAction.setEnabled(!loading);
        wallpaperAction.setEnabled(!loading);
        themeAction.setEnabled(!loading);
    }

    private void selectSgf() {
        log(DEBUG, TAG, "selectSgf");

        setLoading(true);

        final Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
        intent.addCategory(CATEGORY_OPENABLE);
        intent.setType("application/x-go-sgf");

        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    setLoading(false);

                    if (result.getResultCode() != RESULT_OK) {
                        log(ERROR, TAG, "registerForActivityResult FAILURE");
                        showSnackbar(this, R.string.sgf_selection_failure);
                        return;
                    }
                    SgfToPngActivity.start(this, result.getData().getData());
                }
        ).launch(intent);
    }

    private void selectSgfFolder() {
        log(DEBUG, TAG, "selectSgfFolder");
        WallpaperActivity.start(this);
    }

    private void openThemeEditing() {
        log(DEBUG, TAG, "openThemeEditing");
        EditThemeActivity.start(this);
    }
}
