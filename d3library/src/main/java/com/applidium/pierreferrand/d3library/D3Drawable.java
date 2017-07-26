package com.applidium.pierreferrand.d3library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.action.PinchType;
import com.applidium.pierreferrand.d3library.action.ScrollDirection;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;

import java.util.List;

public abstract class D3Drawable {
    private static final float DEFAULT_STROKE_WIDTH = 5.0F;
    private static final int MAX_REDRAW_NEEDED = 4;

    private final Object key = new Object();

    @Nullable private D3FloatFunction leftLimit;
    @Nullable private D3FloatFunction rightLimit;
    @Nullable private D3FloatFunction topLimit;
    @Nullable private D3FloatFunction bottomLimit;

    private float height;
    private float width;
    private int canvasState;

    protected boolean lazyRecomputing = true;
    private int calculationNeeded = 1;

    @Nullable protected List<D3Drawable> children;

    @NonNull protected Paint paint;

    @Nullable private OnClickAction onClickAction = new OnClickAction() {
        @Override public void onClick(float x, float y) {
            updateNeeded();
        }
    };

    @Nullable private OnScrollAction onScrollAction = new OnScrollAction() {
        @Override public void onScroll(
            ScrollDirection direction,
            float coordinateX,
            float coordinateY,
            float dX,
            float dY
        ) {
            updateNeeded();
        }
    };

    @Nullable private OnPinchAction onPinchAction = new OnPinchAction() {
        @Override public void onPinch(
            PinchType pinchType,
            float coordinateStaticX,
            float coordinateStaticY,
            float coordinateMobileX,
            float coordinateMobileY,
            float dX,
            float dY
        ) {
            updateNeeded();
        }
    };

    /**
     * This method must be called in the constructor of each D3Drawable in order to setup
     * the Paint
     */
    protected void setupPaint() {
        Paint newPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        newPaint.setColor(Color.rgb(0, 0, 0));
        newPaint.setStyle(Paint.Style.FILL);
        newPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        paint(newPaint);
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
        onDimensionsChange(width, height);
        if (children == null) {
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            children.get(i).setDimensions(height, width);
        }
    }

    protected void onDimensionsChange(float width, float height) {
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
        if (children == null) {
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            children.get(i).prepareParameters();
        }
    }

    /**
     * Defines the rectangle limit in which the Drawable can draw itself.
     */
    public D3Drawable setClipRect(
        @NonNull D3FloatFunction leftLimit,
        @NonNull D3FloatFunction topLimit,
        @NonNull D3FloatFunction rightLimit,
        @NonNull D3FloatFunction bottomLimit
    ) {
        this.leftLimit = leftLimit;
        this.topLimit = topLimit;
        this.rightLimit = rightLimit;
        this.bottomLimit = bottomLimit;
        return this;
    }

    /**
     * Deletes the rectangle limit in which the Drawable can draw itself.
     */
    public D3Drawable deleteClipRect() {
        leftLimit = null;
        rightLimit = null;
        topLimit = null;
        bottomLimit = null;
        return this;
    }

    final void preDraw(@NonNull Canvas canvas) {
        if (leftLimit != null) {
            canvasState = canvas.save();
            canvas.clipRect(
                leftLimit.getFloat(),
                topLimit.getFloat(),
                rightLimit.getFloat(),
                bottomLimit.getFloat()
            );
        }

        if (children == null) {
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            children.get(i).preDraw(canvas);
        }
    }

    final void postDraw(@NonNull Canvas canvas) {
        if (leftLimit != null) {
            canvas.restoreToCount(canvasState);
        }
        synchronized (key) {
            calculationNeeded = lazyRecomputing ? Math.max(calculationNeeded - 1, 0) : 1;
        }

        if (children == null) {
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            children.get(i).postDraw(canvas);
        }
    }

    /**
     * If lazyRecomputing is false, each time the Drawable should draw itself, it
     * will first recompute all data. If it is true, the Drawable will recompute its data only
     * when {@link #updateNeeded()} is called.
     * Setting this property to true can accelerate the drawing process but must be set to false
     * when data can change between two drawing loops.
     * The property is set to true by default.
     */
    public D3Drawable lazyRecomputing(boolean lazyRecomputing) {
        this.lazyRecomputing = lazyRecomputing;
        return this;
    }

    /**
     * Notify the Drawable that he must recompute its data. It is useful only when the Drawable
     * has lazy recomputing enable. See {@link #lazyRecomputing(boolean)} for more information.
     */
    public final void updateNeeded() {
        synchronized (key) {
            calculationNeeded = lazyRecomputing ?
                Math.min(calculationNeeded + 2, MAX_REDRAW_NEEDED) : 2;
        }

        if (children == null) {
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            children.get(i).updateNeeded();
        }
    }

    public final void updateNeeded(int updatesNeeded) {
        synchronized (key) {
            calculationNeeded = lazyRecomputing ?
                Math.min(calculationNeeded + updatesNeeded, MAX_REDRAW_NEEDED) : 1;
        }

        if (children == null) {
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            children.get(i).updateNeeded(updatesNeeded);
        }
    }

    protected final int calculationNeeded() {
        synchronized (key) {
            return calculationNeeded;
        }
    }
}
