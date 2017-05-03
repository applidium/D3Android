package com.applidium.pierreferrand.d3library.boxplot;

import android.graphics.Canvas;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.scale.D3Scale;

public class D3BoxPlot extends D3Drawable {

    private static final float DEFAULT_WIDTH = 50f;
    private static final float DEFAULT_OFFSET = 0f;

    private D3FloatFunction dataWidth;
    private D3FloatFunction offsetX;
    private D3Scale<Float> scale;

    private float[] data;

    private float min;
    private float max;
    private float median;
    private float lowerQuartile;
    private float upperQuartile;

    public D3BoxPlot() {
        setupDefaultValues();
    }

    private void setupDefaultValues() {
        dataWidth(DEFAULT_WIDTH);
        offsetX(DEFAULT_OFFSET);
        scale(new D3Scale<>(new Float[]{0f, 1f}));
    }

    private D3BoxPlot(
        float min, float max, float median, float lowerQuartile, float upperQuartile
    ) {
        this.min = min;
        this.max = max;
        this.median = median;
        this.lowerQuartile = lowerQuartile;
        this.upperQuartile = upperQuartile;
        setupDefaultValues();

    }

    public D3BoxPlot(float[] data) {
        data(data);
        setupDefaultValues();
    }

    public float[] data() {
        return data != null ? data.clone() : null;
    }

    public D3BoxPlot data(float[] data) {
        this.data = data.clone();
        computeStatistics();
        return this;
    }

    public float min() {
        return min;
    }

    public D3BoxPlot min(float min) {
        data = null;
        this.min = min;
        return this;
    }

    public float max() {
        return max;
    }

    public D3BoxPlot max(float max) {
        data = null;
        this.max = max;
        return this;
    }

    public float median() {
        return median;
    }

    public D3BoxPlot median(float median) {
        data = null;
        this.median = median;
        return this;
    }

    public float lowerQuartile() {
        return lowerQuartile;
    }

    public D3BoxPlot lowerQuartile(float lowerQuartile) {
        data = null;
        this.lowerQuartile = lowerQuartile;
        return this;
    }

    public float upperQuartile() {
        return upperQuartile;
    }

    public D3BoxPlot upperQuartile(float upperQuartile) {
        data = null;
        this.upperQuartile = upperQuartile;
        return this;
    }

    public D3Scale<Float> scale() {
        return scale;
    }

    public D3BoxPlot scale(D3Scale<Float> scale) {
        this.scale = scale;
        return this;
    }

    public float offsetX() {
        return offsetX.getFloat();
    }

    public D3BoxPlot offsetX(final float offsetX) {
        this.offsetX = new D3FloatFunction() {
            @Override public float getFloat() {
                return offsetX;
            }
        };
        return this;
    }

    public D3BoxPlot offsetX(D3FloatFunction offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public float dataWidth() {
        return dataWidth.getFloat();
    }

    public D3BoxPlot dataWidth(final float dataWidth) {
        this.dataWidth = new D3FloatFunction() {
            @Override public float getFloat() {
                return dataWidth;
            }
        };
        return this;
    }

    public D3BoxPlot dataWidth(D3FloatFunction dataWidth) {
        this.dataWidth = dataWidth;
        return this;
    }

    private void computeStatistics() {
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
            (data[data.length / 2 - 1] + data[data.length / 2]) / 2f :
            data[data.length / 2];

        lowerQuartile = data[Math.round((float) data.length / 4f)];
        upperQuartile = data[Math.round((float) data.length * 3f / 4f)];

    }

    @Override public D3BoxPlot onClickAction(OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3BoxPlot onScrollAction(OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3BoxPlot onPinchAction(OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public void draw(Canvas canvas) {
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
            realOffsetX + realWidth / 2f,
            coordinateMax,
            realOffsetX + realWidth / 2f,
            coordinateUpperQuartile,
            paint
        );

        canvas.drawLine(
            realOffsetX + realWidth / 2f,
            coordinateLowerQuartile,
            realOffsetX + realWidth / 2f,
            coordinateMin,
            paint
        );
    }
}
