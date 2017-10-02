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

package com.applidium.pierreferrand.d3library.boxplot;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.mappers.D3FloatDataMapperFunction;
import com.applidium.pierreferrand.d3library.scale.D3Scale;
import com.applidium.pierreferrand.d3library.threading.ValueStorage;

@SuppressWarnings({"WeakerAccess", "unused"})
public class D3BoxPlot<T> extends D3Drawable {
    private static final String OFFSET_X_ERROR = "OffsetX should not be null";
    private static final String DATA_WIDTH_ERROR = "DataWidth should not be null";
    private static final String SCALE_ERROR = "Scale should not be null";
    private static final float DEFAULT_WIDTH = 50F;
    private static final float DEFAULT_OFFSET = 0F;

    @Nullable private D3FloatFunction dataWidth;
    @Nullable private D3FloatFunction offsetX;
    @Nullable private D3Scale<Float> scale;

    @Nullable D3FloatDataMapperFunction<T> dataMapper;

    @NonNull private final ValueStorage<Statistics> statistics = new ValueStorage<>();
    @NonNull private final StatisticsComputer<T> statisticsComputer
        = new StatisticsComputer<>(this);

    @Nullable T[] data;

    public D3BoxPlot() {
        setupDefaultValues();
    }

    public D3BoxPlot(@Nullable T[] data) {
        data(data);
        setupDefaultValues();
    }

    private void setupDefaultValues() {
        setupPaint();
        dataWidth(DEFAULT_WIDTH);
        offsetX(DEFAULT_OFFSET);
        scale(new D3Scale<>(new Float[]{0F, 1F}));
    }

    /**
     * If data had be given, returns it. Else returns null.
     */
    @Nullable public T[] data() {
        return data;
    }

    /**
     * Sets the data for the BoxPlot. Compute and update statistics.
     */
    public D3BoxPlot<T> data(@Nullable T[] data) {
        this.data = data;
        statisticsComputer.setDataLength(data == null ? 0 : data.length);
        return this;
    }

    /**
     * Returns the minimum that had been either computed or given.
     */
    public float min() {
        return statistics.getValue().min;
    }

    /**
     * Returns the maximum that had been either computed or given.
     */
    public float max() {
        return statistics.getValue().min;
    }

    /**
     * Returns the median that had been either computed or given.
     */
    public float median() {
        return statistics.getValue().median;
    }

    /**
     * Returns the lower quartile that had been either computed or given.
     */
    public float lowerQuartile() {
        return statistics.getValue().lowerQuartile;
    }

    /**
     * Returns the upper quartile that had been either computed or given.
     */
    public float upperQuartile() {
        return statistics.getValue().upperQuartile;
    }

    /**
     * Returns the Scale used by the BoxPlot.
     */
    @Nullable public D3Scale<Float> scale() {
        return scale;
    }

    /**
     * Sets the Scale used by the BoxPlot for drawing itself.
     */
    public D3BoxPlot<T> scale(@NonNull D3Scale<Float> scale) {
        this.scale = scale;
        return this;
    }

    /**
     * Returns the horizontal offset for the BoxPlot.
     */
    public float offsetX() {
        if (offsetX == null) {
            throw new IllegalStateException(OFFSET_X_ERROR);
        }
        return offsetX.getFloat();
    }

    /**
     * Sets the horizontal offset for the BoxPlot.
     */
    public D3BoxPlot<T> offsetX(final float offsetX) {
        this.offsetX = new D3FloatFunction() {
            @Override public float getFloat() {
                return offsetX;
            }
        };
        return this;
    }

    /**
     * Sets the horizontal offset for the BoxPlot.
     */
    public D3BoxPlot<T> offsetX(@NonNull D3FloatFunction offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    /**
     * Returns the width of the BoxPlot's core.
     */
    public float dataWidth() {
        if (dataWidth == null) {
            throw new IllegalStateException(DATA_WIDTH_ERROR);
        }
        return dataWidth.getFloat();
    }

    /**
     * Sets the width of the BoxPlot's core.
     */
    public D3BoxPlot<T> dataWidth(final float dataWidth) {
        this.dataWidth = new D3FloatFunction() {
            @Override public float getFloat() {
                return dataWidth;
            }
        };
        return this;
    }

    /**
     * Sets the width of the BoxPlot's core.
     */
    public D3BoxPlot<T> dataWidth(@NonNull D3FloatFunction dataWidth) {
        this.dataWidth = dataWidth;
        return this;
    }

    @Override public D3BoxPlot<T> onClickAction(@Nullable OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3BoxPlot<T> onScrollAction(@Nullable OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3BoxPlot<T> onPinchAction(@Nullable OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    public D3BoxPlot<T> dataMapper(@NonNull D3FloatDataMapperFunction<T> dataMapper) {
        this.dataMapper = dataMapper;
        return this;
    }

    @Override public D3BoxPlot<T> setClipRect(
        @NonNull D3FloatFunction leftLimit,
        @NonNull D3FloatFunction topLimit,
        @NonNull D3FloatFunction rightLimit,
        @NonNull D3FloatFunction bottomLimit
    ) {
        super.setClipRect(leftLimit, topLimit, rightLimit, bottomLimit);
        return this;
    }

    @Override public D3BoxPlot<T> deleteClipRect() {
        super.deleteClipRect();
        return this;
    }

    @Override public void prepareParameters() {
        if (lazyRecomputing && calculationNeeded() == 0) {
            return;
        }
        statistics.setValue(statisticsComputer);
    }

    @Override public D3BoxPlot<T> paint(@NonNull Paint paint) {
        super.paint(paint);
        paint.setStyle(Paint.Style.STROKE);
        return this;
    }

    @Override public D3BoxPlot<T> lazyRecomputing(boolean lazyRecomputing) {
        super.lazyRecomputing(lazyRecomputing);
        return this;
    }

    @Override public void draw(@NonNull Canvas canvas) {
        if (scale == null) {
            throw new IllegalStateException(SCALE_ERROR);
        }
        if (dataWidth == null) {
            throw new IllegalStateException(DATA_WIDTH_ERROR);
        }
        if (offsetX == null) {
            throw new IllegalStateException(OFFSET_X_ERROR);
        }
        Statistics stats = statistics.getValue();
        float realWidth = dataWidth.getFloat();
        float realOffsetX = offsetX.getFloat();


        canvas.drawLine(
            realOffsetX,
            stats.maxCoordinate,
            realOffsetX + realWidth,
            stats.maxCoordinate,
            paint
        );

        canvas.drawLine(
            realOffsetX,
            stats.minCoordinate,
            realOffsetX + realWidth,
            stats.minCoordinate,
            paint
        );

        canvas.drawLine(
            realOffsetX,
            stats.medianCoordinate,
            realOffsetX + realWidth,
            stats.medianCoordinate,
            paint
        );

        canvas.drawRect(
            realOffsetX,
            stats.upperQuartileCoordinate,
            realOffsetX + realWidth,
            stats.lowerQuartileCoordinate,
            paint
        );

        canvas.drawLine(
            realOffsetX + realWidth / 2F,
            stats.maxCoordinate,
            realOffsetX + realWidth / 2F,
            stats.upperQuartileCoordinate,
            paint
        );

        canvas.drawLine(
            realOffsetX + realWidth / 2F,
            stats.lowerQuartileCoordinate,
            realOffsetX + realWidth / 2F,
            stats.minCoordinate,
            paint
        );
    }
}
