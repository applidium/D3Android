package com.applidium.pierreferrand.d3library.axes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;

import com.applidium.pierreferrand.d3library.scale.Scale;

public class Axis {

    private static final float DEFAULT_TICK_SIZE = 25.0f;
    private static final float DEFAULT_STROKE_WIDTH = 5.0f;
    private static final float DEFAULT_OFFSET = 25.0f;
    private static final int DEFAULT_TICK_NUMBER = 5;

    private float offsetX = DEFAULT_OFFSET;
    private float offsetY = DEFAULT_OFFSET;
    private float innerTickSize = DEFAULT_TICK_SIZE;
    private float outerTickSize = DEFAULT_TICK_SIZE;
    private int ticksNumber = DEFAULT_TICK_NUMBER;

    private final AxisOrientation orientation;
    private Scale scale;

    private float[] ticks;

    private Paint paint;
    private Paint textPaint;

    public Axis(AxisOrientation orientation, Scale scale) {
        this.orientation = orientation;
        this.scale = scale;
        setUpPaints();
    }

    private void setUpPaints() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(new Color().rgb(0, 0, 0));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
    }

    /***
     * Set the color of the stroke for the Axis and the ticks of this axis
     */
    public Axis axisColor(@ColorInt int color) {
        paint.setColor(color);
        return this;
    }

    /***
     * Set the width of the stroke for the Axis and the ticks
     */
    public Axis axisWidth(float width) {
        paint.setStrokeWidth(width);
        return this;
    }

    /***
     * Set the coordinates of the origin
     */
    public Axis translate(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        return this;
    }

    /***
     * @return the number of ticks for the Axis
     */
    public int ticks() {
        return ticksNumber;
    }

    /***
     * Set the number of ticks for the Axis
     */
    public Axis ticks(int ticksNumber) {
        ticks = null;
        this.ticksNumber = ticksNumber;
        return this;
    }

    /***
     * Reset ticks : default Scale ticks will be used
     */
    public Axis tickValues() {
        ticks = null;
        ticksNumber = DEFAULT_TICK_NUMBER;
        return this;
    }

    /***
     * Set the the ticks to use rather than default Scale ticks
     */
    public Axis tickValues(float[] ticks) {
        if (ticks == null || ticks.length < 2) {
            throw new IllegalStateException("TickValue must have at least 2 values");
        }
        ticksNumber = ticks.length;
        this.ticks = ticks.clone();
        return this;
    }

    /***
     * @return the scale of the Axis
     */
    public Scale scale() {
        return this.scale;
    }

    /***
     * Set the scale of the Axis
     */
    public Axis scale(Scale scale) {
        this.scale = scale;
        return this;
    }

    /***
     * @return the inner ticks' size
     */
    public float tickSize() {
        return tickSizeInner();
    }

    /***
     * Set the inner and outer ticks' size
     */
    public Axis tickSize(float size) {
        tickSizeInner(size);
        tickSizeOuter(size);
        return this;
    }

    /***
     * @return the inner ticks' size
     */
    public float tickSizeInner() {
        return innerTickSize;
    }

    /***
     * Set the inner ticks' size
     */
    public Axis tickSizeInner(float size) {
        innerTickSize = size;
        return this;
    }

    /***
     * @return the outer ticks' size
     */
    public float tickSizeOuter() {
        return outerTickSize;
    }

    /***
     * Set the outer ticks' size
     */
    public Axis tickSizeOuter(float size) {
        outerTickSize = size;
        return this;
    }

    private float firstBoundRange() {
        return scale.range()[0];
    }

    private float lastBoundRange() {
        float[] range = scale.range();
        return range[range.length - 1];
    }

    public void draw(Canvas canvas) {
        drawLine(canvas);
    }

    private void drawLine(Canvas canvas) {
        float startX, startY, endX, endY;
        if (orientation == AxisOrientation.TOP || orientation == AxisOrientation.BOTTOM) {
            startX = offsetX + firstBoundRange();
            startY = offsetY;
            endX = offsetX + lastBoundRange();
            endY = offsetY;
        } else {
            startX = offsetX;
            startY = offsetY + firstBoundRange();
            endX = offsetX;
            endY = offsetY + lastBoundRange();
        }

        canvas.drawLine(startX, startY, endX, endY, paint);
    }
}
