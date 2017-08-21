package com.applidium.pierreferrand.d3library.curve;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.line.D3Line;
import com.applidium.pierreferrand.d3library.scale.Interpolator;
import com.applidium.pierreferrand.d3library.threading.ValueStorage;

public class D3Curve<T> extends D3Line<T> {
    private static final String DATA_ERROR = "Data should not be null";
    private static final String TICKS_ERROR =
        "PrepareParameters should have be called to setup ticksX and ticksY";
    private static final String TICKS_X_ERROR = "TicksX should not be null";
    private static final int DEFAULT_POINT_NUMBER = 100;
    private static final int DEFAULT_INDEX_SIZE = 5;

    @NonNull final ValueStorage<float[]> ticksX = new ValueStorage<>();
    @NonNull private final GetTicksXRunnable<T> ticksXRunnable = new GetTicksXRunnable<>(this);
    @NonNull final ValueStorage<float[]> ticksY = new ValueStorage<>();
    @NonNull private final GetTicksYRunnable<T> ticksYRunnable = new GetTicksYRunnable<>(this);


    private int pointsNumber;

    public D3Curve() {
        super();
        initInterpolator();
        pointsNumber(DEFAULT_POINT_NUMBER);
    }

    public D3Curve(T[] data) {
        super(data);
        initInterpolator();
        pointsNumber(DEFAULT_POINT_NUMBER);
    }

    private void initInterpolator() {
        interpolator = new Interpolator() {
            private int[] indexToUse;

            @Override
            public float interpolate(
                float x,
                float[] xData,
                float[] yData
            ) {
                if (indexToUse == null) {
                    initIndex(yData.length);
                }
                return computeLagrangePolynomialInterpolation(x, xData, yData);
            }

            private float computeLagrangePolynomialInterpolation(
                float x,
                float[] xData,
                float[] yData
            ) {
                float result = 0F;
                for (int i = 0; i < indexToUse.length; i++) {
                    float intermediary = 1F;
                    for (int j = 0; j < indexToUse.length; j++) {
                        if (i != j) {
                            intermediary *= (x - xData[indexToUse[j]]) /
                                (xData[indexToUse[i]] - xData[indexToUse[j]]);
                        }
                    }
                    result += yData[indexToUse[i]] * intermediary;
                }
                return result;
            }

            private void initIndex(int indexSize) {
                if (indexSize <= DEFAULT_INDEX_SIZE) {
                    indexToUse = new int[indexSize];
                    for (int i = 0; i < indexToUse.length; i++) {
                        indexToUse[i] = i;
                    }
                } else {
                    indexToUse = new int[DEFAULT_INDEX_SIZE];
                    for (int i = 0; i < DEFAULT_INDEX_SIZE; i++) {
                        indexToUse[i] = (indexSize - 1) * i / (DEFAULT_INDEX_SIZE - 1);
                    }
                }
            }
        };
    }

    @Override public float interpolateValue(float measuredX) {
        return interpolator.interpolate(measuredX, x(), y());
    }

    /**
     * Returns the number of point used by the interpolation
     */
    public int pointsNumber() {
        return pointsNumber;
    }

    /**
     * Sets the number of point used by the interpolation
     */
    public D3Curve<T> pointsNumber(int pointsNumber) {
        this.pointsNumber = pointsNumber;
        ticksXRunnable.onPointsNumberChange(pointsNumber);
        ticksYRunnable.onPointsNumberChange(pointsNumber);
        lines = new float[4 * (pointsNumber - 1)];
        return this;
    }

    @Override public D3Curve<T> paint(@NonNull Paint paint) {
        super.paint(paint);
        return this;
    }

    @Override public D3Curve<T> x(@NonNull D3DataMapperFunction<T> x) {
        super.x(x);
        return this;
    }

    @Override public D3Curve<T> y(@NonNull D3DataMapperFunction<T> y) {
        super.y(y);
        return this;
    }

    @Override public D3Curve<T> data(@Nullable T[] data) {
        this.data = data;
        if (data == null) {
            setDataStorageDataLength(0);
            return this;
        }
        setDataStorageDataLength(data.length);
        return this;
    }

    @Override public D3Curve<T> interpolator(@NonNull Interpolator interpolator) {
        super.interpolator(interpolator);
        return this;
    }

    @Override public D3Curve<T> onClickAction(@Nullable OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3Curve<T> onScrollAction(@Nullable OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3Curve<T> onPinchAction(@Nullable OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public void draw(@NonNull Canvas canvas) {
        if (data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }

        if (data.length < 2) {
            return;
        }

        float[] xDraw = ticksX.getValue();
        float[] yDraw = ticksY.getValue();

        for (int i = 0; i < pointsNumber - 1; i++) {
            lines[4 * i] = xDraw[i];
            lines[4 * i + 1] = yDraw[i];
            lines[4 * i + 2] = xDraw[i + 1];
            lines[4 * i + 3] = yDraw[i + 1];
        }
        canvas.drawLines(lines, paint);
    }

    @Override public void prepareParameters() {
        if (lazyRecomputing && calculationNeeded() == 0) {
            return;
        }
        super.prepareParameters();
        ticksX.setValue(ticksXRunnable);
        ticksY.setValue(ticksYRunnable);
    }

    @Override public D3Curve<T> setClipRect(
        @NonNull D3FloatFunction leftLimit,
        @NonNull D3FloatFunction topLimit,
        @NonNull D3FloatFunction rightLimit,
        @NonNull D3FloatFunction bottomLimit
    ) {
        super.setClipRect(leftLimit, topLimit, rightLimit, bottomLimit);
        return this;
    }

    @Override public D3Curve<T> deleteClipRect() {
        super.deleteClipRect();
        return this;
    }
}
