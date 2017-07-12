package com.applidium.pierreferrand.d3library;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.applidium.pierreferrand.d3library.action.PinchType;
import com.applidium.pierreferrand.d3library.action.ScrollDirection;

import java.util.ArrayList;
import java.util.List;

public class D3View extends View {

    private boolean clickTracker;

    public final List<D3Drawable> drawables;

    public D3View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        drawables = new ArrayList<>();
    }

    public void add(D3Drawable drawable) {
        drawables.add(drawable);
    }

    @Override public void draw(Canvas canvas) {
        super.draw(canvas);
        for (D3Drawable drawable : drawables) {
            drawable.setDimensions(getHeight(), getWidth());
            drawable.draw(canvas);
        }
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                return handleMoveAction(event);
            case MotionEvent.ACTION_UP:
                handleUpAction(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
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
            clickTracker = false;
            handlePinchMovement(event);
        } else if (event.getPointerCount() == 1) {
            clickTracker = false;
            handleScrollMovement(event, historySize);
        }
        return false;
    }

    private void handleUpAction(MotionEvent event) {
        if (clickTracker) {
            for (D3Drawable drawable : drawables) {
                drawable.onClick(event.getX(), event.getY());
            }
            invalidate();
        }
    }

    private void handleDownAction(MotionEvent event) {
        clickTracker = event.getPointerCount() == 1;
    }

    private void handlePointerDownAction() {
        clickTracker = false;
    }

    private void handlePinchMovement(MotionEvent event) {
        float[] maxDifferenceAbsolute = new float[event.getPointerCount()];
        float[] differenceX = new float[event.getPointerCount()];
        float[] differenceY = new float[event.getPointerCount()];
        computeDifferences(event, maxDifferenceAbsolute, differenceX, differenceY);
        int indexMovement = findFingerMovedIndex(maxDifferenceAbsolute);
        PinchType pinchType = computePinchType(
            event, differenceX[indexMovement], differenceY[indexMovement], indexMovement
        );
        for (D3Drawable drawable : drawables) {
            drawable.onPinch(pinchType, differenceX[indexMovement], differenceY[indexMovement]);
        }
        invalidate();
    }

    private void computeDifferences(
        MotionEvent event, float[] maxDifferenceAbsolute, float[] differenceX, float[] differenceY
    ) {
        int historySize = event.getHistorySize();
        for (int i = 0; i < maxDifferenceAbsolute.length; i++) {
            differenceX[i] = event.getX(i) - event.getHistoricalX(i, historySize - 1);
            differenceY[i] = event.getY(i) - event.getHistoricalY(i, historySize - 1);
            maxDifferenceAbsolute[i] = Math.max(Math.abs(differenceX[i]), Math.abs(differenceY[i]));
        }
    }

    private int findFingerMovedIndex(float[] maxDifferenceAbsolute) {
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

    private PinchType computePinchType(MotionEvent event, float a, float a1, int indexMovement) {
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

    private void handleScrollMovement(MotionEvent event, int historySize) {
        ScrollDirection direction;
        float diffX = event.getX() - event.getHistoricalX(historySize - 1);
        float diffY = event.getY() - event.getHistoricalY(historySize - 1);
        if (Math.abs(diffX) > Math.abs(diffY)) {
            direction = diffX > 0f ? ScrollDirection.RIGHT : ScrollDirection.LEFT;
        } else {
            direction = diffY > 0f ? ScrollDirection.BOTTOM : ScrollDirection.TOP;
        }
        for (D3Drawable drawable : drawables) {
            drawable.onScroll(direction, diffX, diffY);
        }
        invalidate();
    }
}
