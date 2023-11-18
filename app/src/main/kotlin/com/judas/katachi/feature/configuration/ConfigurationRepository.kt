package com.judas.katachi.feature.configuration

import com.judas.katachi.feature.theme.Theme
import com.judas.katachi.utility.prefReader
import com.judas.katachi.utility.prefWriter

class ConfigurationRepository {
    companion object {
        // Content
        private const val KEY_MOVE_SPEED = "move-speed"
        private const val KEY_JOSEKI = "joseki"

        // Goban
        private const val KEY_BACKGROUND = "background"
        private const val KEY_LINE_COLOR = "line-color"

        // Black stones
        private const val KEY_BLACK_STONE_COLOR = "black-stone-color"
        private const val KEY_BLACK_BORDER_COLOR = "black-border-color"

        // White stones
        private const val KEY_WHITE_STONE_COLOR = "white-stone-color"
        private const val KEY_WHITE_BORDER_COLOR = "white-border-color"

        private val keys = listOf(
            KEY_MOVE_SPEED,
            KEY_JOSEKI,
            KEY_BACKGROUND,
            KEY_LINE_COLOR,
            KEY_BLACK_STONE_COLOR,
            KEY_BLACK_BORDER_COLOR,
            KEY_WHITE_STONE_COLOR,
            KEY_WHITE_BORDER_COLOR
        )
    }

    fun clear() = keys.iterator().forEach { prefWriter().remove(it).apply() }

    // Content

    var moveSpeed: Float
        get() = prefReader().getFloat(KEY_MOVE_SPEED, 2f)
        set(value) = prefWriter().putFloat(KEY_MOVE_SPEED, value).apply()

    var playJoseki: Boolean
        get() = prefReader().getBoolean(KEY_JOSEKI, true)
        set(value) = prefWriter().putBoolean(KEY_JOSEKI, value).apply()

    // Goban

    var backgroundColor: Int
        get() = prefReader().getInt(KEY_BACKGROUND, Theme.Classic.backgroundColor)
        set(value) = prefWriter().putInt(KEY_BACKGROUND, value).apply()

    var lineColor: Int
        get() = prefReader().getInt(KEY_LINE_COLOR, Theme.Classic.lineColor)
        set(value) = prefWriter().putInt(KEY_LINE_COLOR, value).apply()

    // Black stones

    var blackStoneColor: Int
        get() = prefReader().getInt(KEY_BLACK_STONE_COLOR, Theme.Classic.blackStoneColor)
        set(value) = prefWriter().putInt(KEY_BLACK_STONE_COLOR, value).apply()

    var blackStoneBorderColor: Int
        get() = prefReader().getInt(KEY_BLACK_BORDER_COLOR, Theme.Classic.blackStoneBorderColor)
        set(value) = prefWriter().putInt(KEY_BLACK_BORDER_COLOR, value).apply()

    // White stones

    var whiteStoneColor: Int
        get() = prefReader().getInt(KEY_WHITE_STONE_COLOR, Theme.Classic.whiteStoneColor)
        set(value) = prefWriter().putInt(KEY_WHITE_STONE_COLOR, value).apply()

    var whiteStoneBorderColor: Int
        get() = prefReader().getInt(KEY_WHITE_BORDER_COLOR, Theme.Classic.whiteStoneBorderColor)
        set(value) = prefWriter().putInt(KEY_WHITE_BORDER_COLOR, value).apply()
}
