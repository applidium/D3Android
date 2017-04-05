package com.applidium.pierreferrand.d3library;

import android.graphics.Canvas;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.action.PinchType;
import com.applidium.pierreferrand.d3library.action.ScrollDirection;

public abstract class D3Drawable {
    private float height;
    private float width;

    @Nullable private OnClickAction onClickAction;
    @Nullable private OnScrollAction onScrollAction;
    @Nullable private OnPinchAction onPinchAction;

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

    public void onScroll(ScrollDirection direction, float dX, float dY) {
        if (onScrollAction != null) {
            onScrollAction.onScroll(direction, dX, dY);
        }
    }

    public D3Drawable onPinchAction(OnPinchAction onPinchAction) {
        this.onPinchAction = onPinchAction;
        return this;
    }

    public void onPinch(PinchType pinchType, float dX, float dY) {
        if (onPinchAction != null) {
            onPinchAction.onPinch(pinchType, dX, dY);
        }
    }


}
