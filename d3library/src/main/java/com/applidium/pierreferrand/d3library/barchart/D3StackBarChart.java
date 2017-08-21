package com.applidium.pierreferrand.d3library.barchart;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.Line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;

import java.util.ArrayList;
import java.util.List;

public class D3StackBarChart<T> extends D3Drawable {
    private static final String DATA_WIDTH_ERROR = "DataWidth should not be null";

    @NonNull private List<D3BarChart<T>> barCharts;
    @Nullable private D3FloatFunction dataWidth;

    public D3StackBarChart() {
        this(null);
    }

    public D3StackBarChart(@NonNull T[] data, int stackNumber) {
        data(data, stackNumber);
    }

    public D3StackBarChart(@NonNull T[][] data) {
        data(data);
    }

    /**
     * Sets the data for the different StackBar.
     *
     * @param stackNumber Defines the number of stack for the StackBarChart
     */
    public D3StackBarChart<T> data(@NonNull T[] data, int stackNumber) {
        barCharts = new ArrayList<>();
        for (int i = 0; i < stackNumber; i++) {
            barCharts.add(new D3BarChart<>(data));
        }
        return this;
    }

    /**
     * Sets the data for the different StackBar.
     */
    public D3StackBarChart<T> data(@NonNull T[][] data) {
        barCharts = new ArrayList<>();
        for (T[] tab : data) {
            barCharts.add(new D3BarChart<>(tab));
        }
        return this;
    }

    /**
     * Returns the width of the bars.
     */
    public float dataWidth() {
        if (dataWidth == null) {
            throw new IllegalStateException(DATA_WIDTH_ERROR);
        }
        return dataWidth.getFloat();
    }

    /**
     * Sets the width of the bars.
     */
    public D3StackBarChart<T> dataWidth(final float dataWidth) {
        return dataWidth(new D3FloatFunction() {
            @Override public float getFloat() {
                return dataWidth;
            }
        });
    }

    /**
     * Sets the width of the bars.
     */
    public D3StackBarChart<T> dataWidth(@NonNull D3FloatFunction dataWidth) {
        this.dataWidth = dataWidth;
        for (D3BarChart barChart : barCharts) {
            barChart.dataWidth(dataWidth);
        }
        return this;
    }

    /**
     * Returns the horizontal coordinates of the middle of the bars.
     */
    public float[] x() {
        return barCharts.get(0).x();
    }

    /**
     * Sets horizontal coordinates of the middle of the bars.
     */
    public D3StackBarChart<T> x(@NonNull D3DataMapperFunction<T> x) {
        for (D3BarChart<T> barChart : barCharts) {
            barChart.x(x);
        }
        return this;
    }

    /**
     * Returns the vertical coordinates of the bottom of the bars.
     */
    @NonNull public float[] y() {
        return barCharts.get(0).y();
    }

    /**
     * Sets the vertical coordinates of the bottom of the bars.
     */
    public D3StackBarChart<T> y(final float y) {
        return y(new D3DataMapperFunction<T>() {
            @Override public float compute(T object, int position, T[] data) {
                return y;
            }
        });
    }

    /**
     * Sets the vertical coordinates of the bottom of the bars.
     */
    public D3StackBarChart<T> y(@NonNull D3DataMapperFunction<T> y) {
        barCharts.get(0).y(y);
        for (int i = 1; i < barCharts.size(); i++) {
            final int finalI = i;
            barCharts.get(i).y(new D3DataMapperFunction<T>() {
                @Override public float compute(T object, int position, T[] data) {
                    return barCharts.get(finalI - 1).y()[position]
                        - barCharts.get(finalI - 1).dataHeight()[position];
                }
            });
        }
        return this;
    }

    /**
     * Sets the different heights for the stacks of BarChart.
     */
    public D3StackBarChart<T> dataHeight(@NonNull final float[][] dataHeight) {
        for (int i = 0; i < dataHeight.length; i++) {
            final int finalI = i;
            barCharts.get(i).dataHeight(new D3DataMapperFunction<T>() {
                private float[] heights = dataHeight[finalI];

                @Override public float compute(T object, int position, T[] data) {
                    return heights[position];
                }
            });
        }
        return this;
    }

    /**
     * Sets the different heights for the stacks of BarChart.
     */
    public D3StackBarChart<T> dataHeight(@NonNull List<D3DataMapperFunction<T>> dataHeight) {
        for (int i = 0; i < dataHeight.size(); i++) {
            barCharts.get(i).dataHeight(dataHeight.get(i));
        }
        return this;
    }

    /**
     * Returns the colors used for the stacks of BarCharts. Each array are for a stack. For each
     * stack if there are more data than colors, the colors are used circularly.
     */
    @NonNull public int[][] colors() {
        int[][] result = new int[barCharts.size()][];
        for (int i = 0; i < result.length; i++) {
            result[i] = barCharts.get(i).colors();
        }
        return result;
    }

    /**
     * Sets the colors used for the stacks of BarCharts. Each array are for a stack. For each
     * stack if there are more data than colors, the colors are used circularly.
     */
    public D3StackBarChart<T> colors(@NonNull int[][] colors) {
        for (int i = 0; i < barCharts.size(); i++) {
            barCharts.get(i).colors(colors[i % colors.length]);
        }
        return this;
    }

    @Override public D3StackBarChart<T> onClickAction(@NonNull OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3StackBarChart<T> onScrollAction(@NonNull OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3StackBarChart<T> onPinchAction(@NonNull OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public D3StackBarChart<T> setClipRect(
        @NonNull D3FloatFunction leftLimit,
        @NonNull D3FloatFunction topLimit,
        @NonNull D3FloatFunction rightLimit,
        @NonNull D3FloatFunction bottomLimit
    ) {
        super.setClipRect(leftLimit, topLimit, rightLimit, bottomLimit);
        return this;
    }

    @Override public D3StackBarChart<T> deleteClipRect() {
        super.deleteClipRect();
        return this;
    }

    @Override public void draw(@NonNull Canvas canvas) {
        for (D3BarChart<T> barChart : barCharts) {
            barChart.draw(canvas);
        }
    }
}
