package com.applidium.pierreferrand.d3library.helper;

public final class ArrayConverterHelper {
    private ArrayConverterHelper(){}

    public static float[] convertArray(Float[] toConvert) {
        float[] result = new float[toConvert.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = toConvert[i] == null ? 0F : toConvert[i];
        }
        return result;
    }
}
