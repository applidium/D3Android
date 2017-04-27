package com.applidium.pierreferrand.d3library.axes;

import android.graphics.Color;
import android.support.annotation.ColorInt;

public class LegendProperties {

    private final static float DEFAULT_TEXT_SIZE = 15.0f;

    private VerticalAlignment verticalAlignment;
    private HorizontalAlignment horizontalAlignment;

    private float offsetX = 0.0f;
    private float offsetY = 0.0f;

    private float textSizeInPixels = DEFAULT_TEXT_SIZE;

    @ColorInt private int color;

    public LegendProperties() {
        this(VerticalAlignment.TOP, HorizontalAlignment.RIGHT);
    }

    public LegendProperties(VerticalAlignment verticalAlignment) {
        this(verticalAlignment, HorizontalAlignment.RIGHT);
    }

    public LegendProperties(HorizontalAlignment horizontalAlignment) {
        this(VerticalAlignment.TOP, horizontalAlignment);
    }

    public LegendProperties(
        VerticalAlignment verticalAlignment, HorizontalAlignment horizontalAlignment
    ) {
        this.verticalAlignment = verticalAlignment;
        this.horizontalAlignment = horizontalAlignment;
        color = new Color().rgb(0, 0, 0);
    }

    public VerticalAlignment verticalAlignement() {
        return verticalAlignment;
    }

    public void verticalAlignement(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public HorizontalAlignment horizontalAlignement() {
        return horizontalAlignment;
    }

    public void horizontalAlignement(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public float offsetX() {
        return offsetX;
    }

    public void offsetX(float paddingX) {
        this.offsetX = paddingX;
    }

    public float offsetY() {
        return offsetY;
    }

    public void offsetY(float paddingY) {
        this.offsetY = paddingY;
    }

    public float textSizeInPixels() {
        return textSizeInPixels;
    }

    public void textSizeInPixels(float textSizeInPixels) {
        this.textSizeInPixels = textSizeInPixels;
    }

    public int color() {
        return color;
    }

    public void color(@ColorInt int color) {
        this.color = color;
    }
}
