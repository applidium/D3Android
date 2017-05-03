package com.applidium.pierreferrand.d3library.Line;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.scale.Interpolator;
import com.applidium.pierreferrand.d3library.scale.LinearInterpolator;
import com.applidium.pierreferrand.d3library.threading.ThreadPool;
import com.applidium.pierreferrand.d3library.threading.ValueRunnable;
import com.applidium.pierreferrand.d3library.threading.ValueStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class D3Line<T> extends D3Drawable {

    private ValueStorage<float[]> storeX;
    private ValueStorage<float[]> storeY;

    protected T[] data;
    private D3DataMapperFunction<T> x;
    private D3DataMapperFunction<T> y;

    protected Interpolator interpolator;

    public D3Line() {
        this(null);
    }

    public D3Line(T[] data) {
        this.data = data.clone();
        interpolator = new LinearInterpolator();
        setupPaint();
    }

    public float[] x() {
        if (storeX != null) {
            return storeX.getValue();
        }
        return compute(x);
    }

    private float[] compute(D3DataMapperFunction<T> mapper) {
        final float[] result = new float[data.length];

        List<Callable<Object>> tasks = new ArrayList<>();
        for (int k = 0; k < ThreadPool.coresNumber; k++) {
            buildTask(mapper, result, tasks, k);
        }
        ThreadPool.execute(tasks);
        return result;
    }

    private void buildTask(
        final D3DataMapperFunction<T> mapper,
        final float[] result,
        List<Callable<Object>> tasks,
        final int k
    ) {
        tasks.add(Executors.callable(
            new Runnable() {
                @Override public void run() {
                    for (int i = k; i < result.length; i += ThreadPool.coresNumber) {
                        result[i] = mapper.compute(data[i], i, data);
                    }
                }
            }
        ));
    }

    public D3Line<T> x(D3DataMapperFunction<T> function) {
        x = function;
        return this;
    }

    public float[] y() {
        if (storeY != null) {
            return storeY.getValue();
        }
        return compute(y);
    }

    public D3Line<T> y(D3DataMapperFunction<T> function) {
        y = function;
        return this;
    }

    public T[] data() {
        return data.clone();
    }

    public D3Line<T> data(T[] data) {
        this.data = data.clone();
        return this;
    }

    public Interpolator interpolator() {
        return interpolator;
    }

    public D3Line<T> interpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public float interpolateValue(float measuredX) {
        float[] x = storeX != null ? storeX.getValue() : x();
        float[] y = storeY != null ? storeY.getValue() : y();
        int index = 0;
        while (index < y.length - 2 && x[index + 1] < measuredX) {
            index++;
        }
        return interpolator.interpolate(
            measuredX,
            new float[]{x[index], x[index + 1]},
            new float[]{y[index], y[index + 1]}
        );
    }

    public D3Line<T> paint(Paint paint) {
        this.paint = paint;
        return this;
    }

    @Override public void draw(Canvas canvas) {
        if (data.length < 2) {
            return;
        }

        float[] x = storeX.getValue();
        float[] y = storeY.getValue();

        for (int i = 1; i < data.length; i++) {
            canvas.drawLine(x[i - 1], y[i - 1], x[i], y[i], paint);
        }
    }

    @Override public void prepareParameters() {
        final Object keyX = new Object();
        final Object keyY = new Object();
        storeX = new ValueStorage<>(buildRunnable(keyX, x), keyX);
        storeY = new ValueStorage<>(buildRunnable(keyY, y), keyY);
    }

    @Override public D3Line<T> onClickAction(OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3Line<T> onScrollAction(OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3Line<T> onPinchAction(OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @NonNull private ValueRunnable<float[]> buildRunnable(
        final Object key, final D3DataMapperFunction<T> mapper
    ) {
        return new ValueRunnable<float[]>() {
            float[] value;

            @Override public float[] getValue() {
                return value;
            }

            @Override public void run() {
                synchronized (key) {
                    value = compute(mapper);
                    key.notify();
                }
            }
        };
    }
}
