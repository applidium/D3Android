package com.applidium.pierreferrand.d3library.boxplot;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.scale.D3Scale;

public class D3BoxPlot extends D3Drawable {
    private static final String OFFSET_X_ERROR = "OffsetX should not be null";
    private static final String DATA_WIDTH_ERROR = "DataWidth should not be null";
    private static final String DATA_ERROR = "Data should not be null";
    private static final String SCALE_ERROR = "Scale should not be null";
    private static final float DEFAULT_WIDTH = 50F;
    private static final float DEFAULT_OFFSET = 0F;

    @Nullable private D3FloatFunction dataWidth;
    @Nullable private D3FloatFunction offsetX;
    @Nullable private D3Scale<Float> scale;

    @Nullable private float[] data;

    private float min;
    private float max;
    private float median;
    private float lowerQuartile;
    private float upperQuartile;

    public D3BoxPlot() {
        setupDefaultValues();
    }

    public D3BoxPlot(
        float min, float max, float median, float lowerQuartile, float upperQuartile
    ) {
        this.min = min;
        this.max = max;
        this.median = median;
        this.lowerQuartile = lowerQuartile;
        this.upperQuartile = upperQuartile;
        setupDefaultValues();

    }

    public D3BoxPlot(@NonNull float[] data) {
        data(data);
        setupDefaultValues();
    }

    private void setupDefaultValues() {
        dataWidth(DEFAULT_WIDTH);
        offsetX(DEFAULT_OFFSET);
        scale(new D3Scale<>(new Float[]{0F, 1F}));
    }

    /**
     * If data had be given, returns it. Else returns null.
     */
    @Nullable public float[] data() {
        return data != null ? data.clone() : null;
    }

    /**
     * Sets the data for the BoxPlot. Compute and update statistics.
     */
    public D3BoxPlot data(@NonNull float[] data) {
        this.data = data.clone();
        computeStatistics();
        return this;
    }

    /**
     * Returns the minimum that had been either computed or given.
     */
    public float min() {
        return min;
    }

    /**
     * Sets the minimum and erases data.
     */
    public D3BoxPlot min(float min) {
        data = null;
        this.min = min;
        return this;
    }

    /**
     * Returns the maximum that had been either computed or given.
     */
    public float max() {
        return max;
    }

    /**
     * Sets the maximum and erases data.
     */
    public D3BoxPlot max(float max) {
        data = null;
        this.max = max;
        return this;
    }

    /**
     * Returns the median that had been either computed or given.
     */
    public float median() {
        return median;
    }

    /**
     * Sets the median and erases data.
     */
    public D3BoxPlot median(float median) {
        data = null;
        this.median = median;
        return this;
    }

    /**
     * Returns the lower quartile that had been either computed or given.
     */
    public float lowerQuartile() {
        return lowerQuartile;
    }

    /**
     * Sets the lower quartile and erases data.
     */
    public D3BoxPlot lowerQuartile(float lowerQuartile) {
        data = null;
        this.lowerQuartile = lowerQuartile;
        return this;
    }

    /**
     * Returns the upper quartile that had been either computed or given.
     */
    public float upperQuartile() {
        return upperQuartile;
    }

    /**
     * Sets the upper quartile and erases data.
     */
    public D3BoxPlot upperQuartile(float upperQuartile) {
        data = null;
        this.upperQuartile = upperQuartile;
        return this;
    }

    /**
     * Returns the Scale used by the BoxPlot.
     */
    @Nullable public D3Scale<Float> scale() {
        return scale;
    }

    /**
     * Sets the Scale used by the BoxPlot for drawing itself.
     */
    public D3BoxPlot scale(@NonNull D3Scale<Float> scale) {
        this.scale = scale;
        return this;
    }

    /**
     * Returns the horizontal offset for the BoxPlot.
     */
    public float offsetX() {
        if (offsetX == null) {
            throw new IllegalStateException(OFFSET_X_ERROR);
        }
        return offsetX.getFloat();
    }

    /**
     * Sets the horizontal offset for the BoxPlot.
     */
    public D3BoxPlot offsetX(final float offsetX) {
        this.offsetX = new D3FloatFunction() {
            @Override public float getFloat() {
                return offsetX;
            }
        };
        return this;
    }

    /**
     * Sets the horizontal offset for the BoxPlot.
     */
    public D3BoxPlot offsetX(@NonNull D3FloatFunction offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    /**
     * Returns the width of the BoxPlot's core.
     */
    public float dataWidth() {
        if (dataWidth == null) {
            throw new IllegalStateException(DATA_WIDTH_ERROR);
        }
        return dataWidth.getFloat();
    }

    /**
     * Sets the width of the BoxPlot's core.
     */
    public D3BoxPlot dataWidth(final float dataWidth) {
        this.dataWidth = new D3FloatFunction() {
            @Override public float getFloat() {
                return dataWidth;
            }
        };
        return this;
    }

    /**
     * Sets the width of the BoxPlot's core.
     */
    public D3BoxPlot dataWidth(@NonNull D3FloatFunction dataWidth) {
        this.dataWidth = dataWidth;
        return this;
    }

    private void computeStatistics() {
        if (data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        int i = 0;
        float swap;
        while (i < data.length - 1) {
            if (data[i] > data[i + 1]) {
                swap = data[i];
                data[i] = data[i + 1];
                data[i + 1] = swap;
                i = Math.max(i - 1, 0);
            } else {
                i++;
            }
        }

        min = data[0];
        max = data[data.length - 1];
        median = data.length % 2 == 0 ?
            (data[data.length / 2 - 1] + data[data.length / 2]) / 2F :
            data[data.length / 2];

        lowerQuartile = data[Math.round((float) data.length / 4F)];
        upperQuartile = data[Math.round((float) data.length * 3F / 4F)];

    }

    @Override public D3BoxPlot onClickAction(@Nullable OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3BoxPlot onScrollAction(@Nullable OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3BoxPlot onPinchAction(@Nullable OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public D3BoxPlot setClipRect(
        @NonNull D3FloatFunction leftLimit,
        @NonNull D3FloatFunction topLimit,
        @NonNull D3FloatFunction rightLimit,
        @NonNull D3FloatFunction bottomLimit
    ) {
        super.setClipRect(leftLimit, topLimit, rightLimit, bottomLimit);
        return this;
    }

    @Override public D3BoxPlot deleteClipRect() {
        super.deleteClipRect();
        return this;
    }

    @Override public void draw(@NonNull Canvas canvas) {
        if (scale == null) {
            throw new IllegalStateException(SCALE_ERROR);
        }
        if (dataWidth == null) {
            throw new IllegalStateException(DATA_WIDTH_ERROR);
        }
        if (offsetX == null) {
            throw new IllegalStateException(OFFSET_X_ERROR);
        }
        float coordinateMax = scale.value(max);
        float coordinateMin = scale.value(min);
        float coordinateMedian = scale.value(median);
        float coordinateLowerQuartile = scale.value(lowerQuartile);
        float coordinateUpperQuartile = scale.value(upperQuartile);
        float realWidth = dataWidth.getFloat();
        float realOffsetX = offsetX.getFloat();


        canvas.drawLine(
            realOffsetX,
            coordinateMax,
            realOffsetX + realWidth,
            coordinateMax,
            paint
        );

        canvas.drawLine(
            realOffsetX,
            coordinateMin,
            realOffsetX + realWidth,
            coordinateMin,
            paint
        );

        canvas.drawLine(
            realOffsetX,
            coordinateMedian,
            realOffsetX + realWidth,
            coordinateMedian,
            paint
        );

        canvas.drawRect(
            realOffsetX,
            coordinateUpperQuartile,
            realOffsetX + realWidth,
            coordinateLowerQuartile,
            paint
        );

        canvas.drawLine(
            realOffsetX + realWidth / 2F,
            coordinateMax,
            realOffsetX + realWidth / 2F,
            coordinateUpperQuartile,
            paint
        );

        canvas.drawLine(
            realOffsetX + realWidth / 2F,
            coordinateLowerQuartile,
            realOffsetX + realWidth / 2F,
            coordinateMin,
            paint
        );
    }
}
