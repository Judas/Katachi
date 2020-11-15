package com.judas.katachi.core.theme;

import android.content.Context;

import androidx.annotation.StringRes;

import com.judas.katachi.R;

import java.util.ArrayList;
import java.util.List;

public enum ConfigItem {
    BOARD(R.string.config_board),
    LINES(R.string.config_line),
    WHITE(R.string.config_white_stones),
    BLACK(R.string.config_black_stones),
    CURRENT(R.string.config_current_stone);

    @StringRes
    public final int label;

    ConfigItem(final int label) {
        this.label = label;
    }

    public String label(final Context context) {
        return context.getString(label);
    }

    public List<ConfigSubItem> getChildren() {
        final List<ConfigSubItem> subItems = new ArrayList<>();
        for (final ConfigSubItem subItem : ConfigSubItem.values()) {
            if (subItem.parent == this) {
                subItems.add(subItem);
            }
        }
        return subItems;
    }
}
