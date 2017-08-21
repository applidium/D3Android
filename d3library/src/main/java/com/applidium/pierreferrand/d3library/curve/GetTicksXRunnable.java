package com.applidium.pierreferrand.d3library.curve;

import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

class GetTicksXRunnable<T> extends ValueRunnable<float[]> {
    private final D3Curve<T> curve;

    GetTicksXRunnable(D3Curve<T> curve) {
        this.curve = curve;
    }

    void onPointsNumberChange(int pointsNumber) {
        value = new float[pointsNumber];
    }

    @Override protected void computeValue() {
        float[] xData = curve.x();
        value[0] = xData[0];
        for (int i = 1; i < value.length - 1; i++) {
            value[i] = ((value.length - 1 - i) * xData[0] + i * xData[xData
                .length - 1]) /
                value.length;
        }
        value[value.length - 1] = xData[xData.length - 1];
    }
}
