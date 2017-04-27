package com.applidium.pierreferrand.d3library;

import android.graphics.Canvas;

public abstract class D3Drawable {
    private float height;
    private float width;

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
}
