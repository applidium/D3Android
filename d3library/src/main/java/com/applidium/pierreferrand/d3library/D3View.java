package com.applidium.pierreferrand.d3library;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.applidium.pierreferrand.d3library.action.Action;
import com.applidium.pierreferrand.d3library.action.PinchType;
import com.applidium.pierreferrand.d3library.action.ScrollDirection;
import com.applidium.pierreferrand.d3library.threading.ThreadPool;

import java.util.ArrayList;
import java.util.List;

public class D3View extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    /**
     * Allows to have a proper click action, and to disable the transformation of a click action
     * into a scroll action when the user moves a little his finger.
     */
    private static final int DEFAULT_CLICK_ACTIONS_NUMBER = 3;
    private boolean mustRun = true;
    private boolean initialized;
    private boolean isSurfaceCreated;
    private final Object surfaceKey = new Object();

    private Object key = new Object();

    private boolean needRedraw = true;

    /**
     * Allows to make post-run actions be executed by the main thread, so post-run actions
     * can modify the UI.
     */
    @NonNull private final Handler handler;

    private int clickTracker;

    @NonNull private final List<D3Drawable> drawables;
    @NonNull public final List<Action> afterDrawActions;


    public D3View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        drawables = new ArrayList<>();
        afterDrawActions = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());
        initialized = false;
        getHolder().addCallback(this);
    }

    private void launchDisplay() {
        ThreadPool.execute(this);
    }

    /**
     * Adds a Drawable to the list of displayed Drawables.
     */
    public void add(@NonNull D3Drawable drawable) {
        drawables.add(drawable);
    }

    /**
     * Removes a Drawable to the list of displayed Drawables.
     */
    public void remove(@NonNull D3Drawable drawable) {
        drawables.remove(drawable);
    }

    /**
     * Clears the list of displayed Drawables.
     */
    public void clearDrawables() {
        drawables.clear();
    }

    /**
     * Resumes the display of Drawables.
     */
    public void onResume() {
        mustRun = true;
        if (initialized) {
            launchDisplay();
        }
    }

    /**
     * Stops the display of Drawables.
     */
    public void onPause() {
        mustRun = false;
    }

    /**
     * This inner method should not be called.
     */
    @Override public void run() {
        while (mustRun) {
            if (!needRedraw) {
                try {
                    synchronized (key) {
                        key.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
                return;
            }
            synchronized (surfaceKey) {
                if (!isSurfaceCreated) {
                    continue;
                }
                Surface surface = getHolder().getSurface();
                Canvas c = surface.lockHardwareCanvas();

                if (c != null) {
                    draw(c);
                    getHolder().getSurface().unlockCanvasAndPost(c);
                }
            }
        }
    }

    /**
     * This inner method should not be called.
     */
    @Override public void draw(Canvas canvas) {
        super.draw(canvas);
        needRedraw = false;
        for (D3Drawable drawable : drawables) {
            drawable.prepareParameters();
        }
        canvas.drawRGB(255, 255, 255);
        for (D3Drawable drawable : drawables) {
            drawable.preDraw(canvas);
            drawable.draw(canvas);
            drawable.postDraw(canvas);
            needRedraw = needRedraw || drawable.calculationNeeded() > 0;
        }
        for (final Action action : afterDrawActions) {
            handler.post(new Runnable() {
                @Override public void run() {
                    action.execute();
                }
            });
        }
    }

    /**
     * This inner method should not be called
     */
    @Override public boolean onTouchEvent(MotionEvent event) {
        needRedraw = true;
        synchronized (key) {
            key.notifyAll();
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                return handleMoveAction(event);
            case MotionEvent.ACTION_UP:
                handleUpAction(event);
                break;
            case MotionEvent.ACTION_DOWN:
                handleDownAction(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                handlePointerDownAction();
                break;
            default:
                /* Nothing to do */
        }
        return true;
    }

    private boolean handleMoveAction(MotionEvent event) {
        int historySize = event.getHistorySize();
        if (historySize == 0) {
            return true;
        }
        if (event.getPointerCount() == 2) {
            clickTracker = 0;
            handlePinchMovement(event);
        } else if (event.getPointerCount() == 1) {
            clickTracker = Math.max(clickTracker - 1, 0);
            handleScrollMovement(event, historySize);
        }
        return false;
    }

    private void handleUpAction(MotionEvent event) {
        if (clickTracker > 0) {
            for (D3Drawable drawable : drawables) {
                drawable.onClick(event.getX(), event.getY());
            }
        }
    }

    private void handleDownAction(MotionEvent event) {
        clickTracker = event.getPointerCount() == 1 ? DEFAULT_CLICK_ACTIONS_NUMBER : 0;
    }

    private void handlePointerDownAction() {
        clickTracker = 0;
    }

    private void handlePinchMovement(@NonNull MotionEvent event) {
        float[] maxDifferenceAbsolute = new float[event.getPointerCount()];
        float[] differenceX = new float[event.getPointerCount()];
        float[] differenceY = new float[event.getPointerCount()];

        int histLength = event.getHistorySize();
        computeDifferences(event, maxDifferenceAbsolute, differenceX, differenceY);
        int indexMovement = findFingerMovedIndex(maxDifferenceAbsolute);
        PinchType pinchType = computePinchType(
            event, differenceX[indexMovement], differenceY[indexMovement], indexMovement
        );
        for (D3Drawable drawable : drawables) {
            drawable.onPinch(
                pinchType,
                event.getHistoricalX(1 - indexMovement, histLength - 1),
                event.getHistoricalY(1 - indexMovement, histLength - 1),
                event.getHistoricalX(indexMovement, histLength - 1),
                event.getHistoricalY(indexMovement, histLength - 1),
                differenceX[indexMovement],
                differenceY[indexMovement]
            );
        }
    }

    private void computeDifferences(
        @NonNull MotionEvent event,
        @NonNull float[] maxDifferenceAbsolute,
        @NonNull float[] differenceX,
        @NonNull float[] differenceY
    ) {
        int historySize = event.getHistorySize();
        for (int i = 0; i < maxDifferenceAbsolute.length; i++) {
            differenceX[i] = event.getX(i) - event.getHistoricalX(i, historySize - 1);
            differenceY[i] = event.getY(i) - event.getHistoricalY(i, historySize - 1);
            maxDifferenceAbsolute[i] = Math.max(Math.abs(differenceX[i]), Math.abs(differenceY[i]));
        }
    }

    private int findFingerMovedIndex(@NonNull float[] maxDifferenceAbsolute) {
        int result = 0;
        float diffMovementAbsolute = maxDifferenceAbsolute[0];
        for (int i = 1; i < maxDifferenceAbsolute.length; i++) {
            if (diffMovementAbsolute < maxDifferenceAbsolute[i]) {
                result = i;
                diffMovementAbsolute = maxDifferenceAbsolute[i];
            }
        }
        return result;
    }

    private PinchType computePinchType(
        @NonNull MotionEvent event, float a, float a1, int indexMovement
    ) {
        if (Math.abs(a) > Math.abs(a1)) {
            if (event.getX(indexMovement) > event.getX(1 - indexMovement)) {
                return a > 0 ?
                    PinchType.HORIZONTAL_INCREASE : PinchType.HORIZONTAL_DECREASE;
            } else {
                return a > 0 ?
                    PinchType.HORIZONTAL_DECREASE : PinchType.HORIZONTAL_INCREASE;
            }
        } else {
            if (event.getY(indexMovement) > event.getY(1 - indexMovement)) {
                return a1 > 0 ?
                    PinchType.VERTICAL_INCREASE : PinchType.VERTICAL_DECREASE;
            } else {
                return a1 > 0 ?
                    PinchType.VERTICAL_DECREASE : PinchType.VERTICAL_INCREASE;
            }
        }
    }

    private void handleScrollMovement(@NonNull MotionEvent event, int historySize) {
        ScrollDirection direction;
        float previousX = event.getHistoricalX(historySize - 1);
        float previousY = event.getHistoricalY(historySize - 1);
        float diffX = event.getX() - previousX;
        float diffY = event.getY() - previousY;
        if (Math.abs(diffX) > Math.abs(diffY)) {
            direction = diffX > 0F ? ScrollDirection.RIGHT : ScrollDirection.LEFT;
        } else {
            direction = diffY > 0F ? ScrollDirection.BOTTOM : ScrollDirection.TOP;
        }
        for (D3Drawable drawable : drawables) {
            drawable.onScroll(direction, previousX, previousY, diffX, diffY);
        }
    }

    @Override public void surfaceCreated(SurfaceHolder holder) {
        launchDisplay();
        initialized = true;
        synchronized (surfaceKey) {
            isSurfaceCreated = true;
        }
    }

    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        for (D3Drawable drawable : drawables) {
            drawable.setDimensions(getHeight(), getWidth());
        }
    }

    @Override public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (surfaceKey) {
            isSurfaceCreated = false;
        }
        onPause();
    }
}
