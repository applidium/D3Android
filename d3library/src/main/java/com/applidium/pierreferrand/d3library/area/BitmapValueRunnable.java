package com.applidium.pierreferrand.d3library.area;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;

import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

class BitmapValueRunnable<T> extends ValueRunnable<Bitmap> {
    private static final String GROUND_ERROR = "Ground should not be null";
    private static final String DATA_ERROR = "Data should not be null";

    private final D3Area<T> area;
    private final Canvas canvas = new Canvas();

    BitmapValueRunnable(D3Area<T> area) {
        this.area = area;
    }

    void onDimensionsChange(float width, float height) {
        value = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(value);
    }

    @Override protected void computeValue() {
        draw();
    }

    private void draw() {
        T[] data = area.data();
        if (data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        if (data.length < 2) {
            return;
        }
        if (area.ground == null) {
            throw new IllegalStateException(GROUND_ERROR);
        }

        value.eraseColor(0);
        float computedGrounded = area.ground.getFloat();
        float[] x = area.x();
        float[] y = area.y();

        Path path = new Path();
        path.moveTo(x[0], y[0]);
        for (int i = 1; i < data.length; i++) {
            path.lineTo(x[i], y[i]);
        }
        path.lineTo(x[data.length - 1], computedGrounded);
        path.lineTo(x[0], computedGrounded);

        canvas.drawPath(path, area.paint());
    }
}
