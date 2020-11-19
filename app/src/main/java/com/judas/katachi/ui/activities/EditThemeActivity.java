package com.judas.katachi.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.judas.katachi.R;
import com.judas.katachi.core.theme.ConfigItem;
import com.judas.katachi.core.theme.ConfigSubItem;
import com.judas.katachi.core.theme.KatachiTheme;

import static com.judas.katachi.core.prefs.PreferenceHelper.prefs;
import static com.judas.katachi.ui.dialogs.ConfigUpdateDialog.Builder.configUpdateDialog;
import static com.judas.katachi.ui.dialogs.SaveThemeDialog.Builder.saveThemeDialog;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;

public class EditThemeActivity extends BoardActivity {
    private static final String TAG = EditThemeActivity.class.getSimpleName();
    private static final String EXTRA_THEME = TAG + ".EXTRA_THEME";

    private ChipGroup mainChipGroup;
    private ChipGroup secondaryChipGroup;
    private View.OnClickListener chipClickListener;

    public static void start(final Context context) {
        log(DEBUG, TAG, "start");
        context.startActivity(new Intent(context, EditThemeActivity.class));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log(DEBUG, TAG, "onCreate");

        setContentView(R.layout.theme_edit);

        // Toolbar
        final MaterialToolbar toolbar = findViewById(R.id.theme_edit_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            final int itemId = item.getItemId();
            if (itemId == R.id.theme_edit_save) {
                showSaveDialog();
                return true;
            } else if (itemId == R.id.theme_edit_presets) {
                showThemeDialog(theme -> {
                    preview.setTheme(theme);
                    preview.postInvalidate();
                });
                return true;
            }
            return false;
        });

        preview = findViewById(R.id.theme_edit_preview);
        mainChipGroup = findViewById(R.id.theme_edit_main_chip_group);
        secondaryChipGroup = findViewById(R.id.theme_edit_secondary_chip_group);

        final LayoutInflater inflater = LayoutInflater.from(this);
        chipClickListener = v -> {
            final Object tag = v.getTag();
            if (tag instanceof ConfigItem) {
                onConfigItemSelected(inflater, (Chip) v, (ConfigItem) v.getTag());
            } else if (tag instanceof ConfigSubItem) {
                onConfigSubItemSelected((ConfigSubItem) v.getTag());
            }
        };
        loadConfigItems(inflater);

        final KatachiTheme theme = savedInstanceState != null ? savedInstanceState.getParcelable(EXTRA_THEME) : null;
        loadDefaultGame(theme);
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
                .whenConfirmed((it, value) -> {
                    log(DEBUG, TAG, "onConfigValueUpdated " + it.name() + " : " + value);

                    final KatachiTheme theme = preview.getTheme();
                    theme.setValueFor(this, it, value);
                    preview.setTheme(theme);
                    preview.postInvalidate();
                })
                .build()
                .show();
    }

    private void showSaveDialog() {
        log(DEBUG, TAG, "showSaveDialog");

        saveThemeDialog(this)
                .whenConfirmed(title -> {
                    final KatachiTheme theme = preview.getTheme();
                    theme.name = title;
                    prefs(this).saveUserTheme(theme);
                })
                .build()
                .show();
    }
}
