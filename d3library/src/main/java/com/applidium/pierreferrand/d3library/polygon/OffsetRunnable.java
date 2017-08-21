package com.applidium.pierreferrand.d3library.polygon;

import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

class OffsetRunnable extends ValueRunnable<Float> {
    private D3FloatFunction function;

    void setOffsetFunction(D3FloatFunction function) {
        this.function = function;
    }

    @Override protected void computeValue() {
        value = function.getFloat();
    }
}
