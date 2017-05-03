package com.applidium.pierreferrand.d3library.area;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.applidium.pierreferrand.d3library.Line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.Line.D3Line;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.scale.Interpolator;

public class D3Area<T> extends D3Line<T> {
    private D3FloatFunction ground;

    public D3Area() {
        super();
    }

    public D3Area(T[] data) {
        super(data);
        setupPaint();
    }

    @Override public D3Area<T> onClickAction(OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3Area<T> onScrollAction(OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3Area<T> onPinchAction(OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public D3Area<T> x(D3DataMapperFunction<T> function) {
        super.x(function);
        return this;
    }

    @Override public D3Area<T> y(D3DataMapperFunction<T> function) {
        super.y(function);
        return this;
    }

    @Override public D3Area<T> data(T[] data) {
        super.data(data);
        return this;
    }

    @Override public D3Area<T> interpolator(Interpolator interpolator) {
        super.interpolator(interpolator);
        return this;
    }

    @Override public D3Area<T> paint(Paint paint) {
        super.paint(paint);
        return this;
    }

    public float ground() {
        return ground.getFloat();
    }

    public D3Area<T> ground(D3FloatFunction ground) {
        this.ground = ground;
        return this;
    }

    @Override public void draw(Canvas canvas) {
        if (data.length < 2) {
            return;
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
