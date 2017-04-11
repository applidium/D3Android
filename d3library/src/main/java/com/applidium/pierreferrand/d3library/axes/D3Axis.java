package com.applidium.pierreferrand.d3library.axes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.action.PinchType;
import com.applidium.pierreferrand.d3library.action.ScrollDirection;
import com.applidium.pierreferrand.d3library.helper.ArrayConverterHelper;
import com.applidium.pierreferrand.d3library.helper.TextHelper;
import com.applidium.pierreferrand.d3library.scale.D3Converter;
import com.applidium.pierreferrand.d3library.scale.D3Scale;

public class D3Axis<T> extends D3Drawable {

    private static final float DEFAULT_TICK_SIZE = 25.0f;
    private static final float DEFAULT_STROKE_WIDTH = 5.0f;
    private static final float DEFAULT_OFFSET = 0.0f;
    private static final int DEFAULT_TICK_NUMBER = 5;
    private static final float BEGINNING_PROPORTION = 0.05f;
    private static final float END_PROPORTION = 0.95f;
    private static final float PINCH_MIN_SPACING = 100f;

    private D3FloatFunction offsetX;
    private D3FloatFunction offsetY;
    private float innerTickSize = DEFAULT_TICK_SIZE;
    private float outerTickSize = DEFAULT_TICK_SIZE;
    private int ticksNumber = DEFAULT_TICK_NUMBER;

    private final AxisOrientation orientation;
    private D3Scale<T> scale;

    private String[] ticks;

    private Paint paint;
    private Paint textPaint;

    private LegendProperties legendProperties;

    public D3Axis(AxisOrientation orientation) {
        this.orientation = orientation;
        scale = new D3Scale<>();
        if (orientation == AxisOrientation.TOP || orientation == AxisOrientation.BOTTOM) {
            scale.range(new D3RangeFunction<Float>() {
                @Override public Float[] getRange() {
                    return new Float[]{BEGINNING_PROPORTION * width(), END_PROPORTION * width()};
                }
            });
        } else {
            scale.range(new D3RangeFunction<Float>() {
                @Override public Float[] getRange() {
                    return new Float[]{END_PROPORTION * height(), BEGINNING_PROPORTION * height()};
                }
            });
        }

        setupProperties();
    }

    private void setupProperties() {
        translate(DEFAULT_OFFSET, DEFAULT_OFFSET);
        this.legendProperties = new LegendProperties();
        setUpPaints();
        setupDefaultActions();
    }

    private void setupDefaultActions() {
        if (orientation == AxisOrientation.RIGHT || orientation == AxisOrientation.LEFT) {
            onScrollAction(getHorizontalOnScrollAction());
            onPinchAction(getHorizontalOnPinchAction());
        } else {
            onScrollAction(getVerticalOnScrollAction());
            onPinchAction(getVerticalOnPinchAction());
        }
    }

    @NonNull private OnScrollAction getHorizontalOnScrollAction() {
        return new OnScrollAction() {
            @Override public void onScroll(
                ScrollDirection direction, float coordinateX, float coordinateY, float dX, float dY
            ) {
                T[] domain = scale.domain();
                Float[] range = scale.range();
                float sign = dY < 0f ? 1f : -1f;
                dY = Math.abs(dY);
                D3Converter<T> converter = scale.converter();
                converter.convert(domain[0]);
                float offset = scale.converter().convert(domain[0])
                    - converter.convert(scale.invert(range[0] + dY));
                offset *= sign * 2;
                domain[0] = converter.invert(converter.convert(domain[0]) - offset);
                domain[1] = converter.invert(converter.convert(domain[1]) - offset);
                scale.domain(domain);
            }
        };
    }

    @NonNull private OnPinchAction getHorizontalOnPinchAction() {
        return new OnPinchAction() {
            @Override public void onPinch(
                PinchType pinchType, float coordinateStaticX, float coordinateStaticY,
                float coordinateMobileX, float coordinateMobileY, float dX, float dY
            ) {
                resizeOnPinch(coordinateStaticY, coordinateMobileY, dY);
            }
        };
    }

    private void resizeOnPinch(
        float coordinateStatic, float coordinateMobile, float diffCoordinate
    ) {
        if (Math.abs(coordinateMobile - coordinateStatic) < PINCH_MIN_SPACING) {
            return;
        }

        Float[] range = range();

        float coordinateMin = range[0];
        float coordinateMax = range[1];

        int inverted = 0;
        if (coordinateMin > coordinateMax) {
            inverted = 1;
            float tmp = coordinateMin;
            coordinateMin = coordinateMax;
            coordinateMax = tmp;
        }

        if ((coordinateMobile < coordinateMin || coordinateMobile > coordinateMax)
            || (coordinateStatic < coordinateMin || coordinateStatic > coordinateMax)) {
            return;
        }
        float propMobile = (coordinateMobile + diffCoordinate - coordinateMin)
            / (coordinateMax - coordinateMin);
        float propStatic = (coordinateStatic - coordinateMin) / (coordinateMax - coordinateMin);

        D3Converter<T> converter = scale.converter();

        float valueStatic = converter.convert(scale.invert(coordinateStatic));
        float valueMobile = converter.convert(scale.invert(coordinateMobile - diffCoordinate));

        float newDomainMin = (valueMobile * propStatic - valueStatic * propMobile)
            / (propStatic - propMobile);
        float newDomainMax = (newDomainMin * (propMobile - 1) + valueMobile) /
            propMobile;

        T[] domain = domain();
        domain[inverted] = converter.invert(newDomainMin);
        domain[1 - inverted] = converter.invert(newDomainMax);
        domain(domain);
    }

    @NonNull private OnScrollAction getVerticalOnScrollAction() {
        return new OnScrollAction() {
            @Override public void onScroll(
                ScrollDirection direction, float coordinateX, float coordinateY, float dX, float dY
            ) {
                D3Converter<T> converter = scale.converter();
                T[] domain = scale.domain();
                Float[] range = scale.range();
                float sign = dX < 0f ? 1f : -1f;
                dX = Math.abs(dX);
                float offset = converter.convert(domain[0])
                    - converter.convert(scale.invert(range[0] + dX));
                offset *= sign * 2;
                domain[0] = converter.invert(converter.convert(domain[0]) - offset);
                domain[1] = converter.invert(converter.convert(domain[1]) - offset);
                scale.domain(domain);
            }
        };
    }

    @NonNull private OnPinchAction getVerticalOnPinchAction() {
        return new OnPinchAction() {
            @Override public void onPinch(
                PinchType pinchType, float coordinateStaticX, float coordinateStaticY,
                float coordinateMobileX, float coordinateMobileY, float dX, float dY
            ) {
                resizeOnPinch(coordinateStaticX, coordinateMobileX, dX);
            }
        };
    }

    public D3Axis(AxisOrientation orientation, D3Scale scale) {
        this.orientation = orientation;
        this.scale = scale;
        setupProperties();
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
    public D3Axis<T> axisColor(@ColorInt int color) {
        paint.setColor(color);
        return this;
    }

    /***
     * Set the width of the stroke for the Axis and the ticks
     */
    public D3Axis<T> axisWidth(float width) {
        paint.setStrokeWidth(width);
        return this;
    }

    public T[] domain() {
        return scale.domain();
    }

    public D3Axis<T> domain(T[] domain) {
        scale().domain(domain);
        return this;
    }

    public D3Axis<T> domain(D3RangeFunction function) {
        scale.domain(function);
        return this;
    }

    public Float[] range() {
        return scale.range();
    }

    public D3Axis<T> range(Float[] range) {
        scale.range(range);
        return this;
    }

    public D3Axis<T> range(D3RangeFunction function) {
        scale.range(function);
        return this;
    }

    /***
     * Set the coordinates of the origin
     */
    public D3Axis<T> translate(final float offsetX, final float offsetY) {
        this.offsetX = new D3FloatFunction() {
            @Override public float getFloat() {
                return offsetX;
            }
        };
        this.offsetY = new D3FloatFunction() {
            @Override public float getFloat() {
                return offsetY;
            }
        };
        return this;
    }

    /***
     * @return the horizontal coordinate of the origin
     */
    public float offsetX() {
        return offsetX.getFloat();
    }

    /***
     * Set the horizontal coordinate of the origin
     */
    public D3Axis<T> offsetX(final float offsetX) {
        this.offsetX = new D3FloatFunction() {
            @Override public float getFloat() {
                return offsetX;
            }
        };
        return this;
    }

    /***
     * Set the vertical coordinate of the origin
     */
    public D3Axis<T> offsetX(D3FloatFunction function) {
        this.offsetX = function;
        return this;
    }

    /***
     * @return the vertical coordinate of the origin
     */
    public float offsetY() {
        return offsetY.getFloat();
    }

    /***
     * Set the vertical coordinate of the origin
     */
    public D3Axis<T> offsetY(final float offsetY) {
        this.offsetY = new D3FloatFunction() {
            @Override public float getFloat() {
                return offsetY;
            }
        };
        return this;
    }

    /***
     * Set the horizontal coordinate of the origin
     */
    public D3Axis<T> offsetY(D3FloatFunction function) {
        this.offsetY = function;
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
    public D3Axis<T> ticks(int ticksNumber) {
        ticks = null;
        this.ticksNumber = ticksNumber;
        return this;
    }

    /***
     * Reset ticks : default Scale ticks will be used
     */
    public D3Axis<T> tickValues() {
        ticks = null;
        ticksNumber = DEFAULT_TICK_NUMBER;
        return this;
    }

    /***
     * Set the the ticks to use rather than default Scale ticks
     */
    public D3Axis<T> tickValues(String[] ticks) {
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
    public D3Scale<T> scale() {
        return this.scale;
    }

    /***
     * Set the scale of the Axis
     */
    public D3Axis<T> scale(D3Scale scale) {
        this.scale = scale;
        return this;
    }

    public D3Axis<T> converter(D3Converter<T> converter) {
        scale.converter(converter);
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
    public D3Axis<T> tickSize(float size) {
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
    public D3Axis<T> tickSizeInner(float size) {
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
    public D3Axis<T> tickSizeOuter(float size) {
        outerTickSize = size;
        return this;
    }

    private float firstBoundRange() {
        return scale.range()[0];
    }

    private float lastBoundRange() {
        float[] range = ArrayConverterHelper.convertArray(scale.range());
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
    public D3Axis<T> legendProperties(@Nullable LegendProperties legendProperties) {
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
    public D3Axis<T> textSizeInPixels(float textSizeInPixels) {
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
    public D3Axis<T> legendColor(@ColorInt int color) {
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
    public D3Axis<T> legendVerticalAlignment(VerticalAlignment verticalAlignment) {
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
    public D3Axis<T> legendHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
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
    public D3Axis<T> legendOffsetX(float offsetX) {
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
    public D3Axis<T> legendOffsetY(float offsetY) {
        legendProperties.offsetY(offsetY);
        return this;
    }

    /***
     * set the legend's offsets
     */
    public D3Axis<T> legendOffset(float offsetX, float offsetY) {
        legendProperties.offsetX(offsetX);
        legendProperties.offsetY(offsetY);
        return this;
    }

    @Override public D3Axis<T> onClickAction(OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3Axis<T> onScrollAction(OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3Axis<T> onPinchAction(OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public void draw(Canvas canvas) {
        drawLine(canvas);
        drawTicks(canvas);
        drawTicksLegend(canvas);
    }

    private void drawLine(Canvas canvas) {
        float startX, startY, endX, endY;
        float offsetX = this.offsetX.getFloat();
        float offsetY = this.offsetY.getFloat();
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
        float offsetX = this.offsetX.getFloat();
        float offsetY = this.offsetY.getFloat();
        float outerX = offsetX - outerTickSize / 2;
        float innerX = offsetX + innerTickSize / 2;
        for (int i = 0; i < ticksNumber; i++) {
            float coordinateY = offsetY + lastBoundRange() * i / (ticksNumber - 1.0f)
                + firstBoundRange() * (ticksNumber - i - 1.0f) / (ticksNumber - 1.0f);
            canvas.drawLine(outerX, coordinateY, innerX, coordinateY, paint);
        }
    }

    private void drawVerticalTicks(Canvas canvas) {
        float offsetX = this.offsetX.getFloat();
        float offsetY = this.offsetY.getFloat();
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
        float offsetX = this.offsetX.getFloat();
        String[] usableTicks = this.ticks == null ?
            scale.ticksLegend(this.ticksNumber) : this.ticks;
        float coordinateX = orientation == AxisOrientation.LEFT ?
            offsetX - innerTickSize : offsetX + innerTickSize;
        coordinateX += legendProperties.offsetX();

        for (int i = 0; i < usableTicks.length; i++) {
            drawSingleVerticalLegend(canvas, usableTicks[i], coordinateX, i);
        }
    }

    private void drawSingleVerticalLegend(Canvas canvas, String tick, float coordinateX, int i) {
        float offsetY = this.offsetY.getFloat();
        float coordinateY = offsetY + lastBoundRange() * i / (ticksNumber - 1.0f)
            + firstBoundRange() * (ticksNumber - i - 1.0f) / (ticksNumber - 1.0f);
        coordinateY += legendProperties.offsetY();
        coordinateY += alignmentVerticalOffset(tick);
        coordinateX -= orientation == AxisOrientation.LEFT ?
            textPaint.measureText(tick) : 0;

        canvas.drawText(tick, coordinateX, coordinateY, textPaint);
    }

    private float alignmentVerticalOffset(String legend) {
        float height = TextHelper.getTextHeight(legend, textPaint);
        switch (legendProperties.verticalAlignement()) {
            case BOTTOM:
                return height;
            case CENTER:
                return height / 2.0f;
            default:
                return 0.0f;
        }
    }

    private void drawHorizontalLegend(Canvas canvas) {
        float offsetY = this.offsetY.getFloat();
        String[] usableTicks = this.ticks == null ?
            scale.ticksLegend(this.ticksNumber) : this.ticks;
        float coordinateY = orientation == AxisOrientation.TOP ?
            offsetY - innerTickSize : offsetY + innerTickSize;
        coordinateY += legendProperties.offsetY();

        for (int i = 0; i < usableTicks.length; i++) {
            drawSingleHorizontalLegend(canvas, usableTicks, coordinateY, i);
        }
    }

    private void drawSingleHorizontalLegend(
        Canvas canvas,
        String[] ticks,
        float coordinateY,
        int i
    ) {
        float offsetX = this.offsetX.getFloat();
        float coordinateX = offsetX + lastBoundRange() * i / (ticksNumber - 1.0f)
            + firstBoundRange() * (ticksNumber - i - 1.0f) / (ticksNumber - 1.0f);
        coordinateX += legendProperties.offsetX();
        coordinateX -= alignmentHorizontalOffset(ticks[i]);
        coordinateY += orientation == AxisOrientation.TOP ? 0 :
            TextHelper.getTextHeight("" + ticks[i], textPaint);

        canvas.drawText(ticks[i], coordinateX, coordinateY, textPaint);
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
