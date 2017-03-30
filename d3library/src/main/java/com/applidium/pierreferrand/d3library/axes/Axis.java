package com.applidium.pierreferrand.d3library.axes;

import android.graphics.Color;
import android.graphics.Paint;

import com.applidium.pierreferrand.d3library.scale.Scale;

public class Axis {

    private static final float DEFAULT_TICK_SIZE = 25.0f;
    private static final float DEFAULT_STROKE_WIDTH = 5.0f;
    private static final float DEFAULT_OFFSET = 25.0f;
    private static final int DEFAULT_TICK_NUMBER = 5;

    private float offsetX = DEFAULT_OFFSET;
    private float offsetY = DEFAULT_OFFSET;
    private float innerTickSize = DEFAULT_TICK_SIZE;
    private float outerTickSize = DEFAULT_TICK_SIZE;
    private int ticksNumber = DEFAULT_TICK_NUMBER;

    private final AxisOrientation orientation;
    private Scale scale;

    private float[] ticks;

    private Paint paint;
    private Paint textPaint;

    public Axis(AxisOrientation orientation, Scale scale) {
        this.orientation = orientation;
        this.scale = scale;
        setUpPaints();
    }

    private void setUpPaints() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(new Color().rgb(0, 0, 0));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
    }
}
