package com.judas.katachi.feature.configuration

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.judas.katachi.R
import com.judas.katachi.databinding.ConfigurationFragmentBinding
import com.judas.katachi.databinding.ThemeChipBinding
import com.judas.katachi.feature.goban.GobanDrawer
import com.judas.katachi.feature.theme.Theme
import com.judas.katachi.utility.Logger.Level.DEBUG
import com.judas.katachi.utility.log
import com.judas.katachi.utility.readFileFrom
import com.judas.sgf4k.feature.interpreter.Goban
import com.judas.sgf4k.feature.interpreter.Interpreter
import com.judas.sgf4k.toGameCollection
import com.xw.repo.BubbleSeekBar

class ConfigurationFragment : Fragment() {
    private lateinit var viewBinding: ConfigurationFragmentBinding
    private lateinit var viewModel: ConfigurationViewModel

    private val goban: Goban
        get() {
            val sgfString = "ear-reddening-move.sgf".readFileFrom(requireActivity().assets)
            val game = sgfString.toGameCollection().games.first()
            return Interpreter(game).gobanFor(game.rootNode.children.first())
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        log(DEBUG, "onCreateView")

        viewModel = ViewModelProvider(requireActivity())[ConfigurationViewModel::class.java]
        viewModel.moveSpeedLiveData.observe(viewLifecycleOwner, this::onMoveSpeedUpdated)
        viewModel.contentLiveData.observe(viewLifecycleOwner, this::onContentUpdated)
        viewModel.themeLiveData.observe(viewLifecycleOwner, this::onThemeUpdated)

        viewBinding =
            DataBindingUtil.inflate(inflater, R.layout.configuration_fragment, container, false)
        viewBinding.viewModel = viewModel
        viewBinding.lifecycleOwner = viewLifecycleOwner
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log(DEBUG, "onViewCreated")

        loadPresetThemeChips()
        loadMoveSpeed()
        loadContent()
        loadTheme()

        // Emit theme value to trigger first update
        viewModel.refresh()
    }

    private fun loadPresetThemeChips() {
        log(DEBUG, "loadPresetThemeChips")

        listOf(Theme.Classic, Theme.Paper, Theme.Cosmic, Theme.Katachi).forEach { theme ->
            val themeChipBinding: ThemeChipBinding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.theme_chip,
                viewBinding.themeChipGroup,
                true
            )
            themeChipBinding.themeChip.text = theme::class.java.simpleName
            themeChipBinding.themeChip.setOnClickListener {
                viewModel.backgroundColor = theme.backgroundColor
                viewModel.lineColor = theme.lineColor
                viewModel.blackStoneColor = theme.blackStoneColor
                viewModel.blackStoneBorderColor = theme.blackStoneBorderColor
                viewModel.whiteStoneColor = theme.whiteStoneColor
                viewModel.whiteStoneBorderColor = theme.whiteStoneBorderColor
            }
        }
    }

    private fun loadMoveSpeed() {
        log(DEBUG, "loadMoveSpeed")

        with(viewBinding.settingsView.moveSpeedPicker) {
            configBuilder.min(0.5f).max(10f).progress(viewModel.moveSpeed).build()
            onProgressChangedListener =
                object : BubbleSeekBar.OnProgressChangedListenerAdapter() {
                    override fun onProgressChanged(
                        bubbleSeekBar: BubbleSeekBar,
                        progress: Int,
                        progressFloat: Float,
                        fromUser: Boolean
                    ) {
                        if (fromUser) viewModel.moveSpeed = progressFloat
                    }
                }
        }
    }

    private fun loadContent() {
        log(DEBUG, "loadContent")

        with(viewBinding.settingsView.contentPicker) {
            text = getString(
                if (viewModel.playJoseki) R.string.config_content_joseki
                else R.string.config_content_progames
            )
            setOnClickListener {
                showContentDialog { content ->
                    viewModel.playJoseki = content == getString(R.string.config_content_joseki)
                }
            }
        }
    }

    private fun loadTheme() {
        log(DEBUG, "loadTheme")

        with(viewBinding.settingsView) {
            mapOf(
                backgroundPicker to R.string.config_theme_background,
                linesColorPicker to R.string.config_theme_line_color,
                blackStonesColorPicker to R.string.config_theme_black_stones_color,
                blackStonesBorderColorPicker to R.string.config_theme_black_stones_border_color,
                whiteStonesColorPicker to R.string.config_theme_white_stones_color,
                whiteStonesBorderColorPicker to R.string.config_theme_white_stones_border_color
            ).forEach { entry ->
                entry.key.label.text = getString(entry.value)
                entry.key.colorPicker.setOnClickListener { v ->
                    showColorPickerDialog(getColor(entry.value)) { selectedColor ->
                        setColor(entry.value, selectedColor)
                    }
                }
            }
        }
    }

    private fun onMoveSpeedUpdated(moveSpeed: Float) {
        log(DEBUG, "onMoveSpeedUpdated $moveSpeed")
        viewBinding.settingsView.moveSpeedPicker.setProgress(moveSpeed)
    }

    private fun onContentUpdated(playJoseki: Boolean) {
        log(DEBUG, "onContentUpdated $playJoseki")

        viewBinding.settingsView.contentPicker.text = getString(
            if (playJoseki) R.string.config_content_joseki
            else R.string.config_content_progames
        )
    }

    private fun onThemeUpdated(theme: Theme) {
        log(DEBUG, "onThemeUpdated")

        viewBinding.preview.drawer = GobanDrawer(requireContext(), goban, theme)

        with(viewBinding.settingsView) {
            mapOf(
                backgroundPicker to theme.backgroundColor,
                linesColorPicker to theme.lineColor,
                blackStonesColorPicker to theme.blackStoneColor,
                blackStonesBorderColorPicker to theme.blackStoneBorderColor,
                whiteStonesColorPicker to theme.whiteStoneColor,
                whiteStonesBorderColorPicker to theme.whiteStoneBorderColor
            ).forEach { it.key.colorPicker.imageTintList = ColorStateList.valueOf(it.value) }
        }
    }

    private fun showContentDialog(listener: (String) -> Unit) {
        log(DEBUG, "showContentDialog")

        val contentLabels = listOf(R.string.config_content_joseki, R.string.config_content_progames)
            .map { getString(it) }
            .toTypedArray()

        AlertDialog.Builder(requireContext())
            .setItems(contentLabels) { _, index -> listener.invoke(contentLabels[index]) }
            .create()
            .show()
    }

    private fun showColorPickerDialog(@ColorInt initialColor: Int, listener: (Int) -> Unit) {
        log(DEBUG, "showColorPickerDialog")

        ColorPickerDialogBuilder
            .with(context)
            .setTitle(R.string.color_picker_dialog_title)
            .initialColor(initialColor)
            .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
            .density(12)
            .setOnColorSelectedListener {}
            .setPositiveButton(android.R.string.ok) { _, selectedColor, _ ->
                listener.invoke(selectedColor)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .build()
            .show()
    }

    private fun getColor(key: Int): Int = when (key) {
        R.string.config_theme_background ->
            viewModel.backgroundColor

        R.string.config_theme_line_color ->
            viewModel.lineColor

        R.string.config_theme_black_stones_color ->
            viewModel.blackStoneColor

        R.string.config_theme_black_stones_border_color ->
            viewModel.blackStoneBorderColor

        R.string.config_theme_white_stones_color ->
            viewModel.whiteStoneColor

        R.string.config_theme_white_stones_border_color ->
            viewModel.whiteStoneBorderColor

        else -> Color.BLACK
    }

    private fun setColor(key: Int, color: Int) {
        when (key) {
            R.string.config_theme_background ->
                viewModel.backgroundColor = color

            R.string.config_theme_line_color ->
                viewModel.lineColor = color

            R.string.config_theme_black_stones_color ->
                viewModel.blackStoneColor = color

            R.string.config_theme_black_stones_border_color ->
                viewModel.blackStoneBorderColor = color

            R.string.config_theme_white_stones_color ->
                viewModel.whiteStoneColor = color

            R.string.config_theme_white_stones_border_color ->
                viewModel.whiteStoneBorderColor = color
        }
    }
}
