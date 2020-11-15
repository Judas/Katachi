package com.judas.katachi.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.judas.katachi.R;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static android.content.Intent.CATEGORY_OPENABLE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.judas.katachi.BuildConfig.VERSION_NAME;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.Level.ERROR;
import static com.judas.katachi.utils.log.Logger.log;
import static com.judas.katachi.utils.view.ViewUtils.showSnackbar;

public class SelectionActivity extends AppCompatActivity {
    private static final String TAG = SelectionActivity.class.getSimpleName();

    private MaterialButton action;
    private ProgressBar progress;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log(DEBUG, TAG, "onCreate");

        setContentView(R.layout.selection);

        action = findViewById(R.id.selection_action);
        action.setOnClickListener(v -> selectSgf());

        progress = findViewById(R.id.selection_progress);

        findViewById(R.id.about).setOnClickListener(v -> showAboutDialog());

        setLoading(false);
    }

    private void setLoading(final boolean loading) {
        log(DEBUG, TAG, "setLoading " + loading);

        progress.setVisibility(loading ? VISIBLE : GONE);
        action.setVisibility(loading ? GONE : VISIBLE);
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
                        showSnackbar(this, R.string.selection_failure);
                        return;
                    }

                    EditActivity.start(this, result.getData().getData());
                }
        ).launch(intent);
    }

    private void showAboutDialog() {
        log(DEBUG, TAG, "showAboutDialog");

        final String about = getString(R.string.about_message, VERSION_NAME);
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setIcon(R.mipmap.ic_launcher_round)
                .setMessage(about)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // Keep empty
                })
                .show();
    }
}
