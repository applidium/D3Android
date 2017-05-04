package com.applidium.pierreferrand.d3library.axes;

import android.graphics.Canvas;
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
    private static final float DEFAULT_TICK_SIZE = 25F;
    private static final float DEFAULT_OFFSET = 0F;
    private static final int DEFAULT_TICK_NUMBER = 5;
    private static final float BEGINNING_PROPORTION = 0.05F;
    private static final float END_PROPORTION = 0.95F;
    private static final float PINCH_MIN_SPACING = 100F;
    private static final String CONVERTER_ERROR = "Converter should not be null";
    private static final String DOMAIN_ERROR = "Domain should not be null";
    private static final String RANGE_ERROR = "Range should not be null";
    private static final String SCALE_ERROR = "Scale should not be null";

    @NonNull private D3FloatFunction offsetX;
    @NonNull private D3FloatFunction offsetY;
    private float innerTickSize = DEFAULT_TICK_SIZE;
    private float outerTickSize = DEFAULT_TICK_SIZE;
    private int ticksNumber = DEFAULT_TICK_NUMBER;

    @NonNull private final AxisOrientation orientation;
    @NonNull private D3Scale<T> scale;

    @Nullable private String[] ticks;
    @NonNull private Paint textPaint;
    @NonNull private LegendProperties legendProperties;

    public D3Axis(@NonNull AxisOrientation orientation) {
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

    public D3Axis(@NonNull AxisOrientation orientation, @NonNull D3Scale<T> scale) {
        this.orientation = orientation;
        this.scale = scale;
        setupProperties();
    }

    private void setupProperties() {
        translate(DEFAULT_OFFSET, DEFAULT_OFFSET);
        this.legendProperties = new LegendProperties();
        setupPaint();
        setupDefaultActions();
    }

    @Override protected void setupPaint() {
        super.setupPaint();
        setupTextPaint();
    }

    private void setupTextPaint() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(legendProperties.color());
        textPaint.setTextSize(legendProperties.textSizeInPixels());
    }

    private void setupDefaultActions() {
        if (orientation == AxisOrientation.RIGHT || orientation == AxisOrientation.LEFT) {
            onScrollAction(getHorizontalOnScrollAction());
            onPinchAction(getHorizontalOnPinchAction());
        } else {
            onScrollAction(getVerticalOnScrollAction());
            onPinchAction(getVerticalOnPinchAction());
        }
        onClickAction(null);
    }

    @NonNull private OnScrollAction getHorizontalOnScrollAction() {
        return new OnScrollAction() {
            @Override public void onScroll(
                ScrollDirection direction, float coordinateX, float coordinateY, float dX, float dY
            ) {
                D3Converter<T> converter = scale.converter();
                if (converter == null) {
                    throw new IllegalStateException(CONVERTER_ERROR);
                }
                T[] domain = scale.domain();
                if (domain == null) {
                    throw new IllegalStateException(DOMAIN_ERROR);
                }
                converter.convert(domain[0]);
                Float[] range = scale.range();
                if (range == null) {
                    throw new IllegalStateException(RANGE_ERROR);
                }
                float sign = dY < 0F ? 1F : -1F;
                float absDy = Math.abs(dY);
                float offset = converter.convert(domain[0])
                    - converter.convert(scale.invert(range[0] + absDy));
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

        D3Converter<T> converter = scale.converter();
        if (converter == null) {
            throw new IllegalStateException(CONVERTER_ERROR);
        }
        float propMobile = (coordinateMobile + diffCoordinate - coordinateMin)
            / (coordinateMax - coordinateMin);
        float propStatic = (coordinateStatic - coordinateMin) / (coordinateMax - coordinateMin);
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
                if (converter == null) {
                    throw new IllegalStateException(CONVERTER_ERROR);
                }
                T[] domain = scale.domain();
                if (domain == null) {
                    throw new IllegalStateException(DOMAIN_ERROR);
                }
                Float[] range = scale.range();
                if (range == null) {
                    throw new IllegalStateException(RANGE_ERROR);
                }
                float sign = dX < 0F ? 1F : -1F;
                float absoluteDx = Math.abs(dX);
                float offset = converter.convert(domain[0])
                    - converter.convert(scale.invert(range[0] + absoluteDx));
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

    /**
     * Returns the domain of the associated scale.
     */
    @Nullable public T[] domain() {
        return scale.domain();
    }

    /**
     * Sets the domain of the associated scale.
     */
    public D3Axis<T> domain(@NonNull T[] domain) {
        scale().domain(domain);
        return this;
    }

    /**
     * Sets the domain of the associated scale.
     */
    public D3Axis<T> domain(@NonNull D3RangeFunction<T> function) {
        scale.domain(function);
        return this;
    }

    /**
     * Returns the range of the associated scale.
     */
    @Nullable Float[] range() {
        return scale.range();
    }

    /**
     * Sets the range of the associated scale.
     */
    public D3Axis<T> range(Float[] range) {
        scale.range(range);
        return this;
    }

    /**
     * Sets the range of the associated scale.
     */
    public D3Axis<T> range(@NonNull D3RangeFunction<Float> function) {
        scale.range(function);
        return this;
    }

    /**
     * Sets the coordinates of the origin.
     */
    public D3Axis<T> translate(final float offsetX, final float offsetY) {
        this.offsetX = new D3FloatFunction() {
            @NonNull @Override public float getFloat() {
                return offsetX;
            }
        };
        this.offsetY = new D3FloatFunction() {
            @NonNull @Override public float getFloat() {
                return offsetY;
            }
        };
        return this;
    }

    /**
     * Returns the horizontal coordinate of the origin.
     */
    public float offsetX() {
        return offsetX.getFloat();
    }

    /**
     * Sets the horizontal coordinate of the origin.
     */
    public D3Axis<T> offsetX(final float offsetX) {
        this.offsetX = new D3FloatFunction() {
            @NonNull @Override public float getFloat() {
                return offsetX;
            }
        };
        return this;
    }

    /**
     * Sets the vertical coordinate of the origin.
     */
    public D3Axis<T> offsetX(@NonNull D3FloatFunction function) {
        this.offsetX = function;
        return this;
    }

    /**
     * Returns the vertical coordinate of the origin.
     */
    public float offsetY() {
        return offsetY.getFloat();
    }

    /**
     * Sets the vertical coordinate of the origin.
     */
    public D3Axis<T> offsetY(final float offsetY) {
        this.offsetY = new D3FloatFunction() {
            @Override public float getFloat() {
                return offsetY;
            }
        };
        return this;
    }

    /**
     * Sets the horizontal coordinate of the origin.
     */
    public D3Axis<T> offsetY(@NonNull D3FloatFunction function) {
        this.offsetY = function;
        return this;
    }

    /**
     * Returns the number of ticks for the Axis.
     */
    public int ticks() {
        return ticksNumber;
    }

    /**
     * Sets the number of ticks for the Axis.
     */
    public D3Axis<T> ticks(int ticksNumber) {
        ticks = null;
        this.ticksNumber = ticksNumber;
        return this;
    }

    /**
     * Resets ticks : default Scale ticks will be used.
     */
    public D3Axis<T> tickValues() {
        ticks = null;
        ticksNumber = DEFAULT_TICK_NUMBER;
        return this;
    }

    /**
     * Sets the the ticks to use rather than default Scale ticks.
     */
    public D3Axis<T> tickValues(@NonNull String[] ticks) {
        if (ticks.length < 2) {
            throw new IllegalStateException("TickValue must have at least 2 values");
        }
        ticksNumber = ticks.length;
        this.ticks = ticks.clone();
        return this;
    }

    /**
     * Returns the scale of the Axis.
     */
    public D3Scale<T> scale() {
        return this.scale;
    }

    /**
     * Sets the scale of the Axis.
     */
    public D3Axis<T> scale(@NonNull D3Scale<T> scale) {
        this.scale = scale;
        return this;
    }

    public D3Axis<T> converter(@NonNull D3Converter<T> converter) {
        scale.converter(converter);
        return this;
    }

    /**
     * Returns the inner ticks' size.
     */
    public float tickSize() {
        return tickSizeInner();
    }

    /**
     * Sets the inner and outer ticks' size.
     */
    public D3Axis<T> tickSize(float size) {
        tickSizeInner(size);
        tickSizeOuter(size);
        return this;
    }

    /**
     * Returns the inner ticks' size.
     */
    public float tickSizeInner() {
        return innerTickSize;
    }

    /**
     * Sets the inner ticks' size.
     */
    public D3Axis<T> tickSizeInner(float size) {
        innerTickSize = size;
        return this;
    }

    /**
     * Returns the outer ticks' size.
     */
    public float tickSizeOuter() {
        return outerTickSize;
    }

    /**
     * Sets the outer ticks' size.
     */
    public D3Axis<T> tickSizeOuter(float size) {
        outerTickSize = size;
        return this;
    }

    private float firstBoundRange() {
        if (scale.range() == null) {
            throw new IllegalStateException(RANGE_ERROR);
        }
        return scale.range()[0];
    }

    private float lastBoundRange() {
        float[] range = ArrayConverterHelper.convertArray(scale.range());
        return range[range.length - 1];
    }

    /**
     * Returns the legendProperties use for legends.
     */
    public LegendProperties legendProperties() {
        return legendProperties;
    }

    /**
     * Sets the legendProperties to use. If null, use the default one.
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

    /**
     * Returns the legend's text size.
     */
    public float textSizeInPixels() {
        return legendProperties.textSizeInPixels();
    }

    /**
     * Sets the legend's text size.
     */
    public D3Axis<T> textSizeInPixels(float textSizeInPixels) {
        legendProperties.textSizeInPixels(textSizeInPixels);
        setupTextPaint();
        return this;
    }

    /**
     * Returns the legend's color.
     */
    @ColorInt public int legendColor() {
        return legendProperties.color();
    }

    /**
     * Sets the legend's color.
     */
    public D3Axis<T> legendColor(@ColorInt int color) {
        legendProperties.color(color);
        setupTextPaint();
        return this;
    }

    /**
     * Returns the legend's vertical alignment.
     */
    public VerticalAlignment legendVerticalAlignment() {
        return legendProperties.verticalAlignment();
    }

    /**
     * sets the legend's vertical alignment for a vertical Axis.
     */
    public D3Axis<T> legendVerticalAlignment(@NonNull VerticalAlignment verticalAlignment) {
        legendProperties.verticalAlignment(verticalAlignment);
        return this;
    }

    /**
     * Returns the legend's horizontal alignment.
     */
    public HorizontalAlignment legendHorizontalAlignment() {
        return legendProperties.horizontalAlignement();
    }

    /**
     * Sets the legend's horizontal alignment for a horizontal Axis.
     */
    public D3Axis<T> legendHorizontalAlignment(@NonNull HorizontalAlignment horizontalAlignment) {
        legendProperties.horizontalAlignement(horizontalAlignment);
        return this;
    }

    /**
     * Returns the legend's horizontal offset.
     */
    public float legendOffsetX() {
        return legendProperties.offsetX();
    }

    /**
     * Sets the legend's horizontal offset.
     */
    public D3Axis<T> legendOffsetX(float offsetX) {
        legendProperties.offsetX(offsetX);
        return this;
    }

    /**
     * Returns the legend's vertical offset.
     */
    public float legendOffsetY() {
        return legendProperties.offsetY();
    }

    /**
     * Sets the legend's vertical offset.
     */
    public D3Axis<T> legendOffsetY(float offsetY) {
        legendProperties.offsetY(offsetY);
        return this;
    }

    /**
     * Sets the legend's offsets.
     */
    public D3Axis<T> legendOffset(float offsetX, float offsetY) {
        legendProperties.offsetX(offsetX);
        legendProperties.offsetY(offsetY);
        return this;
    }

    @Override public D3Axis<T> onClickAction(@Nullable OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3Axis<T> onScrollAction(@Nullable OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3Axis<T> onPinchAction(@Nullable OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public D3Axis<T> setClipRect(
        @NonNull D3FloatFunction leftLimit,
        @NonNull D3FloatFunction topLimit,
        @NonNull D3FloatFunction rightLimit,
        @NonNull D3FloatFunction bottomLimit
    ) {
        super.setClipRect(leftLimit, topLimit, rightLimit, bottomLimit);
        return this;
    }

    @Override public D3Axis<T> deleteClipRect() {
        super.deleteClipRect();
        return this;
    }

    @Override public void draw(@NonNull Canvas canvas) {
        drawLine(canvas);
        drawTicks(canvas);
        drawTicksLegend(canvas);
    }

    private void drawLine(@NonNull Canvas canvas) {
        float startX;
        float startY;
        float endX;
        float endY;
        float computedOffsetX = this.offsetX.getFloat();
        float computedOffsetY = this.offsetY.getFloat();
        if (orientation == AxisOrientation.TOP || orientation == AxisOrientation.BOTTOM) {
            startX = computedOffsetX + firstBoundRange();
            startY = computedOffsetY;
            endX = computedOffsetX + lastBoundRange();
            endY = computedOffsetY;
        } else {
            startX = computedOffsetX;
            startY = computedOffsetY + firstBoundRange();
            endX = computedOffsetX;
            endY = computedOffsetY + lastBoundRange();
        }

        canvas.drawLine(startX, startY, endX, endY, paint);
    }

    private void drawTicks(@NonNull Canvas canvas) {
        if (orientation == AxisOrientation.TOP || orientation == AxisOrientation.BOTTOM) {
            drawVerticalTicks(canvas);
        } else {
            drawHorizontalTicks(canvas);
        }
    }

    private void drawHorizontalTicks(@NonNull Canvas canvas) {
        float computedOffsetX = this.offsetX.getFloat();
        float computedOffsetY = this.offsetY.getFloat();
        float outerX = computedOffsetX - outerTickSize / 2;
        float innerX = computedOffsetX + innerTickSize / 2;
        for (int i = 0; i < ticksNumber; i++) {
            float coordinateY = computedOffsetY + lastBoundRange() * i / (ticksNumber - 1F)
                + firstBoundRange() * (ticksNumber - i - 1F) / (ticksNumber - 1F);
            canvas.drawLine(outerX, coordinateY, innerX, coordinateY, paint);
        }
    }

    private void drawVerticalTicks(@NonNull Canvas canvas) {
        float computedOffsetX = this.offsetX.getFloat();
        float computedOffsetY = this.offsetY.getFloat();
        float outerY = computedOffsetY + outerTickSize / 2;
        float innerY = computedOffsetY - innerTickSize / 2;
        for (int i = 0; i < ticksNumber; i++) {
            float coordinateX = computedOffsetX + lastBoundRange() * i / (ticksNumber - 1F)
                + firstBoundRange() * (ticksNumber - i - 1F) / (ticksNumber - 1F);
            canvas.drawLine(coordinateX, innerY, coordinateX, outerY, paint);
        }
    }

    private void drawTicksLegend(@NonNull Canvas canvas) {
        if (orientation == AxisOrientation.TOP || orientation == AxisOrientation.BOTTOM) {
            drawHorizontalLegend(canvas);
        } else {
            drawVerticalLegend(canvas);
        }
    }

    private void drawVerticalLegend(@NonNull Canvas canvas) {
        float computedOffsetX = this.offsetX.getFloat();
        String[] usableTicks = this.ticks == null ?
            scale.ticksLegend(this.ticksNumber) : this.ticks;
        float coordinateX = orientation == AxisOrientation.LEFT ?
            computedOffsetX - innerTickSize : computedOffsetX + innerTickSize;
        coordinateX += legendProperties.offsetX();

        for (int i = 0; i < usableTicks.length; i++) {
            drawSingleVerticalLegend(canvas, usableTicks[i], coordinateX, i);
        }
    }

    private void drawSingleVerticalLegend(
        @NonNull Canvas canvas, @NonNull String tick, float coordinateX, int i
    ) {
        float computedOffsetY = this.offsetY.getFloat();
        float coordinateY = computedOffsetY + lastBoundRange() * i / (ticksNumber - 1F)
            + firstBoundRange() * (ticksNumber - i - 1F) / (ticksNumber - 1F);
        coordinateY += legendProperties.offsetY();
        coordinateY += alignmentVerticalOffset(tick);
        float realCoordinateX = coordinateX - (orientation == AxisOrientation.LEFT ?
            textPaint.measureText("" + tick) : 0);
        canvas.drawText(tick, realCoordinateX, coordinateY, textPaint);
    }

    private float alignmentVerticalOffset(@NonNull String legend) {
        float height = TextHelper.getTextHeight(legend, textPaint);
        switch (legendProperties.verticalAlignment()) {
            case BOTTOM:
                return height;
            case CENTER:
                return height / 2F;
            default:
                return 0F;
        }
    }

    private void drawHorizontalLegend(@NonNull Canvas canvas) {
        float computedOffsetY = this.offsetY.getFloat();
        String[] usableTicks = this.ticks == null ?
            scale.ticksLegend(this.ticksNumber) : this.ticks;
        float coordinateY = orientation == AxisOrientation.TOP ?
            computedOffsetY - innerTickSize : computedOffsetY + innerTickSize;
        coordinateY += legendProperties.offsetY();

        for (int i = 0; i < usableTicks.length; i++) {
            drawSingleHorizontalLegend(canvas, usableTicks, coordinateY, i);
        }
    }

    private void drawSingleHorizontalLegend(
        @NonNull Canvas canvas,
        @NonNull String[] ticks,
        float coordinateY,
        int i
    ) {
        float computedOffsetX = this.offsetX.getFloat();
        float coordinateX = computedOffsetX + lastBoundRange() * i / (ticksNumber - 1F)
            + firstBoundRange() * (ticksNumber - i - 1F) / (ticksNumber - 1F);
        coordinateX += legendProperties.offsetX();
        coordinateX -= alignmentHorizontalOffset(ticks[i]);
        float realCoordinateY = coordinateY + (orientation == AxisOrientation.TOP ? 0 :
            TextHelper.getTextHeight(ticks[i], textPaint));

        canvas.drawText(ticks[i], coordinateX, realCoordinateY, textPaint);
    }

    private float alignmentHorizontalOffset(@NonNull String legend) {
        switch (legendProperties.horizontalAlignement()) {
            case LEFT:
                return textPaint.measureText(legend);
            case CENTER:
                return textPaint.measureText(legend) / 2F;
            default:
                return 0F;
        }
    }
}
