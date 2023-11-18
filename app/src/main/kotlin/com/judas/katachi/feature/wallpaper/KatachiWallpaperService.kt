package com.judas.katachi.feature.wallpaper

import android.content.Context
import android.graphics.Paint
import android.service.wallpaper.WallpaperService
import android.text.TextPaint
import android.view.SurfaceHolder
import androidx.core.content.res.ResourcesCompat
import com.judas.katachi.R
import com.judas.katachi.feature.api.ProGamesApi
import com.judas.katachi.feature.api.model.ProGame
import com.judas.katachi.feature.configuration.ConfigurationRepository
import com.judas.katachi.feature.goban.GobanDrawer
import com.judas.katachi.feature.theme.Theme
import com.judas.katachi.feature.theme.toPaints
import com.judas.katachi.utility.Logger.Level.DEBUG
import com.judas.katachi.utility.Logger.Level.ERROR
import com.judas.katachi.utility.dpToPx
import com.judas.katachi.utility.ioScope
import com.judas.katachi.utility.log
import com.judas.katachi.utility.readFileFrom
import com.judas.sgf4k.feature.exceptions.Sgf4kRuntimeException
import com.judas.sgf4k.feature.interpreter.Goban
import com.judas.sgf4k.feature.interpreter.Interpreter
import com.judas.sgf4k.feature.parser.model.GameNode
import com.judas.sgf4k.feature.parser.model.properties.SgfStandardPropertyKey.BR
import com.judas.sgf4k.feature.parser.model.properties.SgfStandardPropertyKey.DT
import com.judas.sgf4k.feature.parser.model.properties.SgfStandardPropertyKey.EV
import com.judas.sgf4k.feature.parser.model.properties.SgfStandardPropertyKey.PB
import com.judas.sgf4k.feature.parser.model.properties.SgfStandardPropertyKey.PW
import com.judas.sgf4k.feature.parser.model.properties.SgfStandardPropertyKey.WR
import com.judas.sgf4k.toGameCollection
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToLong
import kotlin.random.Random

class KatachiWallpaperService : WallpaperService() {
    private lateinit var engine: KatachiWallpaperEngine

    override fun onCreateEngine(): Engine {
        log(DEBUG, "onCreateEngine")

        // Find theme from repository
        with(ConfigurationRepository()) {
            engine = KatachiWallpaperEngine(this@KatachiWallpaperService)
            return engine
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        log(DEBUG, "onDestroy")
        engine.release()
    }

    inner class KatachiWallpaperEngine(private val context: Context) : Engine() {
        // Content
        private var theme: Theme = Theme.Classic
        private var moveSpeed: Float = 2f
        private var playJoseki: Boolean = true
        private var gameNode: GameNode? = null
        private var interpreter: Interpreter? = null
        private var josekiMode: Boolean = true

        private var josekiNumber: String = ""
        private var date: String = ""
        private var event: String = ""
        private var blackPlayer: String = ""
        private var whitePlayer: String = ""

        private val proGamesApi = ProGamesApi()
        private val proGames = mutableListOf<ProGame>()

        // UI
        private var width = 0f
        private var height = 0f
        private var textPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 12f.dpToPx(context).toFloat()
            color = theme.textColor()
        }
        private val formatterIn = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private val formatterOut = DateFormat.getDateInstance(DateFormat.LONG)

        // Timer
        private var tickFlow = flow {
            delay((moveSpeed * 2000).roundToLong())
            while (true) {
                emit(true)
                delay((moveSpeed * 1000).roundToLong())
            }
        }
        private var tickJob: Job? = null
        private val tickFlowExceptionHandler = CoroutineExceptionHandler { _, e ->
            log(ERROR, "Timer error", e)
            start()
        }

        init {
            loadNextGame()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            log(DEBUG, "onSurfaceChanged")

            this.width = width.toFloat()
            this.height = height.toFloat()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            log(DEBUG, "onVisibilityChanged $visible")

            if (visible) start()
            else stop()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            log(DEBUG, "onSurfaceDestroyed")
            stop()
        }

        private fun start() {
            log(DEBUG, "start")

            stop()
            tickJob = CoroutineScope(Dispatchers.IO + tickFlowExceptionHandler).launch {
                log(DEBUG, "Starting tick job")
                tickFlow.collect {
                    log(DEBUG, "tickFlow tick")
                    loadNextMove()
                }
            }
        }

        private fun stop() {
            log(DEBUG, "stop")
            tickJob?.let {
                log(DEBUG, "stopping tick job")
                it.cancel()
            }
        }

        fun release() {
            log(DEBUG, "release")
            stop()
        }

        private fun reset() {
            log(DEBUG, "reset")

            stop()

            ConfigurationRepository().also {
                theme = Theme(
                    backgroundColor = it.backgroundColor,
                    lineColor = it.lineColor,
                    blackStoneColor = it.blackStoneColor,
                    blackStoneBorderColor = it.blackStoneBorderColor,
                    whiteStoneColor = it.whiteStoneColor,
                    whiteStoneBorderColor = it.whiteStoneBorderColor,
                )
                moveSpeed = it.moveSpeed
                playJoseki = it.playJoseki
            }
            josekiMode = playJoseki

            textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 12f.dpToPx(context).toFloat()
                color = theme.textColor()
            }

            tickFlow = flow {
                delay((moveSpeed * 4000).roundToLong())
                while (true) {
                    emit(true)
                    delay((moveSpeed * 1000).roundToLong())
                }
            }

            start()
        }

        private fun loadNextGame() {
            log(DEBUG, "loadNextGame")

            reset()

            when {
                playJoseki -> loadRandomJoseki()

                proGames.isEmpty() -> {
                    // Fetch game ids
                    ioScope {
                        with(proGamesApi.recentProGames()) {
                            if (isEmpty()) loadRandomJoseki()
                            else {
                                proGames.addAll(this)
                                loadProGame(proGames.removeFirst())
                            }
                        }
                    }
                }

                else -> loadProGame(proGames.removeFirst())
            }
        }

        private fun loadRandomJoseki() {
            josekiNumber = Random.nextInt(0, 7454).toString().padStart(4, '0')
            log(DEBUG, "loadRandomJoseki $josekiNumber")
            val sgf = "$josekiNumber.sgf".readFileFrom(context.assets)
            josekiMode = true
            loadSgf(sgf)
        }

        private fun loadProGame(proGame: ProGame) {
            log(DEBUG, "loadProGame ${proGame.gameId}")

            ioScope {
                with(proGamesApi.gameSgf(proGame)) {
                    if (isBlank()) loadRandomJoseki()
                    else {
                        josekiMode = false
                        loadSgf(this)
                    }
                }
            }
        }

        private fun loadSgf(sgf: String) {
            log(DEBUG, "loadSgf")

            try {
                val game = sgf.toGameCollection().games.first()
                gameNode = game.rootNode
                interpreter = Interpreter(game)

                if (!josekiMode) {
                    date = game.rootNode.getProperty(DT)
                        ?.values?.firstOrNull()
                        ?.let { formatterIn.parse(it, ParsePosition(0)) }
                        ?.let { formatterOut.format(it) }
                        ?: ""
                    event = game.rootNode.getProperty(EV)?.values?.firstOrNull() ?: ""

                    val blackName = game.rootNode.getProperty(PB)?.values?.firstOrNull() ?: ""
                    val blackRank = game.rootNode.getProperty(BR)?.values?.firstOrNull() ?: ""
                    blackPlayer = "$blackName ($blackRank)"

                    val whiteName = game.rootNode.getProperty(PW)?.values?.firstOrNull() ?: ""
                    val whiteRank = game.rootNode.getProperty(WR)?.values?.firstOrNull() ?: ""
                    whitePlayer = "$whiteName ($whiteRank)"
                }
            } catch (e: Sgf4kRuntimeException) {
                log(ERROR, "Error loading SGF", e)
                loadRandomJoseki()
            }
        }

        private fun loadNextMove() {
            log(DEBUG, "loadNextMove")

            if (interpreter == null || gameNode == null) drawLoading()
            else {
                gameNode = gameNode?.children?.firstOrNull()
                gameNode?.let {
                    drawGoban(interpreter!!.gobanFor(it))
                } ?: run {
                    // No more child, load next game
                    gameNode = null
                    interpreter = null
                    loadNextGame()
                }
            }
        }

        private fun drawLoading() {
            log(DEBUG, "drawLoading")

            surfaceHolder.lockCanvas()?.apply {
                // Draw background
                drawColor(theme.backgroundColor)

                // Draw loading
                val marginV = (height - width) / 2f
                textPaint.textAlign = Paint.Align.CENTER
                textPaint.typeface = ResourcesCompat.getFont(context, R.font.qs_medium)
                drawText("Loading...", width / 2f, marginV - textPaint.textSize, textPaint)

                surfaceHolder.unlockCanvasAndPost(this)
            }
        }

        private fun drawGoban(goban: Goban) {
            log(DEBUG, "drawGoban")

            val themePaints = theme.toPaints(context)

            surfaceHolder.lockCanvas()?.apply {
                // Draw background
                drawColor(theme.backgroundColor)

                // Draw game info
                val marginV = (height - width) / 2f
                if (marginV > 0) {
                    if (josekiMode) {
                        // Joseki number
                        textPaint.textAlign = Paint.Align.CENTER
                        textPaint.typeface = ResourcesCompat.getFont(context, R.font.qs_medium)
                        drawText(
                            "Joseki variation #$josekiNumber",
                            width / 2f,
                            marginV - 0.5f * textPaint.textSize,
                            textPaint
                        )
                    } else {
                        // Date
                        textPaint.textAlign = Paint.Align.CENTER
                        textPaint.typeface = ResourcesCompat.getFont(context, R.font.qs_medium)
                        drawText(date, width / 2f, marginV - 0.5f * textPaint.textSize, textPaint)

                        // Event
                        drawText(event, width / 2f, marginV - 2 * textPaint.textSize, textPaint)

                        // Black stone
                        var cx = width / 2f - textPaint.textSize
                        var cy = marginV - 4 * textPaint.textSize
                        val radius = textPaint.textSize * 2 / 3
                        drawCircle(cx, cy, radius, themePaints.blackStonePaint)
                        drawCircle(cx, cy, radius, themePaints.blackStoneBorderPaint)

                        // White stone
                        cx = width / 2f + textPaint.textSize
                        cy = marginV - 4 * textPaint.textSize
                        drawCircle(cx, cy, radius, themePaints.whiteStonePaint)
                        drawCircle(cx, cy, radius, themePaints.whiteStoneBorderPaint)
                        textPaint.typeface = ResourcesCompat.getFont(context, R.font.qs_bold)

                        // Black player
                        textPaint.textAlign = Paint.Align.RIGHT
                        drawText(
                            blackPlayer,
                            width / 2f - textPaint.textSize - radius * 2,
                            cy + textPaint.textSize / 3,
                            textPaint
                        )

                        // White player
                        textPaint.textAlign = Paint.Align.LEFT
                        drawText(
                            whitePlayer,
                            width / 2f + textPaint.textSize + radius * 2,
                            cy + textPaint.textSize / 3,
                            textPaint
                        )
                    }
                }

                // Draw goban
                GobanDrawer(context, goban, theme).drawOn(this, width.toFloat(), height.toFloat())

                surfaceHolder.unlockCanvasAndPost(this)
            }
        }
    }
}