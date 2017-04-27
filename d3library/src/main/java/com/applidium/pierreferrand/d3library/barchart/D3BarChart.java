package com.applidium.pierreferrand.d3library.barchart;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.Line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;

public class D3BarChart<T> extends D3Drawable {

    private int[] colors = new int[]{0xFF0000FF};

    private T[] data;

    private D3DataMapperFunction<T> dataHeight;
    private D3FloatFunction dataWidth;

    /***
     * Define the abscissa of the middle of the bar
     */
    private D3DataMapperFunction<T> x;
    /***
     * Define the ordinate of the bottom of the bar.
     */
    private D3DataMapperFunction<T> y;

    private Paint paint;

    public D3BarChart() {
        this(null);
    }

    public D3BarChart(T[] data) {
        data(data);
        setupPaint();
    }

    private void setupPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
    }

    public T[] data() {
        return data.clone();
    }

    public D3BarChart<T> data(T[] data) {
        this.data = data.clone();
        return this;
    }

    public float[] dataHeight() {
        float[] result = new float[data.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = dataHeight.compute(data[i], i, data);
        }
        return result;
    }

    public D3BarChart<T> dataHeight(D3DataMapperFunction<T> dataHeight) {
        this.dataHeight = dataHeight;
        return this;
    }

    public float dataWidth() {
        return dataWidth.getFloat();
    }

    public D3BarChart<T> dataWidth(final float dataWidth) {
        this.dataWidth = new D3FloatFunction() {
            @Override public float getFloat() {
                return dataWidth;
            }
        };
        return this;
    }

    public D3BarChart<T> dataWidth(D3FloatFunction dataWidth) {
        this.dataWidth = dataWidth;
        return this;
    }

    public float[] x() {
        float[] result = new float[data.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = x.compute(data[i], i, data);
        }
        return result;
    }

    public D3BarChart<T> x(D3DataMapperFunction<T> x) {
        this.x = x;
        return this;
    }

    public D3BarChart<T> x(final float x) {
        this.x = new D3DataMapperFunction<T>() {
            @Override public float compute(T object, int position, T[] data) {
                return x;
            }
        };
        return this;
    }

    public float[] y() {
        float[] result = new float[data.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = y.compute(data[i], i, data);
        }
        return result;
    }

    public D3BarChart<T> y(final float[] y) {
        this.y = new D3DataMapperFunction<T>() {
            private final float[] data = y;

            @Override public float compute(T object, int position, T[] data) {
                return y[position];
            }
        };
        return this;
    }

    public D3BarChart<T> y(D3DataMapperFunction<T> y) {
        this.y = y;
        return this;
    }

    public int[] colors() {
        return colors;
    }

    public D3BarChart<T> colors(int[] colors) {
        this.colors = colors;
        return this;
    }

    @Override public D3BarChart<T> onClickAction(OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3BarChart<T> onScrollAction(OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3BarChart<T> onPinchAction(OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public void draw(Canvas canvas) {
        float width = dataWidth();
        float[] height = dataHeight();
        float[] x = x();
        float[] y = y();

        for (int i = 0; i < data.length; i++) {
            paint.setColor(colors[i % colors.length]);
            canvas.drawRect(
                x[i] - width / 2f,
                y[i] - height[i],
                x[i] + width / 2f,
                y[i],
                paint
            );
        }
    }
}
