package com.judas.katachi.core.wallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.service.wallpaper.WallpaperService;
import android.text.TextPaint;
import android.view.SurfaceHolder;

import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.judas.katachi.R;
import com.judas.katachi.core.api.data.Go4GoGame;
import com.judas.katachi.core.prefs.PreferenceHelper;
import com.judas.katachi.core.theme.KatachiTheme;
import com.judas.katachi.utils.view.BoardDrawer;
import com.toomasr.sgf4j.Sgf;
import com.toomasr.sgf4j.board.VirtualBoard;
import com.toomasr.sgf4j.parser.Game;
import com.toomasr.sgf4j.parser.GameNode;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;
import static android.graphics.Paint.Align.LEFT;
import static android.graphics.Paint.Align.RIGHT;
import static com.judas.katachi.core.api.Go4Go.recentProGames;
import static com.judas.katachi.core.api.Go4Go.sgfFor;
import static com.judas.katachi.core.prefs.PreferenceHelper.ACTION_WALLPAPER_SETTINGS_CHANGED;
import static com.judas.katachi.core.prefs.PreferenceHelper.prefs;
import static com.judas.katachi.core.wallpaper.WallpaperContent.PRO_GAMES;
import static com.judas.katachi.utils.async.RxUtils.flowableSchedulers;
import static com.judas.katachi.utils.async.RxUtils.observableSchedulers;
import static com.judas.katachi.utils.async.RxUtils.singleSchedulers;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;
import static com.judas.katachi.utils.log.Logger.logError;
import static com.judas.katachi.utils.view.ViewUtils.dpToPx;
import static com.judas.katachi.utils.view.ViewUtils.showToast;
import static com.toomasr.sgf4j.board.StoneState.BLACK;
import static com.toomasr.sgf4j.board.StoneState.WHITE;
import static io.reactivex.rxjava3.core.Flowable.interval;
import static java.text.DateFormat.LONG;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class KatachiWallpaperService extends WallpaperService {
    private static final String TAG = KatachiWallpaperService.class.getSimpleName();
    private static final String FILE_FORMAT = "%04d";
    private static final int JOSEKI_COUNT = 7454;

    private KatachiWallpaperEngine engine;

    @Override
    public Engine onCreateEngine() {
        log(DEBUG, TAG, "onCreateEngine");

        engine = new KatachiWallpaperEngine(this);
        return engine;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log(DEBUG, TAG, "onDestroy");

        engine.release();
    }

    public class KatachiWallpaperEngine extends Engine {
        private final BroadcastReceiver settingsUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                reset();
            }
        };

        private final BoardDrawer drawer;

        private final Context context;
        private long speedMs;
        private long delayMs;
        private WallpaperContent content;

        private float width;
        private float height;

        private final List<Go4GoGame> proGames = new ArrayList<>();
        private int proGameIndex = -1;
        private final Random random = new Random();

        private final TextPaint textPaint;
        private final float textSize;
        private final SimpleDateFormat formatterIn = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        private final DateFormat formatterOut = DateFormat.getDateInstance(LONG);

        private Disposable timer;
        private final CompositeDisposable disposables = new CompositeDisposable();

        public KatachiWallpaperEngine(final Context context) {
            log(DEBUG, TAG, "KatachiWallpaperEngine");

            this.context = context;

            drawer = new BoardDrawer(context);
            drawer.setBoard(new VirtualBoard());

            textSize = dpToPx(context, 12);
            textPaint = new TextPaint(ANTI_ALIAS_FLAG);
            textPaint.setTextSize(textSize);

            initialize();
        }

        @Override
        public void onSurfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
            super.onSurfaceChanged(holder, format, width, height);
            log(DEBUG, TAG, "onSurfaceChanged");
            this.width = width;
            this.height = height;
            drawer.setWidth(width);
            drawer.setHeight(height);
        }

        @Override
        public void onVisibilityChanged(final boolean visible) {
            log(DEBUG, TAG, "onVisibilityChanged " + visible);

            if (visible && timer != null) {
                startTimer();
            } else {
                stopTimer();
            }
        }

        @Override
        public void onSurfaceDestroyed(final SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            log(DEBUG, TAG, "onSurfaceDestroyed");
            stopTimer();
        }

        private void initialize() {
            log(DEBUG, TAG, "initialize");

            proGames.clear();
            disposables.add(recentProGames()
                    .onErrorComplete()
                    .compose(observableSchedulers())
                    .subscribe(
                            proGames::add,
                            error -> logError(TAG, "loadProGame", error),
                            this::reset
                    ));

            LocalBroadcastManager.getInstance(context).registerReceiver(settingsUpdateReceiver, new IntentFilter(ACTION_WALLPAPER_SETTINGS_CHANGED));
        }

        private void reset() {
            log(DEBUG, TAG, "reset");

            stopTimer();

            final PreferenceHelper prefs = prefs(context);
            final KatachiTheme theme = prefs.getWallPaperTheme(context);
            speedMs = Float.valueOf(prefs.getWallpaperSpeed() * 1000).longValue();
            delayMs = Float.valueOf(prefs.getWallpaperDelay() * 1000).longValue();
            drawer.setTheme(theme);
            content = prefs.getWallPaperContent();

            textPaint.setColor(theme.lineColor);

            loadContent();
        }

        public void release() {
            log(DEBUG, TAG, "release");

            stopTimer();
            disposables.dispose();
            LocalBroadcastManager.getInstance(context).unregisterReceiver(settingsUpdateReceiver);
        }

        private void loadContent() {
            log(DEBUG, TAG, "loadContent");

            if (content == PRO_GAMES && !proGames.isEmpty()) {
                loadNextProGame();
            } else {
                loadRandomJoseki();
            }
        }

        private void loadNextProGame() {
            log(DEBUG, TAG, "loadNextProGame");

            proGameIndex++;

            if (proGameIndex >= proGames.size()) {
                proGameIndex = -1;
                initialize();
                return;
            }

            final Go4GoGame go4GoGame = proGames.get(proGameIndex);
            disposables.add(sgfFor(go4GoGame)
                    .compose(singleSchedulers())
                    .subscribe(
                            sgf -> {
                                final Game game = Sgf.createFromString(go4GoGame.sgfHeader + sgf);
                                drawer.setGame(game);
                                drawer.setGameNode(game.getRootNode());
                                startTimer();
                            },
                            error -> {
                                logError(TAG, "loadNextProGame", error);
                                loadRandomJoseki();
                            }
                    ));
        }

        private void loadRandomJoseki() {
            log(DEBUG, TAG, "loadRandomJoseki");

            final String filename = String.format(FILE_FORMAT, random.nextInt(JOSEKI_COUNT)) + ".sgf";
            try {
                final InputStream stream = getAssets().open(filename);
                final Game game = Sgf.createFromInputStream(stream);
                stream.close();

                drawer.setGame(game);
                drawer.setGameNode(game.getRootNode());
                startTimer();
            } catch (final IOException e) {
                logError(TAG, "loadRandomJoseki", e);
            }
        }

        private void startTimer() {
            log(DEBUG, TAG, "startTimer");

            stopTimer();

            timer = interval(delayMs, speedMs, MILLISECONDS)
                    .onBackpressureDrop()
                    .compose(flowableSchedulers())
                    .subscribe(
                            tick -> loadNextMove(),
                            error -> {
                                logError(TAG, "startTimer", error);
                                showToast(KatachiWallpaperService.this, R.string.wallpaper_failure);
                            }
                    );
        }

        private void stopTimer() {
            log(DEBUG, TAG, "stopTimer");
            if (timer != null) {
                timer.dispose();
            }
        }

        private void loadNextMove() {
            log(DEBUG, TAG, "loadNextMove");

            // If game/joseki is not yet loaded, skip
            final GameNode current = drawer.getGameNode();
            if (current == null) {
                return;
            }

            // If game/joseki is finished, load next
            final GameNode next = current.getNextNode();
            if (next == null) {
                reset();
            } else {
                drawer.setGameNode(next);
                draw();
            }
        }

        private void draw() {
            log(DEBUG, TAG, "draw");

            final SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();

                if (canvas != null) {
                    canvas.drawColor(drawer.getTheme().backgroundColor);

                    final float marginV = (height - width) / 2f;
                    if (content == PRO_GAMES && marginV > 0) {
                        final Map<String, String> props = drawer.getGame().getProperties();
                        final KatachiTheme theme = drawer.getTheme();

                        textPaint.setTextAlign(CENTER);
                        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.qs_medium));

                        // Date
                        final Date date = formatterIn.parse(props.get("DT"), new ParsePosition(0));
                        final String dateString = formatterOut.format(date);
                        canvas.drawText(dateString, width / 2f, marginV - textSize, textPaint);

                        // Event
                        final String event = props.get("EV");
                        canvas.drawText(event, width / 2f, marginV - 2 * textSize, textPaint);

                        // Black stone
                        float cx = width / 2f - textSize;
                        float cy = marginV - 4 * textSize;
                        float radius = textSize * 2 / 3;
                        canvas.drawCircle(cx, cy, radius, theme.getStonePaint(BLACK, false));
                        canvas.drawCircle(cx, cy, radius, theme.getStoneStrokePaint(BLACK, false));

                        // White stone
                        cx = width / 2f + textSize;
                        cy = marginV - 4 * textSize;
                        canvas.drawCircle(cx, cy, radius, theme.getStonePaint(WHITE, false));
                        canvas.drawCircle(cx, cy, radius, theme.getStoneStrokePaint(WHITE, false));

                        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.qs_bold));

                        // Black player
                        final String black = props.get("PB") + " (" + props.get("BR") + ")";
                        textPaint.setTextAlign(RIGHT);
                        canvas.drawText(black, width / 2f - textSize - radius * 2, cy + textSize / 3, textPaint);

                        // White player
                        final String white = props.get("PW") + " (" + props.get("WR") + ")";
                        textPaint.setTextAlign(LEFT);
                        canvas.drawText(white, width / 2f + textSize + radius * 2, cy + textSize / 3, textPaint);
                    }

                    drawer.drawOn(canvas);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
