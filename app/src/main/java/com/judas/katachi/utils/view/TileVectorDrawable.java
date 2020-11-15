package com.judas.katachi.utils.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.Bitmap.createBitmap;
import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.graphics.Shader.TileMode.REPEAT;

public class TileVectorDrawable extends Drawable {
    private final Paint paint;

    public static TileVectorDrawable tilePattern(final Drawable drawable) {
        return new TileVectorDrawable(drawable, REPEAT);
    }

    private TileVectorDrawable(final Drawable drawable, final Shader.TileMode tileMode) {
        paint = new Paint();
        paint.setShader(new BitmapShader(getBitmap(drawable), tileMode, tileMode));
    }

    @Override
    public void draw(@NonNull final Canvas canvas) {
        canvas.drawPaint(paint);
    }

    @Override
    public void setAlpha(final int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable final ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return TRANSLUCENT;
    }

    private Bitmap getBitmap(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        final int w = drawable.getIntrinsicWidth();
        final int h = drawable.getIntrinsicHeight();

        final Bitmap bmp = createBitmap(w, h, ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bmp;
    }
}
