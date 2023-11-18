package com.judas.katachi.utility

import android.content.Context
import android.util.TypedValue

fun Float.dpToPx(context: Context): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics
).toInt()
