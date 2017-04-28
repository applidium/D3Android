package com.applidium.pierreferrand.d3library.polygon;

import android.graphics.Canvas;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;

import java.util.Arrays;

public class D3Polygon extends D3Drawable {
    private static final String X_ERROR = "X should not be null";
    private static final String Y_ERROR = "Y should not be null";
    
    @Nullable private float[] x;
    @Nullable private float[] y;
    private boolean proportional;

    public D3Polygon() {
        setupPaint();
    }

    public D3Polygon(@NonNull float[] x, @NonNull float[] y) {
        x(x);
        y(y);
        setupPaint();
    }

    public D3Polygon(@NonNull float[] coordinates) {
        coordinates(coordinates);
        setupPaint();
    }

    /**
     * Returns the horizontal coordinates of the Polygon's points.
     */
    public float[] x() {
        if (x == null) {
            throw new IllegalStateException(X_ERROR);
        }
        return x.clone();
    }

    /**
     * Sets the horizontal coordinates of the Polygon's points.
     */
    public D3Polygon x(float[] x) {
        this.x = x.clone();
        return this;
    }

    /**
     * Returns the vertical coordinates of the Polygon's points.
     */
    public float[] y() {
        if (y == null) {
            throw new IllegalStateException(Y_ERROR);
        }
        return y.clone();
    }

    /**
     * Sets the vertical coordinates of the Polygon's points.
     */
    public D3Polygon y(float[] y) {
        this.y = y.clone();
        return this;
    }

    /**
     * Returns the coordinates of the Polygon's points. The array format is [x1, y1, x2, y2, ...]
     */
    public float[] coordinates() {
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

    /**
     * Sets the coordinates of the Polygon's points.
     * The array format should be [x1, y1, x2, y2, ...]
     */
    public D3Polygon coordinates(float[] coordinate) {
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

    /**
     * Returns true if the coordinates are proportional to the dimensions or not.
     */
    public boolean proportional() {
        return proportional;
    }

    /**
     * Sets if the coordinates should be treated as proportional to the dimensions or not. If so,
     * x and y should be in [0F, 1F].
     */
    public D3Polygon proportional(boolean proportional) {
        this.proportional = proportional;
        return this;
    }

    @Override public D3Polygon onClickAction(@NonNull OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3Polygon onScrollAction(@NonNull OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3Polygon onPinchAction(@NonNull OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    /**
     * Returns the coordinates of hull of the polygon which coordinates are given. The coordinates'
     * format is [x1, y1, x2, y2, ...]
     */
    public static float[] polygonHull(@NonNull float[] coordinate) {
        float[] x = new float[coordinate.length / 2];
        float[] y = new float[coordinate.length / 2];
        separateArray(coordinate, x, y);
        return polygonHull(x, y);
    }

    /**
     * Returns the coordinates of hull of the polygon which coordinates are given. The coordinates'
     * format is [x1, y1, x2, y2, ...]
     */
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

    /**
     * Return if the points O1 02 03 ae arranged in counterclockwise order.
     */
    private static boolean cross(
        float x1, float y1,
        float x2, float y2,
        float x3, float y3
    ) {
        return (x2 - x1) * (y2 - y3) + (y2 - y1) * (x3 - x2) >= 0;
    }

    private static void sortPoints(@NonNull float[] x, @NonNull float[] y) {
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

    /**
     * Returns the horizontal coordinate of the Polygon's centroid.
     */
    public float centroidX() {
        return average(x);
    }

    private float average(float[] array) {
        float result = 0F;
        for (int i = 0; i < array.length; i++) {
            result += array[i];
        }
        return result / array.length;
    }

    /**
     * Returns the vertical coordinate of the Polygon's centroid.
     */
    public float centroidY() {
        return average(y);
    }

    /**
     * Returns true if the given point is in the area defined by the polygon.
     */
    public boolean contains(float coordinateX, float coordinateY) {
        float x1;
        float y1;
        float x0 = x[x.length - 1];
        float y0 = y[x.length - 1];
        boolean inside = false;

        for (int i = 0; i < x.length; i++) {
            x1 = x[i];
            y1 = y[i];
            if (((y1 > coordinateY) != (y0 > coordinateY))
                && (coordinateX < (x0 - x1) * (coordinateY - y1) / (y0 - y1) + x1)) {
                inside = !inside;
            }
            x0 = x1;
            y0 = y1;
        }
        return inside;
    }

    @Override public void draw(@NonNull Canvas canvas) {
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
