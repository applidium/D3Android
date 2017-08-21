package com.applidium.pierreferrand.d3library.scale;

import com.applidium.pierreferrand.d3library.axes.D3DomainFunction;

class WrapperDomainFunction<T> implements D3DomainFunction<T> {
    private T[] data;

    WrapperDomainFunction(T[] data) {
        setData(data);
    }

    void setData(T[] data) {
        this.data = data;
    }

    @Override public T[] getRange() {
        return data;
    }
}
