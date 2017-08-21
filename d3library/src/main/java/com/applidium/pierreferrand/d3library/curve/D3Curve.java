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
import com.applidium.pierreferrand.d3library.threading.ValueRunnable;
import com.applidium.pierreferrand.d3library.threading.ValueStorage;

public class D3Curve<T> extends D3Line<T> {
    private static final String DATA_ERROR = "Data should not be null";
    private static final String TICKS_ERROR =
        "PrepareParameters should have be called to setup ticksX and ticksY";
    private static final String TICKS_X_ERROR = "TicksX should not be null";
    private static final int DEFAULT_POINT_NUMBER = 100;
    private static final int DEFAULT_INDEX_SIZE = 5;

    @Nullable private ValueStorage<float[]> ticksX;
    @Nullable private ValueStorage<float[]> ticksY;

    private int pointsNumber = DEFAULT_POINT_NUMBER;

    public D3Curve() {
        super();
        initInterpolator();
    }

    public D3Curve(T[] data) {
        super(data);
        initInterpolator();
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
                if (indexToUse == null || indexToUse.length != yData.length) {
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

    @Override public D3Curve<T> data(@NonNull T[] data) {
        this.data = data.clone();
        return this;
    }

    @Override public D3Curve<T> interpolator(@NonNull Interpolator interpolator) {
        super.interpolator(interpolator);
        return this;
    }

    @Override public D3Curve<T> onClickAction(@NonNull OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3Curve<T> onScrollAction(@NonNull OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3Curve<T> onPinchAction(@NonNull OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public void draw(@NonNull Canvas canvas) {
        if (data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        if (ticksX == null || ticksY == null) {
            throw new IllegalStateException(TICKS_ERROR);
        }

        if (data.length < 2) {
            return;
        }

        float[] xDraw = ticksX.getValue();
        float[] yDraw = ticksY.getValue();

        for (int i = 1; i < yDraw.length; i++) {
            canvas.drawLine(xDraw[i - 1], yDraw[i - 1], xDraw[i], yDraw[i], paint);
        }
    }

    @Override public void prepareParameters() {
        ticksX = new ValueStorage<>();
        ticksX.setValue(getTicksXRunnable());
        ticksY = new ValueStorage<>();
        ticksY.setValue(getTicksYRunnable());
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

    @NonNull private ValueRunnable<float[]> getTicksXRunnable() {
        return new ValueRunnable<float[]>() {
            @Override protected void computeValue() {
                float[] xData = x();
                float[] xDraw = new float[pointsNumber];
                xDraw[0] = xData[0];
                for (int i = 1; i < pointsNumber - 1; i++) {
                    xDraw[i] = ((pointsNumber - 1 - i) * xData[0] + i * xData[xData
                        .length - 1]) /
                        pointsNumber;
                }
                xDraw[pointsNumber - 1] = xData[xData.length - 1];
                value = xDraw;
            }
        };
    }

    @NonNull private ValueRunnable<float[]> getTicksYRunnable() {
        return new ValueRunnable<float[]>() {
            @Override protected void computeValue() {
                if (ticksX == null) {
                    throw new IllegalStateException(TICKS_X_ERROR);
                }
                float[] xData = x();
                float[] yData = y();
                float[] xDraw = ticksX.getValue();
                float[] yDraw = new float[pointsNumber];

                yDraw[0] = interpolator.interpolate(xDraw[0], xData, yData);

                for (int i = 1; i < pointsNumber - 1; i++) {
                    yDraw[i] = interpolator.interpolate(xDraw[i], xData, yData);
                }

                yDraw[pointsNumber - 1] = interpolator.interpolate(
                    xDraw[pointsNumber - 1], xData, yData
                );
                value = yDraw;
            }
        };
    }
}
