package com.applidium.pierreferrand.d3library.Line;

public interface D3DataMapperFunction<T> {
    float compute(T object, int position, T[] data);
}
