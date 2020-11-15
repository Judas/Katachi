package com.judas.katachi.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.judas.katachi.R;
import com.judas.katachi.core.theme.ConfigItem;
import com.judas.katachi.core.theme.ConfigSubItem;
import com.judas.katachi.core.theme.KatachiTheme;
import com.judas.katachi.core.theme.PresetTheme;
import com.judas.katachi.ui.views.VectorView;
import com.toomasr.sgf4j.Sgf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Bitmap.CompressFormat.PNG;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.judas.katachi.core.prefs.PreferenceHelper.prefs;
import static com.judas.katachi.core.theme.PresetTheme.CLASSIC;
import static com.judas.katachi.ui.dialogs.ConfigUpdateDialog.Builder.configUpdateDialog;
import static com.judas.katachi.ui.dialogs.SaveThemeDialog.Builder.saveThemDialog;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.Level.ERROR;
import static com.judas.katachi.utils.log.Logger.log;
import static com.judas.katachi.utils.log.Logger.logError;
import static com.judas.katachi.utils.view.ViewUtils.showSnackbar;
import static java.lang.System.currentTimeMillis;

public class EditActivity extends AppCompatActivity implements VectorView.OnDrawnListener {
    private static final String TAG = EditActivity.class.getSimpleName();
    private static final String EXTRA_FILE_URI = TAG + ".EXTRA_FILE_URI";
    private static final String EXTRA_THEME = TAG + ".EXTRA_THEME";

    private VectorView preview;
    private ChipGroup mainChipGroup;
    private ChipGroup secondaryChipGroup;
    private View.OnClickListener chipClickListener;
    private ProgressBar progress;

    public static void start(final Context context, final Uri sgfFileUri) {
        log(DEBUG, TAG, "start");

        final Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(EXTRA_FILE_URI, sgfFileUri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log(DEBUG, TAG, "onCreate");

        setContentView(R.layout.edit);

        // Toolbar
        final MaterialToolbar toolbar = findViewById(R.id.edit_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit_menu_save:
                    showSaveDialog();
                    return true;
                case R.id.edit_menu_themes:
                    showThemeDialog();
                    return true;
                case R.id.edit_menu_export:
                    export();
                    return true;
                default:
                    return false;
            }
        });

        preview = findViewById(R.id.edit_preview_layout);
        mainChipGroup = findViewById(R.id.edit_main_chip_group);
        secondaryChipGroup = findViewById(R.id.edit_secondary_chip_group);
        progress = findViewById(R.id.edit_progress);

        final LayoutInflater inflater = LayoutInflater.from(this);
        chipClickListener = v -> {
            final Object tag = v.getTag();
            if (tag instanceof ConfigItem) {
                onConfigItemSelected(inflater, (Chip) v, (ConfigItem) v.getTag());
            } else if (tag instanceof ConfigSubItem) {
                onConfigSubItemSelected((ConfigSubItem) v.getTag());
            }
        };

        preview.setOnDrawnListener(this);
        loadConfigItems(inflater);

        final KatachiTheme theme = savedInstanceState != null ? savedInstanceState.getParcelable(EXTRA_THEME) : null;
        loadGame(getIntent().getParcelableExtra(EXTRA_FILE_URI), theme);
    }

    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        log(DEBUG, TAG, "onSaveInstanceState");

        outState.putParcelable(EXTRA_THEME, preview.getTheme());
    }

    private void setLoading(final boolean loading) {
        log(DEBUG, TAG, "setLoading " + loading);
        progress.setVisibility(loading ? VISIBLE : GONE);
    }

    private void loadGame(final Uri sgfFileUri, final KatachiTheme theme) {
        log(DEBUG, TAG, "loadGame");

        setLoading(true);

        try {
            final InputStream is = getContentResolver().openInputStream(sgfFileUri);
            preview.setGame(Sgf.createFromInputStream(is));
            preview.setTheme(theme == null ? new KatachiTheme(this, CLASSIC) : theme);
            is.close();
            refreshPreview();
        } catch (final IOException e) {
            onError(e);
        }
    }

    private void loadConfigItems(final LayoutInflater inflater) {
        log(DEBUG, TAG, "loadConfigItems");

        mainChipGroup.removeAllViews();
        secondaryChipGroup.removeAllViews();

        for (final ConfigItem item : ConfigItem.values()) {
            addChip(inflater, mainChipGroup, item.label, item, true);
        }
    }

    private void addChip(final LayoutInflater inflater, final ChipGroup group, final int labelResId, final Object tag, final boolean checkable) {
        final Chip chip = (Chip) inflater.inflate(R.layout.edit_chip, group, false);
        chip.setText(labelResId);
        chip.setTag(tag);
        chip.setOnClickListener(chipClickListener);
        chip.setCheckable(checkable);
        group.addView(chip);
    }

    private void onConfigItemSelected(final LayoutInflater inflater, final Chip chip, final ConfigItem item) {
        log(DEBUG, TAG, "onConfigItemSelected " + item.name());

        for (int i = 0; i < mainChipGroup.getChildCount(); i++) {
            ((Chip) mainChipGroup.getChildAt(i)).setChecked(false);
        }
        chip.setChecked(true);

        secondaryChipGroup.removeAllViews();
        for (final ConfigSubItem subitem : item.getChildren()) {
            addChip(inflater, secondaryChipGroup, subitem.label, subitem, false);
        }
    }

    private void onConfigSubItemSelected(final ConfigSubItem subitem) {
        log(DEBUG, TAG, "onConfigSubItemSelected " + subitem.name());

        configUpdateDialog(this)
                .withItem(subitem.parent)
                .withSubItem(subitem)
                .withInitialValue(preview.getTheme().getValueFor(subitem))
                .withLastMoveNumber(preview.getLastMoveNumber())
                .whenConfirmed((it, value) -> {
                    log(DEBUG, TAG, "onConfigValueUpdated " + it.name() + " : " + value);

                    final KatachiTheme theme = preview.getTheme();
                    theme.setValueFor(this, it, value);
                    preview.setTheme(theme);

                    refreshPreview();
                })
                .build()
                .show();
    }

    private void refreshPreview() {
        log(DEBUG, TAG, "refreshPreview");

        setLoading(true);
        preview.postInvalidate();
    }

    @Override
    public void onDrawn() {
        log(DEBUG, TAG, "onDrawn");
        setLoading(false);
    }

    @Override
    public void onError(final Exception e) {
        logError(TAG, "onError", e);
        showSnackbar(this, R.string.loading_failure);
        setLoading(false);
    }

    private void showThemeDialog() {
        log(DEBUG, TAG, "showThemeDialog");

        final Map<String, KatachiTheme> themes = new HashMap<>();
        for (final PresetTheme preset : PresetTheme.values()) {
            final String themeName = preset.name().substring(0, 1) + preset.name().substring(1).toLowerCase();
            themes.put(themeName, new KatachiTheme(this, preset));
        }
        for (final Map.Entry<String, KatachiTheme> userTheme : prefs(this).getUserThemes().entrySet()) {
            final KatachiTheme theme = userTheme.getValue();
            theme.init(this);
            themes.put(userTheme.getKey(), theme);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, themes.keySet().toArray(new String[0]));

        new AlertDialog.Builder(this)
                .setTitle(R.string.edit_menu_themes)
                .setAdapter(adapter, (dialog, which) -> {
                    final KatachiTheme theme = themes.get(adapter.getItem(which));
                    if (theme == null) {
                        return;
                    }
                    theme.currentMoveNumber = preview.getTheme().currentMoveNumber;
                    preview.setTheme(theme);

                    refreshPreview();
                    dialog.dismiss();
                })
                .show();
    }

    private void showSaveDialog() {
        log(DEBUG, TAG, "showSaveDialog");

        saveThemDialog(this)
                .whenConfirmed(title -> prefs(this).saveUserTheme(title, preview.getTheme()))
                .build()
                .show();
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
