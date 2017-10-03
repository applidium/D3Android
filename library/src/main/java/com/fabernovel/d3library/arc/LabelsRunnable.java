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

package com.fabernovel.d3library.arc;

import com.fabernovel.d3library.mappers.D3StringDataMapperFunction;
import com.fabernovel.d3library.threading.ValueRunnable;

class LabelsRunnable<T> extends ValueRunnable<String[]> {
    private final D3Arc<T> arc;

    private D3StringDataMapperFunction<T> mapper;
    private boolean areSetLabels;

    LabelsRunnable(D3Arc<T> arc) {
        this.arc = arc;
    }

    void setDataLength(int length) {
        if (areSetLabels) {
            return;
        }
        value = new String[length];
    }

    void setLabels(String[] labels) {
        areSetLabels = true;
        value = labels;
    }

    void setDataMapper(D3StringDataMapperFunction<T> mapper) {
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
