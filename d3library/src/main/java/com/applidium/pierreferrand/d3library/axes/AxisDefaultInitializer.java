package com.applidium.pierreferrand.d3library.axes;

import com.applidium.pierreferrand.d3library.scale.D3Converter;

final class AxisDefaultInitializer {
    private AxisDefaultInitializer() {
    }

    static <T> D3Converter<T> getDefaultConverter(Class<T> tClass) {
        if (tClass.equals(Float.class)) {
            return new D3Converter<T>() {
                @Override public float convert(T toConvert) {
                    return (Float) toConvert;
                }

                @Override public T invert(float toInvert) {
                    return (T) new Float(toInvert);
                }
            };
        } else if (tClass.equals(Integer.class)) {
            return new D3Converter<T>() {
                @Override public float convert(T toConvert) {
                    return (Integer) toConvert;
                }

                @Override public T invert(float toInvert) {
                    return (T) Integer.valueOf((int) toInvert);
                }
            };
        }
        return null;
    }
}
