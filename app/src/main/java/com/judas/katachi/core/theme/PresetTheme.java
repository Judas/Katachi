package com.judas.katachi.core.theme;

import androidx.annotation.ColorInt;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.parseColor;

public enum PresetTheme {
    CLASSIC(parseColor("#CD8500"), 0.66f,
            BLACK, 1.2f, 1f,
            BLACK, BLACK, 2f,
            WHITE, BLACK, 2f,
            BLACK, RED, 2f,
            WHITE, RED, 2f),

    BOOK(WHITE, 0.66f,
            BLACK, 1f, 1f,
            BLACK, BLACK, 1.2f,
            WHITE, BLACK, 1.2f,
            BLACK, RED, 2f,
            WHITE, RED, 2f),

    SEPIA(parseColor("#B0A091"), 0.66f,
            parseColor("#4D443B"), 1f, 1f,
            parseColor("#4D443B"), parseColor("#4D443B"), 1.2f,
            parseColor("#F6E6D7"), parseColor("#4D443B"), 1.2f,
            parseColor("#4D443B"), RED, 2f,
            parseColor("#F6E6D7"), BLUE, 2f),

    FIRE(parseColor("#FFE7A2"), 0.66f,
            parseColor("#37191B"), 1f, 1f,
            parseColor("#A12826"), parseColor("#A12826"), 1.2f,
            parseColor("#FD9B2A"), parseColor("#FD9B2A"), 1.2f,
            parseColor("#A12826"), BLACK, 2f,
            parseColor("#FD9B2A"), parseColor("#A12826"), 2f),

    LEAF(parseColor("#DAD7CD"), 0.66f,
            parseColor("#344E41"), 1f, 1f,
            parseColor("#3A5A40"), parseColor("#3A5A40"), 1.2f,
            parseColor("#A3B18A"), parseColor("#344E41"), 1.2f,
            parseColor("#588157"), parseColor("#3A5A40"), 2f,
            parseColor("#DAD7CD"), parseColor("#3A5A40"), 2f),

    WATER(parseColor("#142C59"), 0.66f,
            parseColor("#0F60E7"), 1f, 1f,
            parseColor("#6FD2EA"), parseColor("#6FD2EA"), 1.2f,
            parseColor("#F9F9F8"), parseColor("#F9F9F8"), 1.2f,
            parseColor("#6FD2EA"), parseColor("#FEA027"), 2f,
            parseColor("#F9F9F8"), parseColor("#FEA027"), 2f),

    KATACHI(parseColor("#14213D"), 0.66f,
            parseColor("#FCA311"), 1f, 0.5f,
            parseColor("#14213D"), parseColor("#FCA311"), 1.2f,
            parseColor("#FCA311"), parseColor("#FCA311"), 1.2f,
            parseColor("#3E4868"), parseColor("#FCA311"), 2f,
            parseColor("#FFD44F"), parseColor("#FCA311"), 2f);

    @ColorInt
    public final int backgroundColor;
    public final float paddingRatio;

    @ColorInt
    public final int lineColor;
    public final float lineWidth;
    public final float lineOverflowRatio;

    @ColorInt
    public final int blackStoneColor;
    @ColorInt
    public final int blackStoneStrokeColor;
    public final float blackStoneStrokeWidth;

    @ColorInt
    public final int whiteStoneColor;
    @ColorInt
    public final int whiteStoneStrokeColor;
    public final float whiteStoneStrokeWidth;

    @ColorInt
    public final int currentBlackStoneColor;
    @ColorInt
    public final int currentBlackStoneStrokeColor;
    public final float currentBlackStoneStrokeWidth;

    @ColorInt
    public final int currentWhiteStoneColor;
    @ColorInt
    public final int currentWhiteStoneStrokeColor;
    public final float currentWhiteStoneStrokeWidth;

    PresetTheme(@ColorInt final int backgroundColor, final float paddingRatio, @ColorInt final int lineColor, final float lineWidth, final float lineOverflowRatio,
                @ColorInt final int blackStoneColor, @ColorInt final int blackStoneStrokeColor, final float blackStoneStrokeWidth,
                @ColorInt final int whiteStoneColor, @ColorInt final int whiteStoneStrokeColor, final float whiteStoneStrokeWidth,
                @ColorInt final int currentBlackStoneColor, @ColorInt final int currentBlackStoneStrokeColor, final float currentBlackStoneStrokeWidth,
                @ColorInt final int currentWhiteStoneColor, @ColorInt final int currentWhiteStoneStrokeColor, final float currentWhiteStoneStrokeWidth) {
        this.backgroundColor = backgroundColor;
        this.paddingRatio = paddingRatio;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
        this.lineOverflowRatio = lineOverflowRatio;
        this.whiteStoneColor = whiteStoneColor;
        this.whiteStoneStrokeColor = whiteStoneStrokeColor;
        this.whiteStoneStrokeWidth = whiteStoneStrokeWidth;
        this.blackStoneColor = blackStoneColor;
        this.blackStoneStrokeColor = blackStoneStrokeColor;
        this.blackStoneStrokeWidth = blackStoneStrokeWidth;
        this.currentBlackStoneColor = currentBlackStoneColor;
        this.currentBlackStoneStrokeColor = currentBlackStoneStrokeColor;
        this.currentBlackStoneStrokeWidth = currentBlackStoneStrokeWidth;
        this.currentWhiteStoneColor = currentWhiteStoneColor;
        this.currentWhiteStoneStrokeColor = currentWhiteStoneStrokeColor;
        this.currentWhiteStoneStrokeWidth = currentWhiteStoneStrokeWidth;
    }

    public String label() {
        return name().substring(0, 1) + name().substring(1).toLowerCase();
    }
}
