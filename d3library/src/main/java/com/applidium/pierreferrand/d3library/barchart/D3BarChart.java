package com.applidium.pierreferrand.d3library.barchart;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.threading.ValueStorage;

public class D3BarChart<T> extends D3Drawable {
    private static final String DATA_ERROR = "Data should not be null";
    private static final String DATA_WIDTH_ERROR = "DataWidth should not be null";
    @NonNull private int[] colors = new int[]{0xFF0000FF};

    @Nullable T[] data;

    @Nullable private D3FloatFunction dataWidth;

    @NonNull private final ValueStorage<float[]> heightValueStorage = new ValueStorage<>();
    @NonNull private final FloatsValueRunnable<T> heightValueRunnable
        = new FloatsValueRunnable<>(this);

    @NonNull private final ValueStorage<float[]> xValueStorage = new ValueStorage<>();
    @NonNull private final FloatsValueRunnable<T> xValueRunnable
        = new FloatsValueRunnable<>(this);
    @NonNull private final ValueStorage<float[]> yValueStorage = new ValueStorage<>();
    @NonNull private final FloatsValueRunnable<T> yValueRunnable
        = new FloatsValueRunnable<>(this);


    public D3BarChart() {
        this(null);
    }

    public D3BarChart(@Nullable T[] data) {
        data(data);
        setupPaint();
    }

    /**
     * Returns the data of the BarChart.
     */
    @Nullable public T[] data() {
        return data != null ? data.clone() : null;
    }

    /**
     * Sets the data of the BarChart.
     */
    public D3BarChart<T> data(@Nullable T[] data) {
        this.data = data != null ? data.clone() : null;
        xValueRunnable.setDataLength(data == null ? 0 : data.length);
        yValueRunnable.setDataLength(data == null ? 0 : data.length);
        heightValueRunnable.setDataLength(data == null ? 0 : data.length);
        return this;
    }

    /**
     * Returns an array with the heights of the BarChart's data representation.
     */
    @NonNull public float[] dataHeight() {
        return heightValueStorage.getValue();
    }

    /**
     * Sets the heights of the BarChart's data representation.
     */
    public D3BarChart<T> dataHeight(@NonNull D3DataMapperFunction<T> dataHeight) {
        heightValueRunnable.setDataMapper(dataHeight);
        return this;
    }

    /**
     * Returns the width of the BarChart's data representation.
     */
    public float dataWidth() {
        if (dataWidth == null) {
            throw new IllegalStateException(DATA_WIDTH_ERROR);
        }
        return dataWidth.getFloat();
    }

    /**
     * Sets the width of the BarChart's data representation.
     */
    public D3BarChart<T> dataWidth(final float dataWidth) {
        dataWidth(new D3FloatFunction() {
            @Override public float getFloat() {
                return dataWidth;
            }
        });
        return this;
    }

    /**
     * Sets the width of the BarChart's data representation.
     */
    public D3BarChart<T> dataWidth(@NonNull D3FloatFunction dataWidth) {
        this.dataWidth = dataWidth;
        return this;
    }

    /**
     * Returns an array with the horizontal coordinate of the middle of the bar for each
     * of the BarChart's data representation.
     */
    @NonNull public float[] x() {
        return xValueStorage.getValue();
    }

    /**
     * Sets horizontal coordinates of the middle of the bar for each
     * of the BarChart's data representation.
     */
    public D3BarChart<T> x(@NonNull D3DataMapperFunction<T> x) {
        xValueRunnable.setDataMapper(x);
        return this;
    }

    /**
     * Sets horizontal coordinates of the middle of the bar for each
     * of the BarChart's data representation.
     */
    public D3BarChart<T> x(@NonNull final float x[]) {
        x(new D3DataMapperFunction<T>() {
            @Override public float compute(T object, int position, T[] data) {
                return x[position];
            }
        });
        return this;
    }

    /**
     * Returns an array with the vertical coordinate of the bottom of the bar for each
     * of the BarChart's data representation.
     */
    @NonNull public float[] y() {
        return yValueStorage.getValue();
    }

    /**
     * Sets vertical coordinates of the bottom of the bar for each
     * of the BarChart's data representation.
     */
    public D3BarChart<T> y(@NonNull final float[] y) {
        y(new D3DataMapperFunction<T>() {
            @Override public float compute(T object, int position, T[] data) {
                return y[position];
            }
        });
        return this;
    }

    /**
     * Sets vertical coordinates of the bottom of the bar for each
     * of the BarChart's data representation.
     */
    public D3BarChart<T> y(@NonNull D3DataMapperFunction<T> y) {
        yValueRunnable.setDataMapper(y);
        return this;
    }

    /**
     * Returns an array with the list of colors used for the data representation. If there are
     * more data to represent than colors, colors are used circularly.
     */
    @NonNull public int[] colors() {
        return colors;
    }

    /**
     * Sets the colors used for the data representation. If there are
     * more data to represent than colors, colors are used circularly.
     */
    public D3BarChart<T> colors(@NonNull int[] colors) {
        this.colors = colors;
        return this;
    }

    @Override public D3BarChart<T> onClickAction(@NonNull OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3BarChart<T> onScrollAction(@NonNull OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3BarChart<T> onPinchAction(@NonNull OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public D3BarChart<T> setClipRect(
        @NonNull D3FloatFunction leftLimit,
        @NonNull D3FloatFunction topLimit,
        @NonNull D3FloatFunction rightLimit,
        @NonNull D3FloatFunction bottomLimit
    ) {
        super.setClipRect(leftLimit, topLimit, rightLimit, bottomLimit);
        return this;
    }

    @Override public D3BarChart<T> deleteClipRect() {
        super.deleteClipRect();
        return this;
    }

    @Override public void prepareParameters() {
        if (lazyRecomputing && calculationNeeded() == 0) {
            return;
        }
        xValueStorage.setValue(xValueRunnable);
        yValueStorage.setValue(yValueRunnable);
        heightValueStorage.setValue(heightValueRunnable);
    }

    @Override public void draw(@NonNull Canvas canvas) {
        if (data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        float width = dataWidth();
        float[] height = dataHeight();
        float[] computedX = x();
        float[] computedY = y();

        for (int i = 0; i < data.length; i++) {
            paint.setColor(colors[i % colors.length]);
            canvas.drawRect(
                computedX[i] - width / 2F,
                computedY[i] - height[i],
                computedX[i] + width / 2F,
                computedY[i],
                paint
            );
        }
    }
}
