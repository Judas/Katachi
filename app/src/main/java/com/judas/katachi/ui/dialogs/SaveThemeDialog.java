package com.judas.katachi.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputLayout;
import com.judas.katachi.R;

import static android.text.TextUtils.isEmpty;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;

public class SaveThemeDialog extends AlertDialog {
    public interface Listener {
        void onThemeSaved(final String title);
    }

    private static final String TAG = SaveThemeDialog.class.getSimpleName();

    private SaveThemeDialog(final Builder builder) {
        super(builder.context, R.style.AlertDialog);
        log(DEBUG, TAG, "SaveThemeDialog");


        final LayoutInflater inflater = LayoutInflater.from(builder.context);
        final View root = inflater.inflate(R.layout.theme_save_dialog, null);
        setView(root);
        setTitle(R.string.edit_menu_save);

        setButton(BUTTON_POSITIVE, builder.context.getString(android.R.string.ok), (dialog, which) -> {
            // Keep empty
        });

        setButton(BUTTON_NEGATIVE, builder.context.getString(android.R.string.cancel), (dialog, which) -> {
            // Keep empty
        });

        // Title
        final TextInputLayout title = root.findViewById(R.id.theme_title);
        setOnShowListener(dialog -> {
            final Button button = getButton(BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                final String titleString = title.getEditText().getText().toString();

                if (isEmpty(titleString)) {
                    title.setError(builder.context.getString(R.string.theme_title_required));
                    return;
                }

                if (builder.listener != null) {
                    builder.listener.onThemeSaved(titleString);
                }
                dismiss();
            });
        });
    }

    public static class Builder {
        private Context context;
        private Listener listener;

        public static Builder saveThemDialog(final Context context) {
            return new Builder(context);
        }

        private Builder(final Context context) {
            this.context = context;
        }

        public Builder whenConfirmed(final Listener listener) {
            this.listener = listener;
            return this;
        }

        public SaveThemeDialog build() {
            return new SaveThemeDialog(this);
        }

    }
}
