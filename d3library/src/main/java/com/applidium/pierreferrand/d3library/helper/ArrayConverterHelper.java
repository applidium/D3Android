package com.applidium.pierreferrand.d3library.helper;

public class ArrayConverterHelper {
    public static float[] convertArray(Float[] toConvert) {
        float[] result = new float[toConvert.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = toConvert[i] == null ? 0f : toConvert[i];
        }
        return result;
    }
}
