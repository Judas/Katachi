package com.judas.katachi.utils.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.judas.katachi.R;
import com.judas.katachi.core.theme.KatachiTheme;
import com.toomasr.sgf4j.board.StoneState;
import com.toomasr.sgf4j.board.VirtualBoard;
import com.toomasr.sgf4j.parser.Game;
import com.toomasr.sgf4j.parser.GameNode;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.text.TextUtils.isEmpty;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.log;
import static com.judas.katachi.utils.log.Logger.logError;
import static com.judas.katachi.utils.view.TileVectorDrawable.tilePattern;
import static com.toomasr.sgf4j.board.StoneState.EMPTY;
import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.lang.Math.round;

public class BoardDrawer {
    private static final String TAG = BoardDrawer.class.getSimpleName();
    private static final String SIZE_KEY = "SZ";
    private static final int DEFAULT_BOARD_SIZE = 19;

    private float width;
    private float height;
    private VirtualBoard board;
    private Game game;
    private GameNode gameNode;
    private KatachiTheme theme;

    private final RectF bgRect;
    private final Drawable pattern;

    private final Bitmap exportBitmap;
    private final Canvas exportCanvas;

    public BoardDrawer(final Context context) {
        bgRect = new RectF();
        pattern = tilePattern(ContextCompat.getDrawable(context, R.drawable.bg_pattern));
        exportBitmap = Bitmap.createBitmap(3000, 3000, ARGB_8888);
        exportCanvas = new Canvas(exportBitmap);
    }

    public Bitmap getBitmap() {
        return exportBitmap;
    }

    public void setWidth(final float width) {
        this.width = width;
    }

    public void setHeight(final float height) {
        this.height = height;
    }

    public void setBoard(final VirtualBoard board) {
        this.board = board;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(final Game game) {
        this.game = game;
    }

    public GameNode getGameNode() {
        return gameNode;
    }

    public void setGameNode(final GameNode gameNode) {
        this.gameNode = gameNode;
        board.fastForwardTo(gameNode);
    }

    public KatachiTheme getTheme() {
        return theme;
    }

    public void setTheme(final KatachiTheme theme) {
        this.theme = theme;
    }

    public void drawOn(final Canvas canvas) {
        log(DEBUG, TAG, "drawOn");

        // Quick exit
        if (game == null || theme == null || width <= 0 || height <= 0) {
            return;
        }

        try {
            final float marginV = max(0, (height - width) / 2);
            final float marginH = max(0, (width - height) / 2);
            final float bgSize = width - 2 * marginH;

            exportBitmap.setHeight(round(bgSize));
            exportBitmap.setWidth(round(bgSize));
            exportCanvas.setBitmap(exportBitmap);

            // Pattern
            canvas.clipRect(marginH, marginV, marginH + bgSize, marginV + bgSize);
            pattern.draw(canvas);

            // Background
            bgRect.set(0, 0, bgSize, bgSize);
            canvas.translate(marginH, marginV);
            canvas.drawRect(bgRect, theme.backgroundPaint);
            exportCanvas.drawRect(bgRect, theme.backgroundPaint);

            // Lines
            final String sizeString = game.getProperties().get(SIZE_KEY);
            final int size = isEmpty(sizeString) ? DEFAULT_BOARD_SIZE : parseInt(sizeString);
            final int squareSize = round(bgSize / (size + theme.paddingRatio));
            final float padding = (bgSize - (squareSize * (size - 1))) / 2;

            final Paint linePaint = theme.linePaint;
            final float strokeWidth = linePaint.getStrokeWidth();
            for (int i = 0; i < size; i++) {
                final float linePadding = padding * theme.lineOverflowRatio - strokeWidth / 2;
                final float offset = padding + i * squareSize;
                final float start = linePadding;
                final float end = bgSize - linePadding;
                canvas.drawLine(offset, start, offset, end, linePaint);
                canvas.drawLine(start, offset, end, offset, linePaint);
                exportCanvas.drawLine(offset, start, offset, end, linePaint);
                exportCanvas.drawLine(start, offset, end, offset, linePaint);
            }

            // Stones
            final float stoneRadius = squareSize / 2.1f;
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    final StoneState state = board.getCoord(x, y).getColor();
                    if (state == EMPTY) {
                        continue;
                    }

                    final int[] currentMoveCoords = gameNode.getCoords();
                    final float cx = padding + x * squareSize;
                    final float cy = padding + y * squareSize;
                    final boolean isCurrent = currentMoveCoords[0] == x && currentMoveCoords[1] == y;

                    canvas.drawCircle(cx, cy, stoneRadius, theme.getStonePaint(state, isCurrent));
                    canvas.drawCircle(cx, cy, stoneRadius, theme.getStoneStrokePaint(state, isCurrent));
                    exportCanvas.drawCircle(cx, cy, stoneRadius, theme.getStonePaint(state, isCurrent));
                    exportCanvas.drawCircle(cx, cy, stoneRadius, theme.getStoneStrokePaint(state, isCurrent));
                }
            }
        } catch (final Exception e) {
            logError(TAG, "drawOn", e);
        }
    }
}
