package com.judas.katachi.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.judas.katachi.R;
import com.judas.katachi.core.theme.ConfigItem;
import com.judas.katachi.core.theme.ConfigSubItem;
import com.judas.katachi.ui.views.ColorPickerView;
import com.xw.repo.BubbleSeekBar;

import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;

public class ConfigUpdateDialog extends AlertDialog {
    public interface Listener {
        void onConfigValueUpdated(final ConfigSubItem item, final Object value);
    }

    private static final String TAG = ConfigUpdateDialog.class.getSimpleName();
    private Object value;

    private ConfigUpdateDialog(final Builder builder) {
        super(builder.context, R.style.AlertDialog);
        log(DEBUG, TAG, "ConfigUpdateDialog");

        setTitle(builder.item.label(builder.context) + " - " + builder.subitem.label(builder.context));

        final LayoutInflater inflater = LayoutInflater.from(builder.context);
        switch (builder.subitem.type) {
            case COLOR:
                loadForColor(builder.context, (int) builder.initialValue);
                break;
            case DIMENSION:
            case RATIO:
                loadForFloat(inflater, (float) builder.initialValue, builder.subitem.min, builder.subitem.max);
                break;
            case BOOLEAN:
                loadForBoolean(inflater, builder.subitem, (boolean) builder.initialValue);
                break;
            case INTEGER:
                final int initial = (int) builder.initialValue;
                loadForInteger(inflater, initial == -1 ? builder.lastMoveNumber : initial, 0, builder.lastMoveNumber);
                break;
        }

        setButton(BUTTON_POSITIVE, builder.context.getString(android.R.string.ok), (dialog, which) -> {
            if (builder.listener != null) {
                builder.listener.onConfigValueUpdated(builder.subitem, value);
            }
        });

        setButton(BUTTON_NEGATIVE, builder.context.getString(android.R.string.cancel), (dialog, which) -> {
            // Keep empty
        });
    }

    private void loadForColor(final Context context, final int current) {
        log(DEBUG, TAG, "loadForColor");

        value = current;

        final ColorPickerView picker = new ColorPickerView(context);
        picker.setSelectedColor(current);
        picker.setOnColorChangedListener(color -> value = color);
        setView(picker);
    }

    private void loadForFloat(final LayoutInflater inflater, final float current, final float min, final float max) {
        log(DEBUG, TAG, "loadForFloat");

        value = current;

        final View root = inflater.inflate(R.layout.number_picker, null);
        final BubbleSeekBar seekbar = root.findViewById(R.id.number_picker);
        seekbar.getConfigBuilder()
                .min(min)
                .max(max)
                .floatType()
                .progress(current)
                .build();
        seekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(final BubbleSeekBar bubbleSeekBar, final int progress, final float progressFloat, final boolean fromUser) {
                value = progressFloat;
            }
        });
        setView(root);
    }

    private void loadForInteger(final LayoutInflater inflater, final int current, final int min, final int max) {
        log(DEBUG, TAG, "loadForInteger");

        value = current;

        final View root = inflater.inflate(R.layout.number_picker, null);
        final BubbleSeekBar seekbar = root.findViewById(R.id.number_picker);
        seekbar.getConfigBuilder()
                .min(min)
                .max(max)
                .progress(current)
                .build();
        seekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(final BubbleSeekBar bubbleSeekBar, final int progress, final float progressFloat, final boolean fromUser) {
                value = progress;
            }
        });
        setView(root);
    }

    private void loadForBoolean(final LayoutInflater inflater, final ConfigSubItem subitem, final boolean current) {
        log(DEBUG, TAG, "loadForBoolean");

        value = current;

        final View root = inflater.inflate(R.layout.boolean_picker, null);

        ((TextView) root.findViewById(R.id.boolean_picker_text)).setText(subitem.desc);

        final SwitchMaterial picker = root.findViewById(R.id.boolean_picker);
        picker.setOnCheckedChangeListener((buttonView, isChecked) -> value = isChecked);
        picker.setChecked(current);

        setView(root);
    }

    public static class Builder {
        private Context context;
        private ConfigItem item;
        private ConfigSubItem subitem;
        private Object initialValue;
        private int lastMoveNumber;
        private Listener listener;

        public static Builder configUpdateDialog(final Context context) {
            return new Builder(context);
        }

        private Builder(final Context context) {
            this.context = context;
        }

        public Builder withItem(final ConfigItem item) {
            this.item = item;
            return this;
        }

        public Builder withSubItem(final ConfigSubItem subitem) {
            this.subitem = subitem;
            return this;
        }

        public Builder withInitialValue(final Object initialValue) {
            this.initialValue = initialValue;
            return this;
        }

        public Builder withLastMoveNumber(final int lastMoveNumber) {
            this.lastMoveNumber = lastMoveNumber;
            return this;
        }

        public Builder whenConfirmed(final Listener listener) {
            this.listener = listener;
            return this;
        }

        public ConfigUpdateDialog build() {
            return new ConfigUpdateDialog(this);
        }

    }
}
