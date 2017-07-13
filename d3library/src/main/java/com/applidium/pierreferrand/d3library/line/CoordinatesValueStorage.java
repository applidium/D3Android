package com.applidium.pierreferrand.d3library.line;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    @Nullable private D3DataMapperFunction<T> mapper;

    CoordinatesValueStorage(@NonNull D3Line<T> line) {
        this.line = line;
        tasks = new ArrayList<>();
    }

    void setDataLength(int length) {
        value = new float[length];
    }

    void setMapper(@NonNull D3DataMapperFunction<T> mapper) {
        this.mapper = mapper;
    }

    @Override protected void computeValue() {
        value = compute(mapper);
    }

    float[] compute(@NonNull D3DataMapperFunction<T> mapper) {
        if (line.data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        tasks.clear();
        for (int k = 0; k < ThreadPool.CORES_NUMBER; k++) {
            buildTask(mapper, k);
        }
        ThreadPool.execute(tasks);
        return value;
    }

    private void buildTask(
        @NonNull final D3DataMapperFunction<T> mapper,
        final int k
    ) {
        tasks.add(Executors.callable(
            new Runnable() {
                @Override public void run() {
                    if (line.data == null) {
                        throw new IllegalStateException(DATA_ERROR);
                    }
                    for (int i = k; i < value.length; i += ThreadPool.CORES_NUMBER) {
                        value[i] = mapper.compute(line.data[i], i, line.data);
                    }
                }
            }
        ));
    }
}
