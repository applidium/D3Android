package com.applidium.pierreferrand.d3library.Line;

/**
 * Maps a float value to each value of an array.
 */
public interface D3DataMapperFunction<T> {
    /**
     * @param object The object to consider.
     * @param position The position of the object.
     * @param data The array of data.
     */
    float compute(T object, int position, T[] data);
}
