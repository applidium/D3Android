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

package com.fabernovel.d3library.area;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fabernovel.d3library.action.OnClickAction;
import com.fabernovel.d3library.action.OnPinchAction;
import com.fabernovel.d3library.action.OnScrollAction;
import com.fabernovel.d3library.axes.D3FloatFunction;
import com.fabernovel.d3library.line.D3Line;
import com.fabernovel.d3library.mappers.D3FloatDataMapperFunction;
import com.fabernovel.d3library.scale.Interpolator;
import com.fabernovel.d3library.threading.ValueStorage;

public class D3Area<T> extends D3Line<T> {
    private static final String GROUND_ERROR = "Ground should not be null";
    private static final String DATA_ERROR = "Data should not be null";
    @Nullable D3FloatFunction ground;

    private final ValueStorage<Bitmap> bitmapValueStorage = new ValueStorage<>();
    private final AreaBitmapValueRunnable<T> bitmapValueRunnable =
        new AreaBitmapValueRunnable<>(this);

    public D3Area() {
        super();
    }

    public D3Area(@Nullable T[] data) {
        super(data);
        setupPaint();
    }

    @Override public D3Area<T> onClickAction(@Nullable OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3Area<T> onScrollAction(@Nullable OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3Area<T> onPinchAction(@Nullable OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public D3Area<T> setClipRect(
        @NonNull D3FloatFunction leftLimit,
        @NonNull D3FloatFunction topLimit,
        @NonNull D3FloatFunction rightLimit,
        @NonNull D3FloatFunction bottomLimit
    ) {
        super.setClipRect(leftLimit, topLimit, rightLimit, bottomLimit);
        return this;
    }

    @Override public D3Area<T> deleteClipRect() {
        super.deleteClipRect();
        return this;
    }

    @Override public D3Area<T> x(@NonNull D3FloatDataMapperFunction<T> x) {
        super.x(x);
        return this;
    }

    @Override public D3Area<T> y(@NonNull D3FloatDataMapperFunction<T> y) {
        super.y(y);
        return this;
    }

    @Override public D3Area<T> data(@NonNull T[] data) {
        super.data(data);
        return this;
    }

    @Override public D3Area<T> interpolator(@NonNull Interpolator interpolator) {
        super.interpolator(interpolator);
        return this;
    }

    @Override public D3Area<T> paint(@NonNull Paint paint) {
        super.paint(paint);
        paint.setStyle(Paint.Style.FILL);
        return this;
    }

    /**
     * Returns the ground of the area.
     */
    public float ground() {
        if (ground == null) {
            throw new IllegalStateException(GROUND_ERROR);
        }
        return ground.getFloat();
    }

    /**
     * Sets the ground of the area.
     */
    public D3Area<T> ground(@NonNull D3FloatFunction ground) {
        this.ground = ground;
        return this;
    }

    @Override protected void onDimensionsChange(float width, float height) {
        super.onDimensionsChange(width, height);
        bitmapValueRunnable.resizeBitmap(width, height);
    }

    @Override public void prepareParameters() {
        super.prepareParameters();
        if (lazyRecomputing && calculationNeeded() == 0) {
            return;
        }
        bitmapValueStorage.setValue(bitmapValueRunnable);
    }

    @Override public void draw(@NonNull Canvas canvas) {
        canvas.drawBitmap(bitmapValueStorage.getValue(), 0F, 0F, null);
    }

    @Override public D3Area<T> lazyRecomputing(boolean lazyRecomputing) {
        super.lazyRecomputing(lazyRecomputing);
        return this;
    }
}
