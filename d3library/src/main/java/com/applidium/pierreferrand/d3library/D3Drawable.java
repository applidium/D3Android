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

    protected void setupPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(0, 0, 0));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
    }

    public final Paint paint() {
        return paint;
    }

    public D3Drawable paint(Paint paint) {
        this.paint = paint;
        return this;
    }

    abstract public void draw(Canvas canvas);

    protected void setDimensions(float height, float width) {
        this.height = height;
        this.width = width;
    }

    public float height() {
        return height;
    }

    public float width() {
        return width;
    }

    public D3Drawable onClickAction(OnClickAction onClickAction) {
        this.onClickAction = onClickAction;
        return this;
    }

    public void onClick(float X, float Y) {
        if (onClickAction != null) {
            onClickAction.onClick(X, Y);
        }
    }

    public D3Drawable onScrollAction(OnScrollAction onScrollAction) {
        this.onScrollAction = onScrollAction;
        return this;
    }

    public void onScroll(
        ScrollDirection direction, float coordinateX, float coordinateY, float dX, float dY
    ) {
        if (onScrollAction != null) {
            onScrollAction.onScroll(direction, coordinateX, coordinateY, dX, dY);
        }
    }

    public D3Drawable onPinchAction(OnPinchAction onPinchAction) {
        this.onPinchAction = onPinchAction;
        return this;
    }

    public void onPinch(
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

    public void prepareParameters() {
        /* Nothing to do. Child classes can override this method to launch computation
         * of parameters before drawing. */
    }
}
