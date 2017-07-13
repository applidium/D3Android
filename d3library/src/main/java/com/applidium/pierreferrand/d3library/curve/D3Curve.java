package com.applidium.pierreferrand.d3library.curve;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.Line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.Line.D3Line;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.scale.Interpolator;
import com.applidium.pierreferrand.d3library.threading.ValueRunnable;
import com.applidium.pierreferrand.d3library.threading.ValueStorage;

public class D3Curve<T> extends D3Line<T> {
    private static final int DEFAULT_POINT_NUMBER = 100;
    ValueStorage<float[]> ticksX;
    ValueStorage<float[]> ticksY;

    private int pointsNumber = DEFAULT_POINT_NUMBER;

    public D3Curve() {
        super();
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

                return computeResult(x, xData, yData, indexToUse);
            }

            private void initIndex(int indexSize) {
                if (indexSize <= 5) {
                    indexToUse = new int[indexSize];
                    for (int i = 0; i < indexToUse.length; i++) {
                        indexToUse[i] = i;
                    }
                } else {
                    indexToUse = new int[]{
                        0,
                        (indexSize - 1) / 4,
                        (indexSize - 1) * 2 / 4,
                        (indexSize - 1) * 3 / 4,
                        (indexSize - 1)
                    };
                }
            }
        };
    }

    private float computeResult(
        float x, float[] xData, float[] yData, int[] indexToUse
    ) {
        float result = 0F;
        for (int i = 0; i < indexToUse.length; i++) {
            float intermediary = 1f;
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

    public D3Curve(T[] data) {
        super(data);
        initInterpolator();
    }

    @Override public float interpolateValue(float measuredX) {
        return interpolator.interpolate(measuredX, x(), y());
    }

    public int pointsNumber() {
        return pointsNumber;
    }

    public D3Curve<T> pointsNumber(int pointsNumber) {
        this.pointsNumber = pointsNumber;
        return this;
    }

    @Override public D3Curve<T> paint(Paint paint) {
        super.paint(paint);
        return this;
    }

    @Override public D3Curve<T> x(D3DataMapperFunction<T> range) {
        super.x(range);
        return this;
    }

    @Override public D3Curve<T> y(D3DataMapperFunction<T> range) {
        super.y(range);
        return this;
    }

    @Override public D3Curve<T> data(T[] data) {
        this.data = data.clone();
        return this;
    }

    @Override public D3Curve<T> interpolator(Interpolator interpolator) {
        super.interpolator(interpolator);
        return this;
    }

    @Override public D3Curve<T> onClickAction(OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3Curve<T> onScrollAction(OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3Curve<T> onPinchAction(OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public void draw(Canvas canvas) {
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
        super.prepareParameters();
        final Object keyX = new Object();
        final Object keyY = new Object();
        ticksX = new ValueStorage<>(getTicksXRunnable(keyX), keyX);
        ticksY = new ValueStorage<>(getTicksYRunnable(keyY), keyY);
    }

    @NonNull private ValueRunnable<float[]> getTicksXRunnable(final Object keyX) {
        return new ValueRunnable<float[]>() {
            float[] value;

            @Override public float[] getValue() {
                return value;
            }

            @Override public void run() {
                synchronized (keyX) {
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
                    keyX.notify();
                }
            }
        };
    }

    @NonNull private ValueRunnable<float[]> getTicksYRunnable(final Object keyY) {
        return new ValueRunnable<float[]>() {
            float[] value;

            @Override public float[] getValue() {
                return value;
            }

            @Override public void run() {
                synchronized (keyY) {
                    float[] xData = x();
                    float[] yData = y();

                    float[] xDraw = ticksX.getValue();
                    float[] yDraw = new float[pointsNumber];

                    yDraw[0] = interpolator.interpolate(xDraw[0], xData, yData);

                    for (int i = 1; i < pointsNumber - 1; i++) {
                        yDraw[i] = interpolator.interpolate(xDraw[i], xData, yData);
                    }

                    yDraw[pointsNumber - 1] = interpolator.interpolate(
                        xDraw[pointsNumber - 1],
                        xData,
                        yData
                    );
                    value = yDraw;
                    keyY.notify();
                }
            }
        };
    }
}
