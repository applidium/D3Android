package com.applidium.pierreferrand.d3library.scale;

public class LinearInterpolator implements Interpolator {
    @Override public float interpolate(
        float initialValue, float[] initialScope, float[] destinationScope
    ) {
        if (destinationScope[0] == destinationScope[1]) {
            return destinationScope[0];
        }
        float proportion = (initialValue - initialScope[0]) / (initialScope[1] - initialScope[0]);
        return (1 - proportion) * destinationScope[0] + proportion * destinationScope[1];
    }
}
