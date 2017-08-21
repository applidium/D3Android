package com.applidium.pierreferrand.d3library.helper;

import android.support.annotation.NonNull;

public final class ArrayConverterHelper {
    private static final String CONTAINER_ARRAY_IS_TOO_SMALL = "Container array is too small";

    private ArrayConverterHelper(){}

    public static float[] convertArray(@NonNull Float[] toConvert, @NonNull float[] resContainer) {
        if (resContainer.length < toConvert.length) {
            throw new IllegalStateException(CONTAINER_ARRAY_IS_TOO_SMALL);
        }
        for (int i = 0; i < toConvert.length; i++) {
            resContainer[i] = toConvert[i] == null ? 0F : toConvert[i];
        }
        return resContainer;
    }
}
