package com.applidium.pierreferrand.d3library.arc;

import android.support.annotation.NonNull;

public class Angles {
    @NonNull float[] startAngles;
    @NonNull float[] drawAngles;

    Angles(int dataNumber) {
        startAngles = new float[dataNumber];
        drawAngles = new float[dataNumber];
    }
}
