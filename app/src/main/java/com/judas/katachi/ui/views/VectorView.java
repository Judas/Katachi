package com.judas.katachi.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

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
import static com.judas.katachi.utils.log.Logger.Level.ERROR;
import static com.judas.katachi.utils.log.Logger.Level.VERBOSE;
import static com.judas.katachi.utils.log.Logger.log;
import static com.judas.katachi.utils.view.TileVectorDrawable.tilePattern;
import static com.toomasr.sgf4j.board.StoneState.EMPTY;
import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.lang.Math.round;

public class VectorView extends View {
    public interface OnDrawnListener {
        void onDrawn();

        void onError(final Exception e);
    }

    private static final String TAG = VectorView.class.getSimpleName();
    private static final String SIZE_KEY = "SZ";

    private Game game;
    private GameNode gameNode;
    private int lastMoveNumber;
    private final VirtualBoard board;

    private KatachiTheme theme;
    private OnDrawnListener listener;

    private final RectF bgRect;
    private final Drawable pattern;

    private Bitmap exportBitmap;
    private Canvas exportCanvas;

    public VectorView(final Context context) {
        this(context, null, 0);
    }

    public VectorView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VectorView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        log(DEBUG, TAG, "VectorView");

        bgRect = new RectF();
        board = new VirtualBoard();
        pattern = tilePattern(ContextCompat.getDrawable(getContext(), R.drawable.bg_pattern));

        exportBitmap = Bitmap.createBitmap(3000, 3000, ARGB_8888);
        exportCanvas = new Canvas(exportBitmap);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(final Game game) {
        log(DEBUG, TAG, "setGame");
        this.game = game;
        initBoard();
    }

    public int getLastMoveNumber() {
        return lastMoveNumber;
    }

    public KatachiTheme getTheme() {
        log(DEBUG, TAG, "getTheme");
        return theme;
    }

    public void setTheme(final KatachiTheme theme) {
        log(DEBUG, TAG, "setTheme");
        this.theme = theme;
        initBoard();
    }

    private void initBoard() {
        log(DEBUG, TAG, "initBoard");

        if (game == null || theme == null) {
            return;
        }

        // Go to last node
        gameNode = game.getLastMove();
        while (isEmpty(gameNode.getMoveString())) {
            gameNode = gameNode.getParentNode();
        }

        // Validate move number
        lastMoveNumber = gameNode.getMoveNo();
        if (theme.currentMoveNumber > lastMoveNumber) {
            theme.currentMoveNumber = -1;
        }

        // Go back if necessary
        if (theme.currentMoveNumber != -1) {
            while (gameNode.getMoveNo() != theme.currentMoveNumber) {
                gameNode = gameNode.getParentNode();
            }
        }

        board.fastForwardTo(gameNode);
    }

    public void setOnDrawnListener(final OnDrawnListener listener) {
        log(DEBUG, TAG, "setOnDrawnListener");
        this.listener = listener;
    }

    private void notifyDrawn() {
        log(VERBOSE, TAG, "notifyDrawn");

        if (listener != null) {
            listener.onDrawn();
        }
    }

    private void notifyError(final Exception e) {
        log(ERROR, TAG, "notifyError " + e.getMessage());

        if (listener != null) {
            listener.onError(e);
        }
    }

    public Bitmap getBitmap() {
        return exportBitmap;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        log(VERBOSE, TAG, "onDraw");

        // Quick exit
        if (game == null || theme == null) {
            return;
        }

        final float width = (float) getWidth();
        final float height = (float) getHeight();

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
            final int size = parseInt(game.getProperties().get(SIZE_KEY));
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

            notifyDrawn();
        } catch (final Exception e) {
            notifyError(e);
        }
    }
}
