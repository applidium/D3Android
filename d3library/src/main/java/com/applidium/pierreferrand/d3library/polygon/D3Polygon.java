package com.applidium.pierreferrand.d3library.polygon;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;

import java.util.Arrays;

public class D3Polygon extends D3Drawable {

    private static final float DEFAULT_STROKE_WIDTH = 5.0f;

    private float[] x;
    private float[] y;

    private boolean proportional;
    private Paint paint;

    public D3Polygon() {
        setupPaint();
    }

    private void setupPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(new Color().rgb(0, 0, 0));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
    }

    public float[] x() {
        return x.clone();
    }

    public D3Polygon x(float[] x) {
        this.x = x;
        return this;
    }

    public float[] y() {
        return y.clone();
    }

    public D3Polygon y(float[] y) {
        this.y = y;
        return this;
    }

    public float[] coordinate() {
        return mergeArrays(x, y);
    }

    private static float[] mergeArrays(float[] firstArray, float[] secondArray) {
        float[] result = new float[firstArray.length * 2];
        for (int i = 0; i < firstArray.length; i++) {
            result[2 * i] = firstArray[i];
            result[2 * i + 1] = secondArray[i];
        }
        return result;
    }

    public D3Polygon coordinate(float[] coordinate) {
        x = new float[coordinate.length / 2];
        y = new float[coordinate.length / 2];
        separateArray(coordinate, x, y);
        return this;
    }

    private static void separateArray(float[] merge, float[] firstArray, float[] secondArray) {
        for (int i = 0; i < firstArray.length; i++) {
            firstArray[i] = merge[2 * i];
            secondArray[i] = merge[2 * i + 1];
        }
    }

    public boolean proportional() {
        return proportional;
    }

    public D3Polygon proportional(boolean proportional) {
        this.proportional = proportional;
        return this;
    }

    @Override public D3Polygon onClickAction(OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3Polygon onScrollAction(OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3Polygon onPinchAction(OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    public static float[] polygonHull(float[] coordinate) {
        float[] x = new float[coordinate.length / 2];
        float[] y = new float[coordinate.length / 2];
        separateArray(coordinate, x, y);
        return polygonHull(x, y);
    }

    public static float[] polygonHull(float[] x, float[] y) {
        int n = x.length;
        int pointer = 0;
        float[] hullX = new float[2 * n];
        float[] hullY = new float[2 * n];

        sortPoints(x, y);
        for (int i = 0; i < n; ++i) {
            while (pointer >= 2 && cross(
                hullX[pointer - 2], hullY[pointer - 2],
                hullX[pointer - 1], hullY[pointer - 1],
                x[i], y[i]
            )) {
                pointer--;
            }
            hullX[pointer] = x[i];
            hullY[pointer++] = y[i];
        }

        for (int i = n - 2, t = pointer + 1; i >= 0; i--) {
            while (pointer >= t && cross(
                hullX[pointer - 2], hullY[pointer - 2],
                hullX[pointer - 1], hullY[pointer - 1],
                x[i], y[i]
            )) {
                pointer--;
            }
            hullX[pointer] = x[i];
            hullY[pointer++] = y[i];
        }

        return mergeArrays(
            Arrays.copyOfRange(hullX, 0, pointer - 1), Arrays.copyOfRange(hullY, 0, pointer - 1)
        );
    }

    private static boolean cross(
        float x1, float y1,
        float x2, float y2,
        float x3, float y3
    ) {
        return (x2 - x1) * (y2 - y3) + (y2 - y1) * (x3 - x2) >= 0;
    }

    private static void sortPoints(float[] x, float[] y) {
        int pointer = 0;
        float tmp;
        while (pointer < x.length - 1) {
            if (x[pointer] > x[pointer + 1]
                || (x[pointer] == x[pointer + 1] && y[pointer] < y[pointer + 1])) {
                tmp = x[pointer];
                x[pointer] = x[pointer + 1];
                x[pointer + 1] = tmp;
                tmp = y[pointer];
                y[pointer] = y[pointer + 1];
                y[pointer + 1] = tmp;
                pointer = Math.max(pointer - 1, 0);
            } else {
                pointer++;
            }
        }
    }

    public float centroidX() {
        return average(x);
    }

    private float average(float[] array) {
        float result = 0f;
        for (int i = 0; i < array.length; i++) {
            result += array[i];
        }
        return result / array.length;
    }

    public float centroidY() {
        return average(y);
    }

    @Override public void draw(Canvas canvas) {
        Path path = new Path();
        path.setFillType(Path.FillType.WINDING);
        if (proportional) {
            path.moveTo(x[0] * width(), y[0] * height());
            for (int i = 1; i < x.length; i++) {
                path.lineTo(x[i] * width(), y[i] * height());
            }
        } else {
            path.moveTo(x[0], y[0]);
            for (int i = 1; i < x.length; i++) {
                path.lineTo(x[i], y[i]);
            }
        }
        canvas.drawPath(path, paint);
    }
}
