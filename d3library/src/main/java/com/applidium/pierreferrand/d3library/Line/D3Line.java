package com.applidium.pierreferrand.d3library.Line;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.scale.Interpolator;
import com.applidium.pierreferrand.d3library.scale.LinearInterpolator;

public class D3Line<T> extends D3Drawable {

    private static final float DEFAULT_STROKE_WIDTH = 5.0f;

    private T[] data;
    private D3DataMapperFunction<T> x;
    private D3DataMapperFunction<T> y;

    private Paint paint;
    private Interpolator interpolator;

    public D3Line() {
        this(null);
    }

    public D3Line(T[] data) {
        this.data = data.clone();
        interpolator = new LinearInterpolator();
        setupPaint();
    }

    private void setupPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(new Color().rgb(0, 0, 0));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
    }

    public float[] x() {
        float[] result = new float[data.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = x.compute(data[i], i, data);
        }
        return result;
    }

    public D3Line<T> x(D3DataMapperFunction<T> function) {
        x = function;
        return this;
    }

    public float[] y() {
        float[] result = new float[data.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = y.compute(data[i], i, data);
        }
        return result;
    }

    public D3Line<T> y(D3DataMapperFunction<T> function) {
        y = function;
        return this;
    }

    public Object[] data() {
        return data.clone();
    }

    public D3Line<T> data(T[] data) {
        this.data = data.clone();
        return this;
    }

    public Interpolator interpolator() {
        return interpolator;
    }

    public D3Line<T> interpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public float interpolateValue(float measuredX) {
        float[] x = this.x();
        float[] y = this.y();
        int index = 0;
        while (index < y.length - 2 && x[index + 1] < measuredX) {
            index++;
        }
        float yValue = interpolator.interpolate(
            measuredX,
            new float[]{x[index], x[index + 1]},
            new float[]{y[index], y[index + 1]}
        );
        return yValue;
    }

    @Override public void draw(Canvas canvas) {
        if (data.length < 2) {
            return;
        }
        float prevX;
        float prevY;

        float nextX = x.compute(data[0], 0, data);
        float nextY = y.compute(data[0], 0, data);

        for (int i = 1; i < data.length; i++) {
            prevX = nextX;
            prevY = nextY;

            nextX = x.compute(data[i], i, data);
            nextY = y.compute(data[i], i, data);

            canvas.drawLine(prevX, prevY, nextX, nextY, paint);
        }
    }
}
