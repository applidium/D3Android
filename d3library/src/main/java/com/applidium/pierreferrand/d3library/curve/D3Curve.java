package com.applidium.pierreferrand.d3library.curve;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.applidium.pierreferrand.d3library.Line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.Line.D3Line;
import com.applidium.pierreferrand.d3library.scale.Interpolator;

public class D3Curve<T> extends D3Line<T> {
    private static final int DEFAULT_POINT_NUMBER = 100;

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

    @Override public Object[] data() {
        return data.clone();
    }

    @Override public D3Curve<T> data(T[] data) {
        this.data = data.clone();
        return this;
    }

    @Override public void draw(Canvas canvas) {
        if (data.length < 2) {
            return;
        }
        float[] xData = x();
        float[] yData = y();

        float[] xDraw = new float[pointsNumber];
        float[] yDraw = new float[pointsNumber];

        xDraw[0] = xData[0];
        yDraw[0] = interpolator.interpolate(xDraw[0], xData, yData);

        for (int i = 1; i < pointsNumber - 1; i++) {
            xDraw[i] = ((pointsNumber - 1 - i) * xData[0] + i * xData[xData.length - 1]) /
                pointsNumber;
            yDraw[i] = interpolator.interpolate(xDraw[i], xData, yData);
        }

        xDraw[pointsNumber - 1] = xData[yData.length - 1];
        yDraw[pointsNumber - 1] = interpolator.interpolate(xDraw[pointsNumber - 1], xData, yData);

        for (int i = 1; i < yDraw.length; i++) {
            canvas.drawLine(xDraw[i - 1], yDraw[i - 1], xDraw[i], yDraw[i], paint);
        }
    }
}
