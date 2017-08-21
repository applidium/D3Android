package com.applidium.pierreferrand.d3library.line;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.action.PinchType;
import com.applidium.pierreferrand.d3library.action.ScrollDirection;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.mappers.D3FloatDataMapperFunction;
import com.applidium.pierreferrand.d3library.scale.Interpolator;
import com.applidium.pierreferrand.d3library.scale.LinearInterpolator;
import com.applidium.pierreferrand.d3library.threading.ValueStorage;

@SuppressWarnings("unused")
public class D3Line<T> extends D3Drawable {
    private static final String X_ERROR = "X should not be null";
    private static final String DATA_ERROR = "Data should not be null";
    private static final String Y_ERROR = "Y should not be null";
    private static final String STORE_ERROR =
        "PrepareParameters should have be called to setup storeX and storeY";

    @NonNull protected final ValueStorage<float[]> storeX;
    @NonNull private final CoordinatesValueStorage<T> xValueStorage;
    @NonNull protected final ValueStorage<float[]> storeY;
    @NonNull private final CoordinatesValueStorage<T> yValueStorage;
    @NonNull protected float[] lines;

    @Nullable protected T[] data;
    @Nullable private D3FloatDataMapperFunction<T> x;
    @Nullable private D3FloatDataMapperFunction<T> y;

    @NonNull protected Interpolator interpolator;

    public D3Line() {
        this(null);
    }

    public D3Line(@Nullable T[] data) {
        storeX = new ValueStorage<>();
        storeY = new ValueStorage<>();
        xValueStorage = new CoordinatesValueStorage<>(this);
        yValueStorage = new CoordinatesValueStorage<>(this);

        data(data);
        interpolator = new LinearInterpolator();
        onClickAction(null);
        setupPaint();
        setupActions();
    }

    private void setupActions() {
        onScrollAction(new OnScrollAction() {
            @Override public void onScroll(
                ScrollDirection direction, float coordinateX, float coordinateY, float dX, float dY
            ) {
                updateNeeded();
            }
        });
        onPinchAction(new OnPinchAction() {
            @Override public void onPinch(
                PinchType pinchType, float coordinateStaticX, float coordinateStaticY,
                float coordinateMobileX, float coordinateMobileY, float dX, float dY
            ) {
                updateNeeded();
            }
        });
    }

    /**
     * Returns an array with the horizontal coordinates of the points of the Line.
     */
    @NonNull public float[] x() {
        if (x == null) {
            throw new IllegalStateException(X_ERROR);
        }
        return storeX.getValue();
    }

    /**
     * Sets an array with the horizontal coordinates of the points of the Line.
     */
    public D3Line<T> x(@NonNull D3FloatDataMapperFunction<T> x) {
        this.x = x;
        xValueStorage.setMapper(x);
        return this;
    }

    /**
     * Returns an array with the vertical coordinates of the points of the Line.
     */
    @NonNull public float[] y() {
        if (y == null) {
            throw new IllegalStateException(Y_ERROR);
        }
        return storeY.getValue();
    }

    /**
     * Sets an array with the vertical coordinates of the points of the Line.
     */
    public D3Line<T> y(D3FloatDataMapperFunction<T> y) {
        this.y = y;
        yValueStorage.setMapper(y);
        return this;
    }

    /**
     * Returns the data used by the Line.
     */
    @Nullable public T[] data() {
        return data;
    }

    protected void setDataStorageDataLength(int length) {
        xValueStorage.setDataLength(length);
        yValueStorage.setDataLength(length);
    }

    /**
     * Sets the data used by the Line.
     */
    public D3Line<T> data(@Nullable T[] data) {
        this.data = data;
        if (data == null) {
            setDataStorageDataLength(0);
            lines = new float[0];
            return this;
        }
        setDataStorageDataLength(data.length);
        lines = new float[4 * (data.length - 1)];
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
        float[] computedX = storeX.getValue();
        float[] computedY = storeY.getValue();
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
        paint.setStyle(Paint.Style.STROKE);
        return this;
    }

    @Override public void draw(@NonNull Canvas canvas) {
        if (data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        if (data.length < 2) {
            return;
        }
        float[] computedX = storeX.getValue();
        float[] computedY = storeY.getValue();
        for (int i = 0; i < data.length - 1; i++) {
            lines[i * 4] = computedX[i];
            lines[i * 4 + 1] = computedY[i];
            lines[i * 4 + 2] = computedX[i + 1];
            lines[i * 4 + 3] = computedY[i + 1];
        }
        canvas.drawLines(lines, paint);
    }

    @Override public void prepareParameters() {
        if (lazyRecomputing && calculationNeeded() == 0) {
            return;
        }
        storeX.setValue(xValueStorage);
        storeY.setValue(yValueStorage);
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
}
