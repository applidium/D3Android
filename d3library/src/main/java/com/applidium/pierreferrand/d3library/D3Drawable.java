package com.applidium.pierreferrand.d3library;

import android.graphics.Canvas;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.action.OnClickAction;
public abstract class D3Drawable {
    private float height;
    private float width;

    @Nullable private OnClickAction onClickAction;

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
}
