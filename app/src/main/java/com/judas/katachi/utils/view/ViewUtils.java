package com.judas.katachi.utils.view;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.applyDimension;

public final class ViewUtils {
    private ViewUtils() {
    }

    public static int dpToPx(final Context context, final float dp) {
        return (int) applyDimension(COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void showSnackbar(@NonNull final Activity activity, @StringRes final int message) {
        Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showToast(@NonNull final Context context, @StringRes final int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(@NonNull final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
