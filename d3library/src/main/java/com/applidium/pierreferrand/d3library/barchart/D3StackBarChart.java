package com.applidium.pierreferrand.d3library.barchart;

import android.graphics.Canvas;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.Line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;

import java.util.ArrayList;
import java.util.List;

public class D3StackBarChart<T> extends D3Drawable {

    private List<D3BarChart<T>> barCharts;

    private D3FloatFunction dataWidth;

    public D3StackBarChart() {
        this(null);
    }

    public D3StackBarChart(T[] data, int stackNumber) {
        data(data, stackNumber);
    }

    public D3StackBarChart(T[][] data) {
        data(data);
    }

    public D3StackBarChart<T> data(T[] data, int stackNumber) {
        barCharts = new ArrayList<>();
        for (int i = 0; i < stackNumber; i++) {
            barCharts.add(new D3BarChart<>(data));
        }
        return this;
    }

    public D3StackBarChart<T> data(T[][] data) {
        barCharts = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            barCharts.add(new D3BarChart<>(data[i]));
        }
        return this;
    }

    public float dataWidth() {
        return dataWidth.getFloat();
    }

    public D3StackBarChart<T> dataWidth(final float dataWidth) {
        return dataWidth(new D3FloatFunction() {
            @Override public float getFloat() {
                return dataWidth;
            }
        });
    }

    public D3StackBarChart<T> dataWidth(D3FloatFunction dataWidth) {
        this.dataWidth = dataWidth;
        for (D3BarChart barChart : barCharts) {
            barChart.dataWidth(dataWidth);
        }
        return this;
    }

    public float[] x() {
        return barCharts.get(0).x();
    }

    public D3StackBarChart<T> x(D3DataMapperFunction<T> x) {
        for (D3BarChart<T> barChart : barCharts) {
            barChart.x(x);
        }
        return this;
    }

    public float[] y() {
        return barCharts.get(0).y();
    }

    public D3StackBarChart<T> y(final float y) {
        return y(new D3DataMapperFunction<T>() {
            @Override public float compute(T object, int position, T[] data) {
                return y;
            }
        });
    }

    public D3StackBarChart<T> y(D3DataMapperFunction<T> y) {
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

    public D3StackBarChart<T> dataHeight(final float[][] dataHeight) {
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

    public D3StackBarChart<T> dataHeight(List<D3DataMapperFunction<T>> dataHeight) {
        for (int i = 0; i < dataHeight.size(); i++) {
            barCharts.get(i).dataHeight(dataHeight.get(i));
        }
        return this;
    }

    public int[][] colors() {
        int[][] result = new int[barCharts.size()][];
        for (int i = 0; i < result.length; i++) {
            result[i] = barCharts.get(i).colors();
        }
        return result;
    }

    public D3StackBarChart<T> colors(int[][] colors) {
        for (int i = 0; i < barCharts.size(); i++) {
            barCharts.get(i).colors(colors[i % colors.length]);
        }
        return this;
    }

    @Override public D3StackBarChart<T> onClickAction(OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3StackBarChart<T> onScrollAction(OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3StackBarChart<T> onPinchAction(OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public void draw(Canvas canvas) {
        for (D3BarChart<T> barChart : barCharts) {
            barChart.draw(canvas);
        }
    }
}
