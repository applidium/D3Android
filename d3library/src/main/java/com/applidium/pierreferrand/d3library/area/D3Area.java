package com.applidium.pierreferrand.d3library.area;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.Line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.Line.D3Line;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.scale.Interpolator;

public class D3Area<T> extends D3Line<T> {
    private static final String GROUND_ERROR = "Ground should not be null";
    private static final String DATA_ERROR = "Data should not be null";
    @Nullable private D3FloatFunction ground;

    public D3Area() {
        super();
    }

    public D3Area(@Nullable T[] data) {
        super(data);
        setupPaint();
    }

    @Override public D3Area<T> onClickAction(@NonNull OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3Area<T> onScrollAction(@NonNull OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3Area<T> onPinchAction(@NonNull OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
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

    @Override public void draw(@NonNull Canvas canvas) {
        if (data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        if (data.length < 2) {
            return;
        }
        if (ground == null) {
            throw new IllegalStateException(GROUND_ERROR);
        }

        float[] x = x();
        float[] y = y();
        float computedGrounded = ground.getFloat();

        Path path = new Path();
        path.moveTo(x[0], y[0]);
        for (int i = 1; i < data.length; i++) {
            path.lineTo(x[i], y[i]);
        }
        path.lineTo(x[data.length - 1], computedGrounded);
        path.lineTo(x[0], computedGrounded);

        canvas.drawPath(path, paint);
    }
}
