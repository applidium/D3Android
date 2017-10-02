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
