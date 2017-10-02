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

package com.applidium.pierreferrand.d3library.line;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.mappers.D3FloatDataMapperFunction;
import com.applidium.pierreferrand.d3library.threading.ThreadPool;
import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class CoordinatesValueStorage<T> extends ValueRunnable<float[]> {
    private static final String DATA_ERROR = "Data should not be null";

    @NonNull private final D3Line<T> line;
    @NonNull private final List<Callable<Object>> tasks;
    @NonNull private final List<ComputeValueRunnable> runnables;

    @Nullable private D3FloatDataMapperFunction<T> mapper;

    CoordinatesValueStorage(@NonNull D3Line<T> line) {
        this.line = line;
        tasks = new ArrayList<>();
        runnables = new ArrayList<>();
        for (int i = 0; i < ThreadPool.CORES_NUMBER; i++) {
            runnables.add(new ComputeValueRunnable(i));
        }
    }

    void setDataLength(int length) {
        value = new float[length];
    }

    void setMapper(@NonNull D3FloatDataMapperFunction<T> mapper) {
        this.mapper = mapper;
    }

    @Override protected void computeValue() {
        value = compute(mapper);
    }

    float[] compute(@NonNull D3FloatDataMapperFunction<T> mapper) {
        if (line.data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        tasks.clear();
        for (int k = 0; k < ThreadPool.CORES_NUMBER; k++) {
            tasks.add(Executors.callable(runnables.get(k)));
        }
        ThreadPool.executeOnSecondaryPool(tasks);
        return value;
    }

    private class ComputeValueRunnable implements Runnable {
        int k;

        public ComputeValueRunnable(int offset) {
            k = offset;
        }

        @Override public void run() {
            if (line.data == null) {
                throw new IllegalStateException(DATA_ERROR);
            }
            for (int i = k; i < value.length; i += ThreadPool.CORES_NUMBER) {
                value[i] = mapper.compute(line.data[i], i, line.data);
            }
        }
    }
}
