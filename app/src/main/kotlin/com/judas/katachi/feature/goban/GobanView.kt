package com.judas.katachi.feature.goban

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import com.judas.katachi.utility.Logger.Level.VERBOSE
import com.judas.katachi.utility.log

class GobanView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    var drawer: GobanDrawer? = null
        set(value) {
            field = value
            postInvalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        log(VERBOSE, "onDraw")
        drawer?.drawOn(canvas, width.toFloat(), height.toFloat())
    }
}
