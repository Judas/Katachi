package com.judas.katachi.core.theme;

import android.content.Context;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorInt;

import com.toomasr.sgf4j.board.StoneState;

import java.io.Serializable;
import java.util.Objects;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Join.MITER;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;
import static com.judas.katachi.utils.view.ViewUtils.dpToPx;

public class KatachiTheme implements Parcelable {
    private static final String TAG = KatachiTheme.class.getSimpleName();

    @ColorInt
    public int backgroundColor;
    public float paddingRatio;
    public Paint backgroundPaint;

    @ColorInt
    public int lineColor;
    public float lineWidth;
    public float lineOverflowRatio;
    public Paint linePaint;

    @ColorInt
    public int whiteStoneColor;
    @ColorInt
    public int whiteStoneStrokeColor;
    public float whiteStoneStrokeWidth;
    public Paint whiteStonePaint;
    public Paint whiteStoneStrokePaint;

    @ColorInt
    public int blackStoneColor;
    @ColorInt
    public int blackStoneStrokeColor;
    public float blackStoneStrokeWidth;
    public Paint blackStonePaint;
    public Paint blackStoneStrokePaint;

    @ColorInt
    public int currentBlackStoneColor;
    @ColorInt
    public int currentBlackStoneStrokeColor;
    public float currentBlackStoneStrokeWidth;
    public Paint currentBlackStonePaint;
    public Paint currentBlackStoneStrokePaint;

    @ColorInt
    public int currentWhiteStoneColor;
    @ColorInt
    public int currentWhiteStoneStrokeColor;
    public float currentWhiteStoneStrokeWidth;
    public Paint currentWhiteStonePaint;
    public Paint currentWhiteStoneStrokePaint;

    public boolean highlightCurrentStone;
    public int currentMoveNumber;

    public KatachiTheme(final Context context, final PresetTheme preset) {
        log(DEBUG, TAG, "KatachiTheme");

        this.backgroundColor = preset.backgroundColor;
        this.paddingRatio = preset.paddingRatio;
        this.lineColor = preset.lineColor;
        this.lineWidth = preset.lineWidth;
        this.lineOverflowRatio = preset.lineOverflowRatio;
        this.whiteStoneColor = preset.whiteStoneColor;
        this.whiteStoneStrokeColor = preset.whiteStoneStrokeColor;
        this.whiteStoneStrokeWidth = preset.whiteStoneStrokeWidth;
        this.blackStoneColor = preset.blackStoneColor;
        this.blackStoneStrokeColor = preset.blackStoneStrokeColor;
        this.blackStoneStrokeWidth = preset.blackStoneStrokeWidth;
        this.currentBlackStoneColor = preset.currentBlackStoneColor;
        this.currentBlackStoneStrokeColor = preset.currentBlackStoneStrokeColor;
        this.currentBlackStoneStrokeWidth = preset.currentBlackStoneStrokeWidth;
        this.currentWhiteStoneColor = preset.currentWhiteStoneColor;
        this.currentWhiteStoneStrokeColor = preset.currentWhiteStoneStrokeColor;
        this.currentWhiteStoneStrokeWidth = preset.currentWhiteStoneStrokeWidth;
        this.highlightCurrentStone = preset.highlightCurrentStone;
        this.currentMoveNumber = preset.currentMoveNumber;

        init(context);
    }

    public void init(final Context context) {
        log(DEBUG, TAG, "init");

        backgroundPaint = new Paint(ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(FILL);
        backgroundPaint.setColor(backgroundColor);

        linePaint = new Paint(ANTI_ALIAS_FLAG);
        linePaint.setStyle(STROKE);
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(dpToPx(context, lineWidth));
        linePaint.setStrokeJoin(MITER);

        whiteStonePaint = new Paint(ANTI_ALIAS_FLAG);
        whiteStonePaint.setStyle(FILL);
        whiteStonePaint.setColor(whiteStoneColor);
        whiteStoneStrokePaint = new Paint(ANTI_ALIAS_FLAG);
        whiteStoneStrokePaint.setStyle(STROKE);
        whiteStoneStrokePaint.setColor(whiteStoneStrokeColor);
        whiteStoneStrokePaint.setStrokeWidth(dpToPx(context, whiteStoneStrokeWidth));
        whiteStoneStrokePaint.setStrokeJoin(MITER);

        blackStonePaint = new Paint(ANTI_ALIAS_FLAG);
        blackStonePaint.setStyle(FILL);
        blackStonePaint.setColor(blackStoneColor);
        blackStoneStrokePaint = new Paint(ANTI_ALIAS_FLAG);
        blackStoneStrokePaint.setStyle(STROKE);
        blackStoneStrokePaint.setColor(blackStoneStrokeColor);
        blackStoneStrokePaint.setStrokeWidth(dpToPx(context, blackStoneStrokeWidth));
        blackStoneStrokePaint.setStrokeJoin(MITER);

        currentBlackStonePaint = new Paint(ANTI_ALIAS_FLAG);
        currentBlackStonePaint.setStyle(FILL);
        currentBlackStonePaint.setColor(currentBlackStoneColor);
        currentBlackStoneStrokePaint = new Paint(ANTI_ALIAS_FLAG);
        currentBlackStoneStrokePaint.setStyle(STROKE);
        currentBlackStoneStrokePaint.setColor(currentBlackStoneStrokeColor);
        currentBlackStoneStrokePaint.setStrokeWidth(dpToPx(context, currentBlackStoneStrokeWidth));
        currentBlackStoneStrokePaint.setStrokeJoin(MITER);

        currentWhiteStonePaint = new Paint(ANTI_ALIAS_FLAG);
        currentWhiteStonePaint.setStyle(FILL);
        currentWhiteStonePaint.setColor(currentWhiteStoneColor);
        currentWhiteStoneStrokePaint = new Paint(ANTI_ALIAS_FLAG);
        currentWhiteStoneStrokePaint.setStyle(STROKE);
        currentWhiteStoneStrokePaint.setColor(currentWhiteStoneStrokeColor);
        currentWhiteStoneStrokePaint.setStrokeWidth(dpToPx(context, currentWhiteStoneStrokeWidth));
        currentWhiteStoneStrokePaint.setStrokeJoin(MITER);
    }

    public Paint getStonePaint(final StoneState state, final boolean current) {
        switch (state) {
            case BLACK:
                if (current && highlightCurrentStone) {
                    return currentBlackStonePaint;
                } else {
                    return blackStonePaint;
                }
            case WHITE:
                if (current && highlightCurrentStone) {
                    return currentWhiteStonePaint;
                } else {
                    return whiteStonePaint;
                }
            case EMPTY:
            default:
                return null;
        }
    }

    public Paint getStoneStrokePaint(final StoneState state, final boolean current) {
        switch (state) {
            case BLACK:
                if (current && highlightCurrentStone) {
                    return currentBlackStoneStrokePaint;
                } else {
                    return blackStoneStrokePaint;
                }
            case WHITE:
                if (current && highlightCurrentStone) {
                    return currentWhiteStoneStrokePaint;
                } else {
                    return whiteStoneStrokePaint;
                }
            case EMPTY:
            default:
                return null;
        }
    }

    public Object getValueFor(final ConfigSubItem subItem) {
        log(DEBUG, TAG, "getValueFor " + subItem.name());

        switch (subItem) {
            case BOARD_COLOR:
                return backgroundColor;
            case BOARD_PADDING:
                return paddingRatio;
            case LINE_COLOR:
                return lineColor;
            case LINE_WIDTH:
                return lineWidth;
            case LINE_OVERFLOW:
                return lineOverflowRatio;
            case WHITE_FILL_COLOR:
                return whiteStoneColor;
            case WHITE_STROKE_COLOR:
                return whiteStoneStrokeColor;
            case WHITE_STROKE_WIDTH:
                return whiteStoneStrokeWidth;
            case WHITE_HIGHLIGHTED_FILL_COLOR:
                return currentWhiteStoneColor;
            case WHITE_HIGHLIGHTED_STROKE_COLOR:
                return currentWhiteStoneStrokeColor;
            case WHITE_HIGHLIGHTED_STROKE_WIDTH:
                return currentWhiteStoneStrokeWidth;
            case BLACK_FILL_COLOR:
                return blackStoneColor;
            case BLACK_STROKE_COLOR:
                return blackStoneStrokeColor;
            case BLACK_STROKE_WIDTH:
                return blackStoneStrokeWidth;
            case BLACK_HIGHLIGHTED_FILL_COLOR:
                return currentBlackStoneColor;
            case BLACK_HIGHLIGHTED_STROKE_COLOR:
                return currentBlackStoneStrokeColor;
            case BLACK_HIGHLIGHTED_STROKE_WIDTH:
                return currentBlackStoneStrokeWidth;
            case CURRENT_MOVE_NUMBER:
                return currentMoveNumber;
            case CURRENT_MOVE_HIGHLIGHT:
                return highlightCurrentStone;
        }
        return null;
    }

    public void setValueFor(final Context context, final ConfigSubItem subItem, final Object value) {
        log(DEBUG, TAG, "setValueFor " + subItem.name() + " " + value);

        switch (subItem) {
            case BOARD_COLOR:
                backgroundColor = (int) value;
                break;
            case BOARD_PADDING:
                paddingRatio = (float) value;
                break;
            case LINE_COLOR:
                lineColor = (int) value;
                break;
            case LINE_WIDTH:
                lineWidth = (float) value;
                break;
            case LINE_OVERFLOW:
                lineOverflowRatio = (float) value;
                break;
            case WHITE_FILL_COLOR:
                whiteStoneColor = (int) value;
                break;
            case WHITE_STROKE_COLOR:
                whiteStoneStrokeColor = (int) value;
                break;
            case WHITE_STROKE_WIDTH:
                whiteStoneStrokeWidth = (float) value;
                break;
            case WHITE_HIGHLIGHTED_FILL_COLOR:
                currentWhiteStoneColor = (int) value;
                break;
            case WHITE_HIGHLIGHTED_STROKE_COLOR:
                currentWhiteStoneStrokeColor = (int) value;
                break;
            case WHITE_HIGHLIGHTED_STROKE_WIDTH:
                currentWhiteStoneStrokeWidth = (float) value;
                break;
            case BLACK_FILL_COLOR:
                blackStoneColor = (int) value;
                break;
            case BLACK_STROKE_COLOR:
                blackStoneStrokeColor = (int) value;
                break;
            case BLACK_STROKE_WIDTH:
                blackStoneStrokeWidth = (float) value;
                break;
            case BLACK_HIGHLIGHTED_FILL_COLOR:
                currentBlackStoneColor = (int) value;
                break;
            case BLACK_HIGHLIGHTED_STROKE_COLOR:
                currentBlackStoneStrokeColor = (int) value;
                break;
            case BLACK_HIGHLIGHTED_STROKE_WIDTH:
                currentBlackStoneStrokeWidth = (float) value;
                break;
            case CURRENT_MOVE_NUMBER:
                currentMoveNumber = (int) value;
                break;
            case CURRENT_MOVE_HIGHLIGHT:
                highlightCurrentStone = (boolean) value;
                break;
        }
        init(context);
    }

    // =============================================================================================

    public static final Creator<KatachiTheme> CREATOR = new Creator<KatachiTheme>() {
        public KatachiTheme createFromParcel(final Parcel in) {
            return new KatachiTheme(in);
        }

        public KatachiTheme[] newArray(final int size) {
            return (new KatachiTheme[size]);
        }
    };

    protected KatachiTheme(final Parcel in) {
        backgroundColor = in.readInt();
        paddingRatio = in.readFloat();
        lineColor = in.readInt();
        lineWidth = in.readFloat();
        lineOverflowRatio = in.readFloat();
        whiteStoneColor = in.readInt();
        whiteStoneStrokeColor = in.readInt();
        whiteStoneStrokeWidth = in.readFloat();
        blackStoneColor = in.readInt();
        blackStoneStrokeColor = in.readInt();
        blackStoneStrokeWidth = in.readFloat();
        currentBlackStoneColor = in.readInt();
        currentBlackStoneStrokeColor = in.readInt();
        currentBlackStoneStrokeWidth = in.readFloat();
        currentWhiteStoneColor = in.readInt();
        currentWhiteStoneStrokeColor = in.readInt();
        currentWhiteStoneStrokeWidth = in.readFloat();
        highlightCurrentStone = in.readInt() == 1;
        currentMoveNumber = in.readInt();
    }

    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(backgroundColor);
        dest.writeFloat(paddingRatio);
        dest.writeInt(lineColor);
        dest.writeFloat(lineWidth);
        dest.writeFloat(lineOverflowRatio);
        dest.writeInt(whiteStoneColor);
        dest.writeInt(whiteStoneStrokeColor);
        dest.writeFloat(whiteStoneStrokeWidth);
        dest.writeInt(blackStoneColor);
        dest.writeInt(blackStoneStrokeColor);
        dest.writeFloat(blackStoneStrokeWidth);
        dest.writeInt(currentBlackStoneColor);
        dest.writeInt(currentBlackStoneStrokeColor);
        dest.writeFloat(currentBlackStoneStrokeWidth);
        dest.writeInt(currentWhiteStoneColor);
        dest.writeInt(currentWhiteStoneStrokeColor);
        dest.writeFloat(currentWhiteStoneStrokeWidth);
        dest.writeInt(highlightCurrentStone ? 1 : 0);
        dest.writeInt(currentMoveNumber);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(backgroundColor, paddingRatio, lineColor, lineWidth, lineOverflowRatio, whiteStoneColor, whiteStoneStrokeColor, whiteStoneStrokeWidth, blackStoneColor, blackStoneStrokeColor, blackStoneStrokeWidth, currentBlackStoneColor, currentBlackStoneStrokeColor, currentBlackStoneStrokeWidth, currentWhiteStoneColor, currentWhiteStoneStrokeColor, currentWhiteStoneStrokeWidth, highlightCurrentStone, currentMoveNumber);
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof KatachiTheme)) {
            return false;
        }

        final KatachiTheme that = ((KatachiTheme) other);
        return backgroundColor == that.backgroundColor
                && paddingRatio == that.paddingRatio
                && lineColor == that.lineColor
                && lineWidth == that.lineWidth
                && lineOverflowRatio == that.lineOverflowRatio
                && whiteStoneColor == that.whiteStoneColor
                && whiteStoneStrokeColor == that.whiteStoneStrokeColor
                && whiteStoneStrokeWidth == that.whiteStoneStrokeWidth
                && blackStoneColor == that.blackStoneColor
                && blackStoneStrokeColor == that.blackStoneStrokeColor
                && blackStoneStrokeWidth == that.blackStoneStrokeWidth
                && currentBlackStoneColor == that.currentBlackStoneColor
                && currentBlackStoneStrokeColor == that.currentBlackStoneStrokeColor
                && currentBlackStoneStrokeWidth == that.currentBlackStoneStrokeWidth
                && currentWhiteStoneColor == that.currentWhiteStoneColor
                && currentWhiteStoneStrokeColor == that.currentWhiteStoneStrokeColor
                && currentWhiteStoneStrokeWidth == that.currentWhiteStoneStrokeWidth
                && highlightCurrentStone == that.highlightCurrentStone
                && currentMoveNumber == that.currentMoveNumber;
    }
}
