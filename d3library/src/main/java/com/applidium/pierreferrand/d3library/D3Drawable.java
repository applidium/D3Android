package com.applidium.pierreferrand.d3library;

import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.action.PinchType;
import com.applidium.pierreferrand.d3library.action.ScrollDirection;

public abstract class D3Drawable {
    private static final float DEFAULT_STROKE_WIDTH = 5.0F;

    private float height;
    private float width;

    @Nullable private OnClickAction onClickAction;
    @Nullable private OnScrollAction onScrollAction;
    @Nullable private OnPinchAction onPinchAction;
    @NonNull protected Paint paint;

    /**
     * This method must be called in the constructor of each D3Drawable in order to setup
     * the Paint
     */
    protected void setupPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(0, 0, 0));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
    }

    /**
     * Returns the paint used for drawing
     */
    @NonNull public final Paint paint() {
        return paint;
    }

    /**
     * Sets the paint used for drawing
     */
    public D3Drawable paint(@NonNull Paint paint) {
        this.paint = paint;
        return this;
    }

    /**
     * This inner method must not be called.
     */
    public abstract void draw(@NonNull Canvas canvas);

    final void setDimensions(float height, float width) {
        this.height = height;
        this.width = width;
    }

    /**
     * Returns the height available for the Drawable. It is correctly set just before the
     * drawing.
     */
    public float height() {
        return height;
    }

    /**
     * Returns the width available for the Drawable. It is correctly set just before the
     * drawing.
     */
    public float width() {
        return width;
    }

    /**
     * Sets the action to execute when a click occurs.
     */
    public D3Drawable onClickAction(@Nullable OnClickAction onClickAction) {
        this.onClickAction = onClickAction;
        return this;
    }

    final void onClick(float x, float y) {
        if (onClickAction != null) {
            onClickAction.onClick(x, y);
        }
    }

    /**
     * Sets the action to execute when a scroll occurs.
     */
    public D3Drawable onScrollAction(@Nullable OnScrollAction onScrollAction) {
        this.onScrollAction = onScrollAction;
        return this;
    }

    final void onScroll(
        ScrollDirection direction, float coordinateX, float coordinateY, float dX, float dY
    ) {
        if (onScrollAction != null) {
            onScrollAction.onScroll(direction, coordinateX, coordinateY, dX, dY);
        }
    }

    /**
     * Sets the action to execute when a pinch occurs.
     */
    public D3Drawable onPinchAction(@Nullable OnPinchAction onPinchAction) {
        this.onPinchAction = onPinchAction;
        return this;
    }

    final void onPinch(
        PinchType pinchType, float coordinateStaticX, float coordinateStaticY,
        float coordinateMobileX, float coordinateMobileY, float dX, float dY
    ) {
        if (onPinchAction != null) {
            onPinchAction.onPinch(
                pinchType, coordinateStaticX, coordinateStaticY, coordinateMobileX,
                coordinateMobileY, dX, dY
            );
        }
    }

    /**
     * This inner method must not be called.
     */
    public void prepareParameters() {
        /* Nothing to do. Child classes can override this method to launch computation
         * of parameters before drawing. */
    }
}
