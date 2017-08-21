package com.applidium.pierreferrand.d3library.area;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.line.D3Line;
import com.applidium.pierreferrand.d3library.scale.Interpolator;
import com.applidium.pierreferrand.d3library.threading.ValueStorage;

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

    @Override public D3Area<T> x(@NonNull D3DataMapperFunction<T> x) {
        super.x(x);
        return this;
    }

    @Override public D3Area<T> y(@NonNull D3DataMapperFunction<T> y) {
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
}
