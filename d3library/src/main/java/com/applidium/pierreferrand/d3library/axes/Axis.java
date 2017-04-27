package com.applidium.pierreferrand.d3library.axes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;

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

    private LegendProperties legendProperties;

    public Axis(AxisOrientation orientation, Scale scale) {
        this.orientation = orientation;
        this.scale = scale;
        this.legendProperties = new LegendProperties();
        setUpPaints();
    }

    private void setUpPaints() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(new Color().rgb(0, 0, 0));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        setupTextPaint();
    }

    private void setupTextPaint() {
        textPaint.setColor(legendProperties.color());
        textPaint.setTextSize(legendProperties.textSizeInPixels());
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

    /***
     * @return the legendProperties use for legends
     */
    public LegendProperties legendProperties() {
        return legendProperties;
    }

    /***
     * Set the legendProperties to use. If null, use the default one.
     */
    public Axis legendProperties(@Nullable LegendProperties legendProperties) {
        if (legendProperties == null) {
            this.legendProperties = new LegendProperties();
        } else {
            this.legendProperties = legendProperties;
        }
        setupTextPaint();
        return this;
    }

    /***
     * @return the legend's text size
     */
    public float textSizeInPixels() {
        return legendProperties.textSizeInPixels();
    }

    /***
     * Set the legend's text size
     */
    public Axis textSizeInPixels(float textSizeInPixels) {
        legendProperties.textSizeInPixels(textSizeInPixels);
        setupTextPaint();
        return this;
    }

    /***
     * @return the legend's color
     */
    public @ColorInt int legendColor() {
        return legendProperties.color();
    }

    /***
     * Set the legend's color
     */
    public Axis legendColor(@ColorInt int color) {
        legendProperties.color(color);
        setupTextPaint();
        return this;
    }

    /***
     * @return the legend's vertical alignment
     */
    public VerticalAlignment legendVerticalAlignment() {
        return legendProperties.verticalAlignement();
    }

    /***
     * set the legend's vertical alignment for a vertical Axis
     */
    public Axis legendVerticalAlignment(VerticalAlignment verticalAlignment) {
        legendProperties.verticalAlignement(verticalAlignment);
        return this;
    }

    /***
     * @return the legend's horizontal alignment
     */
    public HorizontalAlignment legendHorizontalAlignment() {
        return legendProperties.horizontalAlignement();
    }

    /***
     * set the legend's horizontal alignment for a horizontal Axis
     */
    public Axis legendHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        legendProperties.horizontalAlignement(horizontalAlignment);
        return this;
    }

    /***
     * @return the legend's horizontal offset
     */
    public float legendOffsetX() {
        return legendProperties.offsetX();
    }

    /***
     * set the legend's horizontal offset
     */
    public Axis legendOffsetX(float offsetX) {
        legendProperties.offsetX(offsetX);
        return this;
    }

    /***
     * @return the legend's vertical offset
     */
    public float legendOffsetY() {
        return legendProperties.offsetY();
    }

    /***
     * set the legend's vertical offset
     */
    public Axis legendOffsetY(float offsetY) {
        legendProperties.offsetY(offsetY);
        return this;
    }

    /***
     * set the legend's offsets
     */
    public Axis legendOffset(float offsetX, float offsetY) {
        legendProperties.offsetX(offsetX);
        legendProperties.offsetY(offsetY);
        return this;
    }

    public void draw(Canvas canvas) {
        drawLine(canvas);
        drawTicks(canvas);
        drawTicksLegend(canvas);
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

    private void drawTicks(Canvas canvas) {
        if (orientation == AxisOrientation.TOP || orientation == AxisOrientation.BOTTOM) {
            drawVerticalTicks(canvas);
        } else {
            drawHorizontalTicks(canvas);
        }
    }

    private void drawHorizontalTicks(Canvas canvas) {
        float outerX = offsetX - outerTickSize / 2;
        float innerX = offsetX + innerTickSize / 2;
        for (int i = 0; i < ticksNumber; i++) {
            float coordinateY = offsetY + lastBoundRange() * i / (ticksNumber - 1.0f)
                + firstBoundRange() * (ticksNumber - i - 1.0f) / (ticksNumber - 1.0f);
            canvas.drawLine(outerX, coordinateY, innerX, coordinateY, paint);
        }
    }

    private void drawVerticalTicks(Canvas canvas) {
        float outerY = offsetY + outerTickSize / 2;
        float innerY = offsetY - innerTickSize / 2;
        for (int i = 0; i < ticksNumber; i++) {
            float coordinateX = offsetX + lastBoundRange() * i / (ticksNumber - 1.0f)
                + firstBoundRange() * (ticksNumber - i - 1.0f) / (ticksNumber - 1.0f);
            canvas.drawLine(coordinateX, innerY, coordinateX, outerY, paint);
        }
    }

    private void drawTicksLegend(Canvas canvas) {
        if (orientation == AxisOrientation.TOP || orientation == AxisOrientation.BOTTOM) {
            drawHorizontalLegend(canvas);
        } else {
            drawVerticalLegend(canvas);
        }
    }

    private void drawVerticalLegend(Canvas canvas) {
        float[] usableTicks = this.ticks == null ? scale.ticks(this.ticksNumber) : this.ticks;
        float coordinateX = orientation == AxisOrientation.LEFT ?
            offsetX - innerTickSize : offsetX + innerTickSize;
        coordinateX += legendProperties.offsetX();

        for (int i = 0; i < usableTicks.length; i++) {
            drawSingleVerticalLegend(canvas, usableTicks[i], coordinateX, i);
        }
    }

    private void drawSingleVerticalLegend(Canvas canvas, float tick, float coordinateX, int i) {
        float coordinateY = offsetY + lastBoundRange() * i / (ticksNumber - 1.0f)
            + firstBoundRange() * (ticksNumber - i - 1.0f) / (ticksNumber - 1.0f);
        coordinateY += legendProperties.offsetY();
        coordinateY += alignmentVerticalOffset(Float.toString(tick));
        coordinateX -= orientation == AxisOrientation.LEFT ?
            textPaint.measureText(Float.toString(tick)) : 0;

        canvas.drawText(Float.toString(tick), coordinateX, coordinateY, textPaint);
    }

    private float alignmentVerticalOffset(String legend) {
        float height = getTextHeight(legend);
        switch (legendProperties.verticalAlignement()) {
            case BOTTOM:
                return height;
            case CENTER:
                return height / 2.0f;
            default:
                return 0.0f;
        }
    }

    private float getTextHeight(String legend) {
        Rect bounds = new Rect();
        textPaint.getTextBounds(legend, 0, legend.length(), bounds);
        return (float) bounds.height();
    }

    private void drawHorizontalLegend(Canvas canvas) {
        float[] usableTicks = this.ticks == null ? scale.ticks(this.ticksNumber) : this.ticks;
        float coordinateY = orientation == AxisOrientation.TOP ?
            offsetY - innerTickSize : offsetY + innerTickSize;
        coordinateY += legendProperties.offsetY();

        for (int i = 0; i < usableTicks.length; i++) {
            drawSingleHorizontalLegend(canvas, usableTicks, coordinateY, i);
        }
    }

    private void drawSingleHorizontalLegend(
        Canvas canvas,
        float[] ticks,
        float coordinateY,
        int i
    ) {
        float coordinateX = offsetX + lastBoundRange() * i / (ticksNumber - 1.0f)
            + firstBoundRange() * (ticksNumber - i - 1.0f) / (ticksNumber - 1.0f);
        coordinateX += legendProperties.offsetX();
        coordinateX -= alignmentHorizontalOffset(Float.toString(ticks[i]));
        coordinateY += orientation == AxisOrientation.TOP ? 0 :
            getTextHeight(Float.toString(ticks[i]));

        canvas.drawText(Float.toString(ticks[i]), coordinateX, coordinateY, textPaint);
    }

    private float alignmentHorizontalOffset(String legend) {
        switch (legendProperties.horizontalAlignement()) {
            case LEFT:
                return textPaint.measureText(legend);
            case CENTER:
                return textPaint.measureText(legend) / 2.0f;
            default:
                return 0.0f;
        }
    }
}
