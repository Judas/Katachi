package com.judas.katachi.feature.configuration.theme

import android.content.Context
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.Join.MITER
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.core.graphics.ColorUtils
import com.judas.katachi.utility.dpToPx
import com.judas.sgf4k.feature.interpreter.IntersectionState
import com.judas.sgf4k.feature.interpreter.IntersectionState.BLACK
import com.judas.sgf4k.feature.interpreter.IntersectionState.WHITE

class ThemePaints(
    val backgroundPaint: Paint,
    val linePaint: Paint,
    val starPaint: Paint,
    val blackStonePaint: Paint,
    val blackStoneBorderPaint: Paint,
    val blackHighlightPaint: Paint,
    val whiteStonePaint: Paint,
    val whiteStoneBorderPaint: Paint,
    val whiteHighlightPaint: Paint
) {
    fun get(state: IntersectionState, stroke: Boolean, highlight: Boolean): Paint? = when {
        state == BLACK && highlight -> blackHighlightPaint
        state == BLACK && stroke -> blackStoneBorderPaint
        state == BLACK -> blackStonePaint
        state == WHITE && highlight -> whiteHighlightPaint
        state == WHITE && stroke -> whiteStoneBorderPaint
        state == WHITE -> whiteStonePaint
        else -> null
    }
}

fun Theme.toPaints(context: Context): ThemePaints = ThemePaints(
    backgroundPaint = fillPaint(backgroundColor),
    linePaint = strokePaint(context, lineColor),
    starPaint = fillPaint(lineColor),
    blackStonePaint = fillPaint(blackStoneColor),
    blackStoneBorderPaint = strokePaint(context, blackStoneBorderColor),
    blackHighlightPaint = fillPaint(whiteStoneColor),
    whiteStonePaint = fillPaint(whiteStoneColor),
    whiteStoneBorderPaint = strokePaint(context, whiteStoneBorderColor),
    whiteHighlightPaint = fillPaint(blackStoneColor)
)

private fun fillPaint(@ColorInt colorInt: Int): Paint =
    Paint(ANTI_ALIAS_FLAG).apply {
        style = FILL
        color = colorInt
    }

private fun strokePaint(context: Context, @ColorInt colorInt: Int): Paint =
    Paint(ANTI_ALIAS_FLAG).apply {
        style = STROKE
        color = colorInt
        strokeWidth = 1.2f.dpToPx(context).toFloat()
        strokeJoin = MITER
    }

fun Paint.adjustAlpha(@IntRange(from = 0x0, to = 0xFF) alpha: Int): Paint =
    Paint(this).apply { color = ColorUtils.setAlphaComponent(color, alpha) }

fun Paint.adjustColor(newColor: Int): Paint =
    Paint(this).apply { color = newColor }