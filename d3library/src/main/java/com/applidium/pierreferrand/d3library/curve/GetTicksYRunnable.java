package com.applidium.pierreferrand.d3library.curve;

import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

class GetTicksYRunnable<T> extends ValueRunnable<float[]> {
    private final D3Curve<T> curve;

    GetTicksYRunnable(D3Curve<T> curve) {
        this.curve = curve;
    }

    void onPointsNumberChange(int pointsNumber) {
        value = new float[pointsNumber];
    }

    @Override protected void computeValue() {
        float[] xData = curve.x();
        float[] yData = curve.y();
        float[] xDraw = curve.ticksX.getValue();

        value[0] = curve.interpolator().interpolate(xDraw[0], xData, yData);

        for (int i = 1; i < value.length - 1; i++) {
            value[i] = curve.interpolator().interpolate(xDraw[i], xData, yData);
        }

        value[value.length - 1] = curve.interpolator().interpolate(
            xDraw[value.length - 1], xData, yData
        );
    }
}
