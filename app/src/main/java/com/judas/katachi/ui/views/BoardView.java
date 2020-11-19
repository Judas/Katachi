package com.judas.katachi.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.judas.katachi.core.theme.KatachiTheme;
import com.judas.katachi.utils.view.BoardDrawer;
import com.toomasr.sgf4j.board.VirtualBoard;
import com.toomasr.sgf4j.parser.Game;
import com.toomasr.sgf4j.parser.GameNode;

import static android.text.TextUtils.isEmpty;
import static com.judas.katachi.utils.log.Logger.Level.DEBUG;
import static com.judas.katachi.utils.log.Logger.Level.VERBOSE;
import static com.judas.katachi.utils.log.Logger.log;

public class BoardView extends View {
    private static final String TAG = BoardView.class.getSimpleName();

    private int moveNumber = -1;
    private int lastMoveNumber = -1;

    private final BoardDrawer drawer;

    public BoardView(final Context context) {
        this(context, null, 0);
    }

    public BoardView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoardView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        log(DEBUG, TAG, "BoardView");

        drawer = new BoardDrawer(context);
        drawer.setBoard(new VirtualBoard());
    }

    public Game getGame() {
        log(DEBUG, TAG, "getGame");
        return drawer.getGame();
    }

    public void setGame(final Game game) {
        log(DEBUG, TAG, "setGame");
        drawer.setGame(game);
        initBoard();
    }

    public GameNode getGameNode() {
        log(DEBUG, TAG, "getGameNode");
        return drawer.getGameNode();
    }

    public void setChildGameNode(final GameNode node) {
        log(DEBUG, TAG, "setGameNode");
        drawer.setGameNode(node);
    }

    public void setMoveNumber(final int moveNumber) {
        log(DEBUG, TAG, "setMoveNumber " + moveNumber);
        this.moveNumber = moveNumber;
        initBoard();
    }

    public int getLastMoveNumber() {
        log(DEBUG, TAG, "getLastMoveNumber");
        return lastMoveNumber;
    }

    public int getMoveNumber() {
        log(DEBUG, TAG, "getMoveNumber");
        return moveNumber;
    }

    public KatachiTheme getTheme() {
        log(DEBUG, TAG, "getTheme");
        return drawer.getTheme();
    }

    public void setTheme(final KatachiTheme theme) {
        log(DEBUG, TAG, "setTheme");
        drawer.setTheme(theme);
        initBoard();
    }

    private void initBoard() {
        log(DEBUG, TAG, "initBoard");

        if (drawer.getGame() == null || drawer.getTheme() == null) {
            return;
        }

        // Go to last node
        GameNode gameNode = drawer.getGame().getLastMove();
        while (isEmpty(gameNode.getMoveString())) {
            gameNode = gameNode.getParentNode();
        }

        // Validate move number
        lastMoveNumber = gameNode.getMoveNo();
        if (moveNumber > lastMoveNumber || moveNumber < 0) {
            moveNumber = lastMoveNumber;
        }

        // Go back if necessary
        if (moveNumber != lastMoveNumber) {
            while (gameNode.getParentNode() != null && gameNode.getMoveNo() != moveNumber) {
                gameNode = gameNode.getParentNode();
            }
        }

        drawer.setGameNode(gameNode);
    }

    public Bitmap getBitmap() {
        return drawer.getBitmap();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        log(VERBOSE, TAG, "onDraw");

        drawer.setWidth(getWidth());
        drawer.setHeight(getHeight());
        drawer.drawOn(canvas);
    }
}
