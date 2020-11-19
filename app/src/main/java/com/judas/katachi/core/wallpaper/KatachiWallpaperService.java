package com.judas.katachi.core.wallpaper;

import android.content.Context;
import android.graphics.Canvas;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.judas.katachi.R;
import com.judas.katachi.core.prefs.PreferenceHelper;
import com.judas.katachi.core.theme.KatachiTheme;
import com.judas.katachi.utils.view.BoardDrawer;
import com.toomasr.sgf4j.Sgf;
import com.toomasr.sgf4j.board.VirtualBoard;
import com.toomasr.sgf4j.parser.Game;
import com.toomasr.sgf4j.parser.GameNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import io.reactivex.rxjava3.disposables.Disposable;

import static com.judas.katachi.core.prefs.PreferenceHelper.prefs;
import static com.judas.katachi.utils.async.RxUtils.flowableSchedulers;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.Level.ERROR;
import static com.judas.katachi.utils.log.Logger.log;
import static com.judas.katachi.utils.log.Logger.logError;
import static com.judas.katachi.utils.view.ViewUtils.showToast;
import static io.reactivex.rxjava3.core.Flowable.interval;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class KatachiWallpaperService extends WallpaperService {
    private static final String TAG = KatachiWallpaperService.class.getSimpleName();
    private static final String FILE_FORMAT = "%04d";
    private static final int JOSEKI_COUNT = 7454;

    @Override
    public Engine onCreateEngine() {
        log(DEBUG, TAG, "onCreateEngine");
        return new KatachiWallpaperEngine(this);
    }

    public class KatachiWallpaperEngine extends Engine {
        private final BoardDrawer drawer;

        private final long speedMs;
        private final long delayMs;

        private final Random random = new Random();
        private Disposable timer;

        public KatachiWallpaperEngine(final Context context) {
            log(DEBUG, TAG, "KatachiWallpaperEngine");

            final PreferenceHelper prefs = prefs(context);
            speedMs = Float.valueOf(prefs.getWallpaperSpeed() * 1000).longValue();
            delayMs = Float.valueOf(prefs.getWallpaperDelay() * 1000).longValue();

            drawer = new BoardDrawer(context);
            drawer.setTheme(prefs.getWallPaperTheme(context));
            drawer.setBoard(new VirtualBoard());

            log(DEBUG, TAG, "speed=" + speedMs + " delay" + delayMs);
            loadRandomJoseki();
            startTimer();
        }

        @Override
        public void onSurfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
            super.onSurfaceChanged(holder, format, width, height);
            log(DEBUG, TAG, "onSurfaceChanged");
            drawer.setWidth(width);
            drawer.setHeight(height);
        }

        @Override
        public void onVisibilityChanged(final boolean visible) {
            log(DEBUG, TAG, "onVisibilityChanged " + visible);
            if (visible) {
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

        private void loadRandomJoseki() {
            log(DEBUG, TAG, "loadRandomJoseki");

            final String filename = String.format(FILE_FORMAT, random.nextInt(JOSEKI_COUNT)) + ".sgf";
            try {
                final InputStream stream = getAssets().open(filename);
                final Game game = Sgf.createFromInputStream(stream);
                stream.close();

                drawer.setGame(game);
                drawer.setGameNode(game.getRootNode());
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
                                log(ERROR, TAG, "startTimer FAILURE");
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

            final GameNode next = drawer.getGameNode().getNextNode();

            // If sequence is finished, load next joseki
            if (next == null) {
                stopTimer();
                loadRandomJoseki();
                startTimer();
                return;
            }

            drawer.setGameNode(next);
            draw();
        }

        private void draw() {
            log(DEBUG, TAG, "loadNextMove");

            final SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(drawer.getTheme().backgroundColor);
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
