package com.applidium.pierreferrand.d3library.axes;

import android.support.annotation.NonNull;

/**
 * Returns a range of T computed each time needed.
 */
public interface D3DomainFunction<T> {
    @NonNull T[] getRange();
}
