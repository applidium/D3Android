/*
 * Copyright 2017, Fabernovel Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.applidium.pierreferrand.d3library.barchart;

import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.mappers.D3FloatDataMapperFunction;
import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

class FloatsValueRunnable<T> extends ValueRunnable<float[]> {
    private static final String DATA_ERROR = "Data should not be null";

    @NonNull private final D3BarChart<T> barChart;
    @NonNull private D3FloatDataMapperFunction<T> mapper;

    FloatsValueRunnable(@NonNull D3BarChart<T> barChart) {
        this.barChart = barChart;
    }

    void setDataLength(int length) {
        value = new float[length];
    }

    void setDataMapper(@NonNull D3FloatDataMapperFunction<T> mapper) {
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
