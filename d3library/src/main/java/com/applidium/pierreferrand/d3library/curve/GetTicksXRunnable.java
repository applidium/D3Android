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

package com.applidium.pierreferrand.d3library.curve;

import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

class GetTicksXRunnable<T> extends ValueRunnable<float[]> {
    private final D3Curve<T> curve;

    GetTicksXRunnable(D3Curve<T> curve) {
        this.curve = curve;
    }

    void onPointsNumberChange(int pointsNumber) {
        value = new float[pointsNumber];
    }

    @Override protected void computeValue() {
        float[] xData = curve.x();
        value[0] = xData[0];
        for (int i = 1; i < value.length - 1; i++) {
            value[i] = ((value.length - 1 - i) * xData[0] + i * xData[xData
                .length - 1]) /
                value.length;
        }
        value[value.length - 1] = xData[xData.length - 1];
    }
}
