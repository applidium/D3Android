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
    private static final int MINIMUM_TIME_PER_FRAME = 33;

    private int minimumTimePerFrame = MINIMUM_TIME_PER_FRAME;
    private boolean mustRun = true;
    private boolean initialized;
    private boolean isSurfaceCreated;
    private final Object surfaceKey = new Object();

    private final Object key = new Object();

    private boolean needRedraw = true;
    private long lastDraw = System.currentTimeMillis();

    private float[] maxDifferenceAbsolute = new float[2];
    private float[] differenceX = new float[2];
    private float[] differenceY = new float[2];

    /* Those variables are used to track the scroll */
    float scrollCurrentX;
    float scrollCurrentY;
    boolean isScrollInitialized;

    /* Those variables are used to track the scroll */
    float[] pinchPreviousX = new float[2];
    float[] pinchCurrentX = new float[2];
    float[] pinchPreviousY = new float[2];
    float[] pinchCurrentY = new float[2];
    boolean isPinchInitialized;

    /**
     * Allows to make post-run actions be executed by the main thread, so post-run actions
     * can modify the UI.
     */
    @NonNull private final Handler handler;

    private int clickTracker;

    @NonNull private final List<D3Drawable> drawables;
    @NonNull public final List<Runnable> afterDrawActions;


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
                        key.wait(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }

            for (D3Drawable drawable : drawables) {
                drawable.prepareParameters();
            }
            sleepIfNeeded();
            synchronized (surfaceKey) {
                drawOnCorrectCanvas();
            }
        }
    }

    private void drawOnCorrectCanvas() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            if (!isSurfaceCreated) {
                return;
            }
            Canvas c = getHolder().lockCanvas();

            if (c != null) {
                draw(c);
                getHolder().unlockCanvasAndPost(c);
            }
        } else {
            if (!isSurfaceCreated) {
                return;
            }
            Surface surface = getHolder().getSurface();
            Canvas c = surface.lockHardwareCanvas();

            if (c != null) {
                draw(c);
                getHolder().getSurface().unlockCanvasAndPost(c);
            }
        }
    }

    private void sleepIfNeeded() {
        long endingTime = lastDraw + minimumTimePerFrame;
        long remainingTime = endingTime - System.currentTimeMillis();
        while (remainingTime > 0) {
            try {
                Thread.sleep(remainingTime);
            } catch (InterruptedException ignore) {
            }
            remainingTime = endingTime - System.currentTimeMillis();
        }
        lastDraw = System.currentTimeMillis();
    }

    /**
     * This inner method should not be called.
     */
    @Override public void draw(Canvas canvas) {
        super.draw(canvas);
        needRedraw = false;
        canvas.drawRGB(255, 255, 255);
        for (D3Drawable drawable : drawables) {
            drawable.preDraw(canvas);
            drawable.draw(canvas);
            drawable.postDraw(canvas);
            needRedraw = needRedraw || drawable.calculationNeeded() > 0;
        }
        for (Runnable action : afterDrawActions) {
            handler.post(action);
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
        if (event.getPointerCount() == 1) {
            handleScrollMovement(event);
            return true;
        } else if (event.getPointerCount() == 2) {
            clickTracker = 0;
            handlePinchMovement(event);
            return true;
        }
        return false;
    }

    private void handleScrollMovement(MotionEvent event) {
        clickTracker = Math.max(clickTracker - 1, 0);
        float previousX = scrollCurrentX;
        scrollCurrentX = event.getX();
        float previousY = scrollCurrentY;
        scrollCurrentY = event.getY();
        if (!isScrollInitialized) {
            isScrollInitialized = true;
            return;
        }

        ScrollDirection direction;
        float diffX = scrollCurrentX - previousX;
        float diffY = scrollCurrentY - previousY;
        if (Math.abs(diffX) > Math.abs(diffY)) {
            direction = diffX > 0F ? ScrollDirection.RIGHT : ScrollDirection.LEFT;
        } else {
            direction = diffY > 0F ? ScrollDirection.BOTTOM : ScrollDirection.TOP;
        }
        for (D3Drawable drawable : drawables) {
            drawable.onScroll(direction, previousX, previousY, diffX, diffY);
        }
    }

    private void handlePinchMovement(@NonNull MotionEvent event) {
        int histLength = event.getHistorySize();
        if (histLength == 0) {
            return;
        }
        pinchPreviousX[0] = pinchCurrentX[0];
        pinchPreviousX[1] = pinchCurrentX[1];
        pinchPreviousY[0] = pinchCurrentY[0];
        pinchPreviousY[1] = pinchCurrentY[1];

        pinchCurrentX[0] = event.getX(0);
        pinchCurrentX[1] = event.getX(1);
        pinchCurrentY[0] = event.getY(0);
        pinchCurrentY[1] = event.getY(1);

        if (!isPinchInitialized) {
            isPinchInitialized = true;
            return;
        }

        updateDifferences();
        int indexMovement = findFingerMovedIndex();
        PinchType pinchType = computePinchType(indexMovement);
        for (D3Drawable drawable : drawables) {
            drawable.onPinch(
                pinchType,
                pinchPreviousX[1 - indexMovement],
                pinchPreviousY[1 - indexMovement],
                pinchPreviousX[indexMovement],
                pinchPreviousY[indexMovement],
                differenceX[indexMovement],
                differenceY[indexMovement]
            );
        }
    }

    private void updateDifferences() {
        for (int i = 0; i < 2; i++) {
            differenceX[i] = pinchCurrentX[i] - pinchPreviousX[i];
            differenceY[i] = pinchCurrentY[i] - pinchPreviousY[i];
            maxDifferenceAbsolute[i] = Math.max(Math.abs(differenceX[i]), Math.abs(differenceY[i]));
        }
    }

    private int findFingerMovedIndex() {
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

    private PinchType computePinchType(int indexMovement) {
        float x = differenceX[indexMovement];
        float y = differenceY[indexMovement];
        if (Math.abs(x) > Math.abs(y)) {
            if (pinchCurrentX[indexMovement] > pinchCurrentX[1 - indexMovement]) {
                return x > 0 ?
                    PinchType.HORIZONTAL_INCREASE : PinchType.HORIZONTAL_DECREASE;
            } else {
                return x > 0 ?
                    PinchType.HORIZONTAL_DECREASE : PinchType.HORIZONTAL_INCREASE;
            }
        } else {
            if (pinchCurrentY[indexMovement] > pinchCurrentY[1 - indexMovement]) {
                return y > 0 ?
                    PinchType.VERTICAL_INCREASE : PinchType.VERTICAL_DECREASE;
            } else {
                return y > 0 ?
                    PinchType.VERTICAL_DECREASE : PinchType.VERTICAL_INCREASE;
            }
        }
    }

    private void handleUpAction(MotionEvent event) {
        if (clickTracker > 0) {
            for (D3Drawable drawable : drawables) {
                drawable.onClick(event.getX(), event.getY());
            }
        }
    }

    private void handleDownAction(MotionEvent event) {
        isScrollInitialized = false;
        isPinchInitialized = false;
        clickTracker = event.getPointerCount() == 1 ? DEFAULT_CLICK_ACTIONS_NUMBER : 0;
    }

    private void handlePointerDownAction() {
        isScrollInitialized = false;
        isPinchInitialized = false;
        clickTracker = 0;
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

    public void setMinimumTimePerFrame(int minimumTimePerFrame) {
        this.minimumTimePerFrame = minimumTimePerFrame;
    }
}
