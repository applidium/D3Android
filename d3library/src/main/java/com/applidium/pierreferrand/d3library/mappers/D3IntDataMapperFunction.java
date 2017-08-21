package com.applidium.pierreferrand.d3library.mappers;

/**
 * Maps an int value to each value of an array.
 */
public interface D3IntDataMapperFunction<T> {
    /**
     * @param object The object to consider.
     * @param position The position of the object.
     * @param data The array of data.
     */
    int compute(T object, int position, T[] data);
}
