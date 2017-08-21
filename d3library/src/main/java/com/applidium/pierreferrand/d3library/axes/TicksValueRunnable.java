package com.applidium.pierreferrand.d3library.axes;

import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

class TicksValueRunnable<T> extends ValueRunnable<String[]> {
    private final D3Axis<T> axis;

    private boolean areCustomLegends;

     TicksValueRunnable(D3Axis<T> axis) {
        this.axis = axis;
    }

    void setCustomTicks(String[] legends) {
        areCustomLegends = true;
        value = legends;
    }

    void setTicksNumber(int ticksNumber) {
        areCustomLegends = false;
        value = new String[ticksNumber];
    }

    int getTicksNumber() {
        return value.length;
    }

    @Override protected void computeValue() {
        if (areCustomLegends) {
            return;
        }

        axis.scale.ticksLegend(getTicksNumber(), value);
    }
}
