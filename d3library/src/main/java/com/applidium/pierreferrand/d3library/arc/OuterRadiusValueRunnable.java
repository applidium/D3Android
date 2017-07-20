package com.applidium.pierreferrand.d3library.arc;

import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

public class OuterRadiusValueRunnable extends ValueRunnable<Float> {
    private final D3Arc arc;

    public OuterRadiusValueRunnable(D3Arc arc) {
        this.arc = arc;
    }

    @Override protected void computeValue() {
        value = arc.outerRadius.getFloat();
    }
}
