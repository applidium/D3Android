package com.applidium.pierreferrand.d3library.scale;

public interface Interpolator {
    float interpolate(
        float initialValue, float[] initialScope, float[] destinationScope
    );
}
