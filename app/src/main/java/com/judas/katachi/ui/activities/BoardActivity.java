package com.judas.katachi.ui.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.judas.katachi.R;
import com.judas.katachi.core.theme.KatachiTheme;
import com.judas.katachi.core.theme.PresetTheme;
import com.judas.katachi.ui.views.BoardView;
import com.toomasr.sgf4j.Sgf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.judas.katachi.core.prefs.PreferenceHelper.prefs;
import static com.judas.katachi.core.theme.PresetTheme.CLASSIC;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;
import static com.judas.katachi.utils.log.Logger.logError;
import static com.judas.katachi.utils.view.ViewUtils.showToast;

public class BoardActivity extends AppCompatActivity {
    public interface OnThemeSelectedListener {
        void onThemeSelected(KatachiTheme theme);
    }

    private static final String TAG = BoardActivity.class.getSimpleName();
    protected static final String EXTRA_THEME = TAG + ".EXTRA_THEME";
    protected static final String EXTRA_MOVE = TAG + ".EXTRA_MOVE";

    protected BoardView preview;

    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        log(DEBUG, TAG, "onSaveInstanceState");

        outState.putParcelable(EXTRA_THEME, preview.getTheme());
        outState.putInt(EXTRA_MOVE, preview.getMoveNumber());
    }

    protected void showThemeDialog(final OnThemeSelectedListener listener) {
        log(DEBUG, TAG, "showThemeDialog");

        final List<KatachiTheme> themes = new ArrayList<>();
        final List<String> themeNames = new ArrayList<>();

        for (final PresetTheme preset : PresetTheme.values()) {
            final KatachiTheme theme = new KatachiTheme(this, preset);
            themes.add(theme);
            themeNames.add(theme.name);
        }
        for (final KatachiTheme userTheme : prefs(this).getUserThemes()) {
            final KatachiTheme theme = new KatachiTheme(this, userTheme);
            themes.add(theme);
            themeNames.add(theme.name);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, themeNames);

        new AlertDialog.Builder(this)
                .setTitle(R.string.menu_themes)
                .setAdapter(adapter, (dialog, which) -> {
                    final int index = themeNames.indexOf(adapter.getItem(which));
                    final KatachiTheme theme = themes.get(index);

                    if (listener != null) {
                        listener.onThemeSelected(theme);
                    }
                    dialog.dismiss();
                })
                .show();
    }

    protected void loadDefaultGame(final KatachiTheme theme) {
        log(DEBUG, TAG, "loadDefaultGame");

        try {
            final InputStream is = getAssets().open("ear-reddening-game.sgf");
            initGame(is, theme, 127);
            is.close();
        } catch (final IOException e) {
            logError(TAG, "loadFromAssets", e);
            showToast(this, R.string.sgf_loading_failure);
        }
    }

    private void loadFromAssets(final String filename, final KatachiTheme theme) {
        log(DEBUG, TAG, "loadFromAssets " + filename);


    }

    protected void initGame(final InputStream stream, final KatachiTheme theme, final int move) {
        log(DEBUG, TAG, "initGame");

        preview.setGame(Sgf.createFromInputStream(stream));
        preview.setMoveNumber(move);
        preview.setTheme(theme == null ? new KatachiTheme(this, CLASSIC) : theme);
        preview.postInvalidate();
    }
}
