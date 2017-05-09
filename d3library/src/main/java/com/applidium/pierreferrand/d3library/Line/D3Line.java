package com.applidium.pierreferrand.d3library.line;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.scale.Interpolator;
import com.applidium.pierreferrand.d3library.scale.LinearInterpolator;
import com.applidium.pierreferrand.d3library.threading.ThreadPool;
import com.applidium.pierreferrand.d3library.threading.ValueRunnable;
import com.applidium.pierreferrand.d3library.threading.ValueStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public class D3Line<T> extends D3Drawable {
    private static final String X_ERROR = "X should not be null";
    private static final String DATA_ERROR = "Data should not be null";
    private static final String Y_ERROR = "Y should not be null";
    private static final String STORE_ERROR =
        "PrepareParameters should have be called to setup storeX and storeY";

    @Nullable private ValueStorage<float[]> storeX;
    @Nullable private ValueStorage<float[]> storeY;

    @Nullable protected T[] data;
    @Nullable private D3DataMapperFunction<T> x;
    @Nullable private D3DataMapperFunction<T> y;

    @NonNull protected Interpolator interpolator;

    public D3Line() {
        this(null);
    }

    public D3Line(@Nullable T[] data) {
        this.data = data != null ? data.clone() : null;
        interpolator = new LinearInterpolator();
        onClickAction(null);
        setupPaint();
    }

    /**
     * Returns an array with the horizontal coordinates of the points of the Line.
     */
    @NonNull public float[] x() {
        if (x == null) {
            throw new IllegalStateException(X_ERROR);
        }
        if (storeX != null) {
            return storeX.getValue();
        }
        return compute(x);
    }

    private float[] compute(@NonNull D3DataMapperFunction<T> mapper) {
        if (data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        final float[] result = new float[data.length];

        List<Callable<Object>> tasks = new ArrayList<>();
        for (int k = 0; k < ThreadPool.CORES_NUMBER; k++) {
            buildTask(mapper, result, tasks, k);
        }
        ThreadPool.execute(tasks);
        return result;
    }

    private void buildTask(
        @NonNull final D3DataMapperFunction<T> mapper,
        @NonNull final float[] result,
        @NonNull List<Callable<Object>> tasks,
        final int k
    ) {
        tasks.add(Executors.callable(
            new Runnable() {
                @Override public void run() {
                    if (data == null) {
                        throw new IllegalStateException(DATA_ERROR);
                    }
                    for (int i = k; i < result.length; i += ThreadPool.CORES_NUMBER) {
                        result[i] = mapper.compute(data[i], i, data);
                    }
                }
            }
        ));
    }

    /**
     * Sets an array with the horizontal coordinates of the points of the Line.
     */
    public D3Line<T> x(@NonNull D3DataMapperFunction<T> x) {
        this.x = x;
        return this;
    }

    /**
     * Returns an array with the vertical coordinates of the points of the Line.
     */
    @NonNull public float[] y() {
        if (y == null) {
            throw new IllegalStateException(Y_ERROR);
        }
        if (storeY != null) {
            return storeY.getValue();
        }
        return compute(y);
    }

    /**
     * Sets an array with the vertical coordinates of the points of the Line.
     */
    public D3Line<T> y(D3DataMapperFunction<T> y) {
        this.y = y;
        return this;
    }

    /**
     * Returns the data used by the Line.
     */
    @Nullable public T[] data() {
        return data != null ? data.clone() : null;
    }

    /**
     * Sets the data used by the Line.
     */
    public D3Line<T> data(@NonNull T[] data) {
        this.data = data.clone();
        return this;
    }

    /**
     * Returns the interpolator used to interpolate values from data.
     */
    @NonNull public Interpolator interpolator() {
        return interpolator;
    }

    /**
     * Sets the interpolator used to interpolate values from data.
     */
    public D3Line<T> interpolator(@NonNull Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    /**
     * Returns the float value, interpolated from the horizontal coordinate given.
     */
    public float interpolateValue(float measuredX) {
        float[] computedX = storeX != null ? storeX.getValue() : x();
        float[] computedY = storeY != null ? storeY.getValue() : y();
        int index = 0;
        while (index < computedY.length - 2 && computedX[index + 1] < measuredX) {
            index++;
        }
        return interpolator.interpolate(
            measuredX,
            new float[]{computedX[index], computedX[index + 1]},
            new float[]{computedY[index], computedY[index + 1]}
        );
    }

    @Override public D3Line<T> paint(@NonNull Paint paint) {
        super.paint(paint);
        return this;
    }

    @Override public void draw(@NonNull Canvas canvas) {
        if (data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        if (storeX == null || storeY == null) {
            throw new IllegalStateException(STORE_ERROR);
        }
        if (data.length < 2) {
            return;
        }

        float[] computedX = storeX.getValue();
        float[] computedY = storeY.getValue();

        for (int i = 1; i < data.length; i++) {
            canvas.drawLine(computedX[i - 1], computedY[i - 1], computedX[i], computedY[i], paint);
        }
    }

    @Override public void prepareParameters() {
        if (lazyRecomputing && calculationNeeded() == 0) {
            return;
        }
        storeX = new ValueStorage<>();
        storeX.setValue(buildRunnable(x));
        storeY = new ValueStorage<>();
        storeY.setValue(buildRunnable(y));
    }

    @Override public D3Line<T> setClipRect(
        @NonNull D3FloatFunction leftLimit,
        @NonNull D3FloatFunction topLimit,
        @NonNull D3FloatFunction rightLimit,
        @NonNull D3FloatFunction bottomLimit
    ) {
        super.setClipRect(leftLimit, topLimit, rightLimit, bottomLimit);
        return this;
    }

    @Override public D3Line<T> deleteClipRect() {
        super.deleteClipRect();
        return this;
    }

    @Override public D3Line<T> onClickAction(@Nullable OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3Line<T> onScrollAction(@Nullable OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3Line<T> onPinchAction(@Nullable OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public D3Line<T> lazyRecomputing(boolean lazyRecomputing) {
        super.lazyRecomputing(lazyRecomputing);
        return this;
    }

    @NonNull private ValueRunnable<float[]> buildRunnable(
        @NonNull final D3DataMapperFunction<T> mapper
    ) {
        return new ValueRunnable<float[]>() {
            @Override protected void computeValue() {
                value = compute(mapper);
            }
        };
    }
}
