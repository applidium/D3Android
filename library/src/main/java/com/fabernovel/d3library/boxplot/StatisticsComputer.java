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

package com.fabernovel.d3library.boxplot;

import android.support.annotation.NonNull;

import com.fabernovel.d3library.threading.ValueRunnable;

class StatisticsComputer<T> extends ValueRunnable<Statistics> {
    private static final String DATA_ERROR = "Data should not be null";

    @NonNull private final D3BoxPlot<T> boxPlot;
    private float[] floatData;

    StatisticsComputer(@NonNull D3BoxPlot<T> boxPlot) {
        this.boxPlot = boxPlot;
        value = new Statistics();
    }

    void setDataLength(int length) {
        floatData = new float[length];
    }

    @Override protected void computeValue() {
        if (boxPlot.data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        computeFloatData();
        computeStatistics();
        computeCoordinates();
    }

    private void computeFloatData() {
        for (int i = 0; i < floatData.length; i++) {
            floatData[i] = boxPlot.dataMapper.compute(boxPlot.data[i], i, boxPlot.data);
        }
    }

    private void computeStatistics() {
        if (boxPlot.data.length == 0) {
            return;
        }
        int i = 0;
        float swap;
        while (i < boxPlot.data.length - 1) {
            if (floatData[i] > floatData[i + 1]) {
                swap = floatData[i];
                floatData[i] = floatData[i + 1];
                floatData[i + 1] = swap;
                i = Math.max(i - 1, 0);
            } else {
                i++;
            }
        }

        value.min = floatData[0];
        value.max = floatData[floatData.length - 1];
        value.median = floatData.length % 2 == 0 ?
            (floatData[floatData.length / 2 - 1] + floatData[floatData.length / 2]) / 2F :
            floatData[floatData.length / 2];

        value.lowerQuartile = floatData[Math.round((float) (floatData.length - 1)/ 4F)];
        value.upperQuartile = floatData[Math.round((float) (floatData.length - 1) * 3F / 4F)];
    }

    private void computeCoordinates() {
        value.minCoordinate = boxPlot.scale().value(value.min);
        value.maxCoordinate = boxPlot.scale().value(value.max);
        value.medianCoordinate = boxPlot.scale().value(value.median);
        value.lowerQuartileCoordinate = boxPlot.scale().value(value.lowerQuartile);
        value.upperQuartileCoordinate = boxPlot.scale().value(value.upperQuartile);
    }
}
