package com.applidium.pierreferrand.d3library.barchart;

import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

class FloatsValueRunnable<T> extends ValueRunnable<float[]> {
    private static final String DATA_ERROR = "Data should not be null";

    @NonNull private final D3BarChart<T> barChart;
    @NonNull private D3DataMapperFunction<T> mapper;

    FloatsValueRunnable(@NonNull D3BarChart<T> barChart) {
        this.barChart = barChart;
    }

    void setDataLength(int length) {
        value = new float[length];
    }

    void setDataMapper(@NonNull D3DataMapperFunction<T> mapper) {
        this.mapper = mapper;
    }

    @Override protected void computeValue() {
        if (barChart.data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        for (int i = 0; i < value.length; i++) {
            value[i] = mapper.compute(barChart.data[i], i, barChart.data);
        }
    }
}
