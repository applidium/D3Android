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

package com.fabernovel.d3library.barchart;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fabernovel.d3library.D3Drawable;
import com.fabernovel.d3library.action.OnClickAction;
import com.fabernovel.d3library.action.OnPinchAction;
import com.fabernovel.d3library.action.OnScrollAction;
import com.fabernovel.d3library.axes.D3FloatFunction;
import com.fabernovel.d3library.mappers.D3FloatDataMapperFunction;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "unchecked"})
public class D3StackBarChart<T> extends D3Drawable {
    private static final String DATA_WIDTH_ERROR = "DataWidth should not be null";

    @Nullable private D3FloatFunction dataWidth;

    public D3StackBarChart() {
        this(null);
    }

    public D3StackBarChart(@Nullable T[] data, int stackNumber) {
        data(data, stackNumber);
    }

    public D3StackBarChart(@Nullable T[][] data) {
        data(data);
    }

    /**
     * Sets the data for the different StackBar.
     *
     * @param stackNumber Defines the number of stack for the StackBarChart
     */
    public D3StackBarChart<T> data(@NonNull T[] data, int stackNumber) {
        children = new ArrayList<>(stackNumber);
        for (int i = 0; i < stackNumber; i++) {
            children.add(new D3BarChart<>(data));
        }
        return this;
    }

    /**
     * Sets the data for the different StackBar.
     */
    public D3StackBarChart<T> data(@Nullable T[][] data) {
        if (data == null) {
            children = new ArrayList<>(0);
            return this;
        }
        children = new ArrayList<>();
        for (T[] tab : data) {
            children.add(new D3BarChart<>(tab));
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
        for (D3Drawable barChart : children) {
            ((D3BarChart<T>) barChart).dataWidth(dataWidth);
        }
        return this;
    }

    /**
     * Returns the horizontal coordinates of the middle of the bars.
     */
    public float[] x() {
        return  ((D3BarChart<T>) children.get(0)).x();
    }

    /**
     * Sets horizontal coordinates of the middle of the bars.
     */
    public D3StackBarChart<T> x(@NonNull D3FloatDataMapperFunction<T> x) {
        for (D3Drawable barChart : children) {
            ((D3BarChart<T>) barChart).x(x);
        }
        return this;
    }

    /**
     * Returns the vertical coordinates of the bottom of the bars.
     */
    @NonNull public float[] y() {
        return ((D3BarChart<T>) children.get(0)).y();
    }

    /**
     * Sets the vertical coordinates of the bottom of the bars.
     */
    public D3StackBarChart<T> y(final float y) {
        return y(new D3FloatDataMapperFunction<T>() {
            @Override public float compute(T object, int position, T[] data) {
                return y;
            }
        });
    }

    /**
     * Sets the vertical coordinates of the bottom of the bars.
     */
    public D3StackBarChart<T> y(@NonNull D3FloatDataMapperFunction<T> y) {
        ((D3BarChart<T>) children.get(0)).y(y);
        for (int i = 1; i < children.size(); i++) {
            final int finalI = i;
            ((D3BarChart<T>) children.get(i)).y(new D3FloatDataMapperFunction<T>() {
                @Override public float compute(T object, int position, T[] data) {
                    return ((D3BarChart<T>) children.get(finalI - 1)).y()[position]
                        - ((D3BarChart<T>) children.get(finalI - 1)).dataHeight()[position];
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
            ((D3BarChart<T>) children.get(i)).dataHeight(new D3FloatDataMapperFunction<T>() {
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
    public D3StackBarChart<T> dataHeight(@NonNull List<D3FloatDataMapperFunction<T>> dataHeight) {
        for (int i = 0; i < dataHeight.size(); i++) {
            ((D3BarChart<T>) children.get(i)).dataHeight(dataHeight.get(i));
        }
        return this;
    }

    /**
     * Returns the colors used for the stacks of BarCharts. Each array are for a stack. For each
     * stack if there are more data than colors, the colors are used circularly.
     */
    @NonNull public int[][] colors() {
        int[][] result = new int[children.size()][];
        for (int i = 0; i < result.length; i++) {
            result[i] =  ((D3BarChart<T>) children.get(i)).colors();
        }
        return result;
    }

    /**
     * Sets the colors used for the stacks of BarCharts. Each array are for a stack. For each
     * stack if there are more data than colors, the colors are used circularly.
     */
    public D3StackBarChart<T> colors(@NonNull int[][] colors) {
        for (int i = 0; i < children.size(); i++) {
            ((D3BarChart<T>) children.get(i)).colors(colors[i % colors.length]);
        }
        return this;
    }

    @Override public D3StackBarChart<T> onClickAction(@Nullable OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3StackBarChart<T> onScrollAction(@Nullable OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3StackBarChart<T> onPinchAction(@Nullable OnPinchAction onPinchAction) {
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
        for (D3Drawable barChart : children) {
            barChart.draw(canvas);
        }
    }

    @Override public void prepareParameters() {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).prepareParameters();
        }
    }
}
