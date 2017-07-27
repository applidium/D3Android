package com.applidium.pierreferrand.d3library.arc;

import com.applidium.pierreferrand.d3library.mappers.D3IntDataMapperFunction;
import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

class ColorsRunnable<T> extends ValueRunnable<int[]> {
    private final D3Arc<T> arc;

    private D3IntDataMapperFunction<T> mapper;
    private boolean areSetLabels;

    ColorsRunnable(D3Arc<T> arc) {
        this.arc = arc;
    }

    void setDataLength(int length) {
        if (areSetLabels) {
            return;
        }
        value = new int[length];
    }

    void setColors(int[] labels) {
        areSetLabels = true;
        value = labels;
    }

    void setDataMapper(D3IntDataMapperFunction<T> mapper) {
        this.mapper = mapper;
        if (!areSetLabels) {
            return;
        }
        areSetLabels = false;
        setDataLength(arc.data == null ? 0 : arc.data.length);
    }

    @Override protected void computeValue() {
        if (areSetLabels) {
            return;
        }

        for (int i = 0; i < value.length; i++) {
            value[i] = mapper.compute(arc.data[i], i, arc.data);
        }
    }
}
