package com.judas.katachi.ui.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.judas.katachi.R;
import com.xw.repo.BubbleSeekBar;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.alpha;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;
import static com.judas.katachi.utils.view.TileVectorDrawable.tilePattern;

public class ColorPickerView extends LinearLayout {
    public interface OnColorChangedListener {
        void onColorChanged(@ColorInt int color);
    }

    private static final String TAG = ColorPickerView.class.getSimpleName();

    private final BubbleSeekBar alpha;
    private final BubbleSeekBar red;
    private final BubbleSeekBar green;
    private final BubbleSeekBar blue;
    private final View preview;
    private final ImageView previewBackground;

    private OnColorChangedListener listener;

    @ColorInt
    private int selected = BLACK;

    public ColorPickerView(final Context context) {
        this(context, null, 0);
    }

    public ColorPickerView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        log(DEBUG, TAG, "ColorPickerView");

        LayoutInflater.from(context).inflate(R.layout.color_picker_view, this);
        alpha = findViewById(R.id.color_channel_alpha);
        red = findViewById(R.id.color_channel_red);
        green = findViewById(R.id.color_channel_green);
        blue = findViewById(R.id.color_channel_blue);
        preview = findViewById(R.id.color_preview);
        previewBackground = findViewById(R.id.color_preview_bg);

        final Drawable pattern = ContextCompat.getDrawable(context, R.drawable.bg_pattern);
        previewBackground.setImageDrawable(tilePattern(pattern));

        final BubbleSeekBar.OnProgressChangedListener listener = new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(final BubbleSeekBar bubbleSeekBar, final int progress, final float progressFloat, final boolean fromUser) {
                update();
            }
        };

        alpha.setOnProgressChangedListener(listener);
        red.setOnProgressChangedListener(listener);
        green.setOnProgressChangedListener(listener);
        blue.setOnProgressChangedListener(listener);
    }

    private void update() {
        log(DEBUG, TAG, "update");

        selected = Color.argb(alpha.getProgress(), red.getProgress(), green.getProgress(), blue.getProgress());
        preview.setBackgroundColor(selected);
        if (listener != null) {
            listener.onColorChanged(selected);
        }
    }

    public void setOnColorChangedListener(final OnColorChangedListener listener) {
        log(DEBUG, TAG, "setOnColorChangedListener");
        this.listener = listener;
    }

    @ColorInt
    public int getSelectedColor() {
        return selected;
    }

    public void setSelectedColor(@ColorInt final int color) {
        log(DEBUG, TAG, "setSelectedColor");

        alpha.setProgress(alpha(color));
        red.setProgress(red(color));
        green.setProgress(green(color));
        blue.setProgress(blue(color));
    }
}
