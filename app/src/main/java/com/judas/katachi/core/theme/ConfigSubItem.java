package com.judas.katachi.core.theme;

import android.content.Context;

import androidx.annotation.StringRes;

import com.judas.katachi.R;

import static com.judas.katachi.core.theme.ConfigItem.BLACK;
import static com.judas.katachi.core.theme.ConfigItem.BOARD;
import static com.judas.katachi.core.theme.ConfigItem.LINES;
import static com.judas.katachi.core.theme.ConfigItem.WHITE;
import static com.judas.katachi.core.theme.ConfigSubItemType.COLOR;
import static com.judas.katachi.core.theme.ConfigSubItemType.DIMENSION;
import static com.judas.katachi.core.theme.ConfigSubItemType.RATIO;

public enum ConfigSubItem {
    BOARD_COLOR(R.string.config_board_color, -1, COLOR, -1f, -1f, BOARD),
    BOARD_PADDING(R.string.config_board_padding, -1, RATIO, 0f, 2f, BOARD),
    LINE_COLOR(R.string.config_line_color, -1, COLOR, -1f, -1f, LINES),
    LINE_WIDTH(R.string.config_line_width, -1, DIMENSION, 0f, 2f, LINES),
    LINE_OVERFLOW(R.string.config_line_overflow, -1, RATIO, 0f, 1f, LINES),
    WHITE_FILL_COLOR(R.string.config_white_stone_color, -1, COLOR, -1f, -1f, WHITE),
    WHITE_STROKE_COLOR(R.string.config_white_stone_stroke_color, -1, COLOR, -1f, -1f, WHITE),
    WHITE_STROKE_WIDTH(R.string.config_white_stone_stroke_width, -1, DIMENSION, 0f, 2f, WHITE),
    WHITE_HIGHLIGHTED_FILL_COLOR(R.string.config_current_white_stone_color, -1, COLOR, -1f, -1f, WHITE),
    WHITE_HIGHLIGHTED_STROKE_COLOR(R.string.config_current_white_stone_color, -1, COLOR, -1f, -1f, WHITE),
    WHITE_HIGHLIGHTED_STROKE_WIDTH(R.string.config_current_white_stone_stroke_width, -1, DIMENSION, 0f, 2f, WHITE),
    BLACK_FILL_COLOR(R.string.config_black_stone_color, -1, COLOR, -1f, -1f, BLACK),
    BLACK_STROKE_COLOR(R.string.config_black_stone_stroke_color, -1, COLOR, -1f, -1f, BLACK),
    BLACK_STROKE_WIDTH(R.string.config_black_stone_stroke_width, -1, DIMENSION, 0f, 2f, BLACK),
    BLACK_HIGHLIGHTED_FILL_COLOR(R.string.config_current_black_stone_color, -1, COLOR, -1f, -1f, BLACK),
    BLACK_HIGHLIGHTED_STROKE_COLOR(R.string.config_current_black_stone_stroke_color, -1, COLOR, -1f, -1f, BLACK),
    BLACK_HIGHLIGHTED_STROKE_WIDTH(R.string.config_current_black_stone_stroke_width, -1, DIMENSION, 0f, 2f, BLACK);

    @StringRes
    public final int label;
    @StringRes
    public final int desc;
    public final ConfigSubItemType type;
    public final float min;
    public final float max;
    public final ConfigItem parent;

    ConfigSubItem(final int label, final int desc, final ConfigSubItemType type, final float min, final float max, final ConfigItem parent) {
        this.label = label;
        this.desc = desc;
        this.type = type;
        this.min = min;
        this.max = max;
        this.parent = parent;
    }

    public String label(final Context context) {
        return context.getString(label);
    }
}
