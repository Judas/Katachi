package com.judas.katachi.feature.theme

import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.TRANSPARENT
import android.graphics.Color.WHITE

open class Theme(
    val backgroundColor: Int,
    val lineColor: Int,
    val blackStoneColor: Int,
    val blackStoneBorderColor: Int,
    val whiteStoneColor: Int,
    val whiteStoneBorderColor: Int
) {
    fun textColor(): Int =
        if (lineColor != TRANSPARENT && lineColor != backgroundColor) lineColor
        else if (blackStoneColor != TRANSPARENT && blackStoneColor != backgroundColor) blackStoneColor
        else if (whiteStoneColor != TRANSPARENT && whiteStoneColor != backgroundColor) whiteStoneColor
        else Color.rgb(
            255 - Color.red(backgroundColor),
            255 - Color.green(backgroundColor),
            255 - Color.blue(backgroundColor)
        )

    data object Classic : Theme(
        backgroundColor = 0xFFCD8500.toInt(),
        lineColor = BLACK,
        blackStoneColor = BLACK,
        blackStoneBorderColor = BLACK,
        whiteStoneColor = WHITE,
        whiteStoneBorderColor = BLACK
    )

    data object Paper : Theme(
        backgroundColor = 0xFFF4F4F4.toInt(),
        lineColor = 0xFF0C0C0C.toInt(),
        blackStoneColor = 0xFF0C0C0C.toInt(),
        blackStoneBorderColor = 0xFF0C0C0C.toInt(),
        whiteStoneColor = 0xFFF4F4F4.toInt(),
        whiteStoneBorderColor = 0xFF0C0C0C.toInt()
    )

    data object Cosmic : Theme(
        backgroundColor = 0xFF212529.toInt(),
        lineColor = TRANSPARENT,
        blackStoneColor = 0xFF212529.toInt(),
        blackStoneBorderColor = 0xFFCED4DA.toInt(),
        whiteStoneColor = 0xFFCED4DA.toInt(),
        whiteStoneBorderColor = 0xFFCED4DA.toInt(),
    )

    data object Katachi : Theme(
        backgroundColor = 0xFF001849.toInt(),
        lineColor = 0xFFFCA311.toInt(),
        blackStoneColor = 0xFF001849.toInt(),
        blackStoneBorderColor = 0xFFFCA311.toInt(),
        whiteStoneColor = 0xFFFCA311.toInt(),
        whiteStoneBorderColor = 0xFFFCA311.toInt()
    )
}
