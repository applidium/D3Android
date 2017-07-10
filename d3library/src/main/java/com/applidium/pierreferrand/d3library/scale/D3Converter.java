package com.applidium.pierreferrand.d3library.scale;

public interface D3Converter<T> {
    float convert(T toConvert);
    T invert(float toInvert);
}
