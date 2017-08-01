package com.applidium.pierreferrand.d3library.axes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.scale.D3Converter;
import com.applidium.pierreferrand.d3library.scale.D3LabelFunction;
import com.applidium.pierreferrand.d3library.scale.D3Scale;
import com.applidium.pierreferrand.d3library.threading.ValueStorage;

@SuppressWarnings({"WeakerAccess", "unused"})
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

    @NonNull private final D3AxisDrawer<T> drawer = new D3AxisDrawer<>(this);
    @NonNull private final AxisBitmapValueRunnable<T> bitmapValueRunnable =
        new AxisBitmapValueRunnable<>(drawer);
    @NonNull private final ValueStorage<Bitmap> bitmapValueStorage = new ValueStorage<>();

    @NonNull final TicksValueRunnable<T> ticksValueRunnable = new TicksValueRunnable<>(this);
    @NonNull final ValueStorage<String[]> ticksLegend = new ValueStorage<>();

    @NonNull D3FloatFunction offsetX;
    @NonNull D3FloatFunction offsetY;
    float innerTickSize = DEFAULT_TICK_SIZE;
    float outerTickSize = DEFAULT_TICK_SIZE;

    @NonNull final AxisOrientation orientation;
    @NonNull D3Scale<T> scale;

    @NonNull Paint textPaint;
    @NonNull LegendProperties legendProperties;

    public D3Axis(@NonNull AxisOrientation orientation) {
        this.orientation = orientation;
        scale = new D3Scale<>();
        if (orientation == AxisOrientation.TOP || orientation == AxisOrientation.BOTTOM) {
            scale.range(new D3RangeFunction() {
                @Override public float[] getRange() {
                    return new float[]{BEGINNING_PROPORTION * width(), END_PROPORTION * width()};
                }
            });
        } else {
            scale.range(new D3RangeFunction() {
                @Override public float[] getRange() {
                    return new float[]{END_PROPORTION * height(), BEGINNING_PROPORTION * height()};
                }
            });
        }
        setupProperties();
    }

    /**
     * @param tClass allows to define some behaviors for simple types (Float, Integer)
     */
    public D3Axis(@NonNull AxisOrientation orientation, Class<T> tClass) {
        this(orientation);
        converter(AxisDefaultInitializer.getDefaultConverter(tClass));
    }

    public D3Axis(@NonNull AxisOrientation orientation, @NonNull D3Scale<T> scale) {
        this.orientation = orientation;
        this.scale = scale;
        setupProperties();
    }

    /**
     * @param tClass allows to define some behaviors for simple types (Float, Integer)
     */
    public D3Axis(
        @NonNull AxisOrientation orientation, @NonNull D3Scale<T> scale, Class<T> tClass
    ) {
        this(orientation, scale);
        converter(AxisDefaultInitializer.getDefaultConverter(tClass));
    }

    private void setupProperties() {
        translate(DEFAULT_OFFSET, DEFAULT_OFFSET);
        ticks(DEFAULT_TICK_NUMBER);
        this.legendProperties = new LegendProperties();
        if (orientation == AxisOrientation.TOP || orientation == AxisOrientation.BOTTOM) {
            offsetY(new D3FloatFunction() {
                @Override public float getFloat() {
                    return END_PROPORTION * height();
                }
            });
        } else {
            offsetX(new D3FloatFunction() {
                @Override public float getFloat() {
                    return BEGINNING_PROPORTION * width();
                }
            });
        }
        setupPaint();
        setupDefaultActions();
    }

    @Override protected void setupPaint() {
        super.setupPaint();
        drawer.setPaint(paint);
        setupTextPaint();
    }

    private void setupTextPaint() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(legendProperties.color());
        textPaint.setTextSize(legendProperties.textSizeInPixels());
    }

    private void setupDefaultActions() {
        D3AxisActionsInitializer<T> initializer = new D3AxisActionsInitializer<>(this);
        if (orientation == AxisOrientation.RIGHT || orientation == AxisOrientation.LEFT) {
            onScrollAction(initializer.getHorizontalOnScrollAction());
            onPinchAction(initializer.getHorizontalOnPinchAction());
        } else {
            onScrollAction(initializer.getVerticalOnScrollAction());
            onPinchAction(initializer.getVerticalOnPinchAction());
        }
        onClickAction(null);
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
    public D3Axis<T> domain(@NonNull D3DomainFunction<T> function) {
        scale.domain(function);
        return this;
    }

    /**
     * Returns the range of the associated scale.
     */
    @Nullable public float[] range() {
        return scale.range();
    }

    /**
     * Sets the range of the associated scale.
     */
    public D3Axis<T> range(float[] range) {
        scale.range(range);
        return this;
    }

    /**
     * Sets the range of the associated scale.
     */
    public D3Axis<T> range(@NonNull D3RangeFunction function) {
        scale.range(function);
        return this;
    }

    /**
     * Sets the coordinates of the origin.
     */
    public D3Axis<T> translate(final float offsetX, final float offsetY) {
        translate(
            new D3FloatFunction() {
                @NonNull @Override public float getFloat() {
                    return offsetX;
                }
            },
            new D3FloatFunction() {
                @NonNull @Override public float getFloat() {
                    return offsetY;
                }
            }
        );
        return this;
    }

    /**
     * Sets the coordinates of the origin.
     */
    public D3Axis<T> translate(@NonNull D3FloatFunction offsetX, @NonNull D3FloatFunction offsetY) {
        offsetX(offsetX);
        offsetY(offsetY);
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
            @Override public float getFloat() {
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
        return ticksValueRunnable.getTicksNumber();
    }

    /**
     * Sets the number of ticks for the Axis.
     */
    public D3Axis<T> ticks(int ticksNumber) {
        ticksValueRunnable.setTicksNumber(ticksNumber);
        return this;
    }

    /**
     * Resets ticks : default Scale ticks will be used.
     */
    public D3Axis<T> tickValues() {
        ticks(DEFAULT_TICK_NUMBER);
        return this;
    }

    /**
     * Sets the the ticks to use rather than default Scale ticks.
     */
    public D3Axis<T> tickValues(@NonNull String[] ticks) {
        ticksValueRunnable.setCustomTicks(ticks);
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

    float firstBoundRange() {
        if (scale.range() == null || scale.range().length == 0) {
            throw new IllegalStateException(RANGE_ERROR);
        }
        return scale.range()[0];
    }

    float lastBoundRange() {
        if (scale.range() == null || scale.range().length == 0) {
            throw new IllegalStateException(RANGE_ERROR);
        }
        float[] range = scale.range();
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

    public D3LabelFunction<T> labelFunction() {
        return scale.labelFunction();
    }

    public D3Axis<T> labelFunction(D3LabelFunction<T> labelFunction) {
        scale.labelFunction(labelFunction);
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
        @NonNull D3FloatFunction leftLimit, @NonNull D3FloatFunction topLimit,
        @NonNull D3FloatFunction rightLimit, @NonNull D3FloatFunction bottomLimit
    ) {
        super.setClipRect(leftLimit, topLimit, rightLimit, bottomLimit);
        return this;
    }

    @Override public D3Axis<T> deleteClipRect() {
        super.deleteClipRect();
        return this;
    }

    @Override public D3Axis<T> lazyRecomputing(boolean lazyRecomputing) {
        super.lazyRecomputing(lazyRecomputing);
        return this;
    }

    @Override public void prepareParameters() {
        if (lazyRecomputing && calculationNeeded() == 0) {
            return;
        }
        ticksLegend.setValue(ticksValueRunnable);
        bitmapValueStorage.setValue(bitmapValueRunnable);
    }

    @Override protected void onDimensionsChange(float width, float height) {
        bitmapValueRunnable.resizeBitmap(width, height);
    }

    @Override public void draw(@NonNull Canvas canvas) {
        if (lazyRecomputing && calculationNeeded() == 0) {
            canvas.drawBitmap(bitmapValueStorage.getValue(), 0F, 0F, null);
        } else {
            drawer.draw(canvas);
        }
    }
}
