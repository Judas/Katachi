package com.judas.katachi.feature.configuration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.judas.katachi.feature.configuration.theme.Theme
import com.judas.katachi.utility.Logger.Level.VERBOSE
import com.judas.katachi.utility.log

class ConfigurationViewModel : ViewModel() {
    private val configurationRepository: ConfigurationRepository = ConfigurationRepository()

    val themeLiveData: MutableLiveData<Theme> = MutableLiveData()
    val moveSpeedLiveData: MutableLiveData<Float> = MutableLiveData()
    val contentLiveData: MutableLiveData<Boolean> = MutableLiveData()

    // Content

    var moveSpeed: Float
        get() = configurationRepository.moveSpeed
        set(value) {
            configurationRepository.moveSpeed = value
            moveSpeedLiveData.value = value
        }

    var playJoseki: Boolean
        get() = configurationRepository.playJoseki
        set(value) {
            configurationRepository.playJoseki = value
            contentLiveData.value = value
        }

    // Goban

    var backgroundColor: Int
        get() = configurationRepository.backgroundColor
        set(value) {
            configurationRepository.backgroundColor = value
            refreshTheme()
        }

    var lineColor: Int
        get() = configurationRepository.lineColor
        set(value) {
            configurationRepository.lineColor = value
            refreshTheme()
        }

    // Black stones

    var blackStoneColor: Int
        get() = configurationRepository.blackStoneColor
        set(value) {
            configurationRepository.blackStoneColor = value
            refreshTheme()
        }

    var blackStoneBorderColor: Int
        get() = configurationRepository.blackStoneBorderColor
        set(value) {
            configurationRepository.blackStoneBorderColor = value
            refreshTheme()
        }

    // White stones

    var whiteStoneColor: Int
        get() = configurationRepository.whiteStoneColor
        set(value) {
            configurationRepository.whiteStoneColor = value
            refreshTheme()
        }

    var whiteStoneBorderColor: Int
        get() = configurationRepository.whiteStoneBorderColor
        set(value) {
            configurationRepository.whiteStoneBorderColor = value
            refreshTheme()
        }

    // Highlight
    var highlight: Highlight
        get() = configurationRepository.highlight
        set(value) {
            configurationRepository.highlight = value
            refreshTheme()
        }

    fun clear() {
        log(VERBOSE, "clear")
        configurationRepository.clear()
        refresh()
    }

    fun refresh() {
        log(VERBOSE, "refresh")

        moveSpeedLiveData.value = moveSpeed
        contentLiveData.value = playJoseki
        refreshTheme()
    }

    private fun refreshTheme() {
        log(VERBOSE, "refreshTheme")

        themeLiveData.value = Theme(
            backgroundColor = backgroundColor,
            lineColor = lineColor,
            blackStoneColor = blackStoneColor,
            blackStoneBorderColor = blackStoneBorderColor,
            whiteStoneColor = whiteStoneColor,
            whiteStoneBorderColor = whiteStoneBorderColor,
            highlight = highlight
        )
    }
}
