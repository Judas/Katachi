package com.judas.katachi.feature.goban

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import com.judas.katachi.feature.configuration.Highlight.CIRCLE
import com.judas.katachi.feature.configuration.Highlight.OPACITY
import com.judas.katachi.feature.configuration.theme.Theme
import com.judas.katachi.feature.configuration.theme.adjustAlpha
import com.judas.katachi.feature.configuration.theme.adjustColor
import com.judas.katachi.feature.configuration.theme.toPaints
import com.judas.katachi.utility.Logger.Level.ERROR
import com.judas.katachi.utility.Logger.Level.VERBOSE
import com.judas.katachi.utility.log
import com.judas.sgf4k.feature.interpreter.Goban
import java.lang.Float.max
import kotlin.math.roundToInt

class GobanDrawer(context: Context, private val goban: Goban, private val theme: Theme) {
    private val themePaints = theme.toPaints(context)
    private val bgRect: RectF = RectF()

    fun drawOn(canvas: Canvas, width: Float, height: Float) {
        log(VERBOSE, "drawOn [$width x $height]")

        // Quick exit
        if (width <= 0 || height <= 0) return

        try {
            val vMargin = max(0f, (height - width) / 2)
            val hMargin = max(0f, (width - height) / 2)
            val fullWidth = width - 2 * hMargin
            val gobanPadding = (fullWidth / goban.size) / 2
            val bgSize = fullWidth - 2 * gobanPadding

            // Draw goban background
            bgRect.set(0f, 0f, fullWidth, fullWidth)
            canvas.translate(hMargin, vMargin)
            canvas.drawRect(bgRect, themePaints.backgroundPaint)
            canvas.translate(gobanPadding, gobanPadding)

            // Draw goban lines
            val squareSize = (bgSize / goban.size).roundToInt()
            val padding = (bgSize - squareSize * (goban.size - 1)) / 2
            for (i in 0 until goban.size) {
                val linePadding = padding - themePaints.linePaint.strokeWidth / 2
                val offset = padding + i * squareSize
                val end = bgSize - linePadding
                canvas.drawLine(offset, linePadding, offset, end, themePaints.linePaint)
                canvas.drawLine(linePadding, offset, end, offset, themePaints.linePaint)
            }

            // Draw star points if size is adequate
            val starRadius = squareSize / 7.2f
            if (goban.size == 19) {
                listOf(
                    3 to 3, 9 to 3, 15 to 3,
                    3 to 9, 9 to 9, 15 to 9,
                    3 to 15, 9 to 15, 15 to 15
                ).forEach {
                    val cx = padding + it.first * squareSize
                    val cy = padding + it.second * squareSize
                    canvas.drawCircle(cx, cy, starRadius, themePaints.starPaint)
                }
            }

            // Draw stones
            goban.intersections.forEachIndexed { column, columns ->
                columns.forEachIndexed { row, intersection ->
                    val stonePaint = themePaints.get(
                        state = intersection.state,
                        stroke = false,
                        highlight = false
                    )
                    val stoneBorderPaint = themePaints.get(
                        state = intersection.state,
                        stroke = true,
                        highlight = false
                    )

                    if (stonePaint != null && stoneBorderPaint != null) {
                        val stoneRadius = squareSize / 2.1f
                        val cx = padding + column * squareSize
                        val cy = padding + row * squareSize
                        val isCurrent = goban.currentIntersection?.row == row
                                && goban.currentIntersection?.column == column
                        val alpha =
                            if (!isCurrent && theme.highlight == OPACITY) 100
                            else 255

                        // Draw background before stone to hide lines for translucent colors
                        canvas.drawCircle(cx, cy, stoneRadius, themePaints.backgroundPaint)
                        canvas.drawCircle(cx, cy, stoneRadius, stonePaint.adjustAlpha(alpha))

                        // Draw background before stone border to avoid sur-opacity
                        canvas.drawCircle(cx, cy, stoneRadius, stoneBorderPaint.adjustColor(themePaints.backgroundPaint.color))
                        canvas.drawCircle(cx, cy, stoneRadius, stoneBorderPaint.adjustAlpha(alpha))

                        if (isCurrent && theme.highlight == CIRCLE) {
                            val circleHighlightRadius = squareSize / 6.4f

                            themePaints.get(
                                state = intersection.state,
                                stroke = false,
                                highlight = true
                            )?.let { canvas.drawCircle(cx, cy, circleHighlightRadius, it) }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            log(ERROR, "drawOn", e)
        }
    }
}
