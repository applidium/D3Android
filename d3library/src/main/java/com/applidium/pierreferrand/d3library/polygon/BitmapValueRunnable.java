package com.applidium.pierreferrand.d3library.polygon;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

public class BitmapValueRunnable extends ValueRunnable<Bitmap> {
    @NonNull private final D3Polygon polygon;
    @NonNull private final Canvas canvas;
    @NonNull private final Path path;


    public BitmapValueRunnable(@NonNull D3Polygon polygon) {
        this.polygon = polygon;
        canvas = new Canvas();
        path = new Path();
        path.setFillType(Path.FillType.WINDING);
    }

    void resizeBitmap(float width, float height) {
        value = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(value);
    }


    @Override protected void computeValue() {
        path.rewind();
        if (polygon.proportional) {
            float width = polygon.width();
            float height = polygon.height();

            path.moveTo(polygon.x[0] * width, polygon.y[0] * height);
            for (int i = 1; i < polygon.x.length; i++) {
                path.lineTo(polygon.x[i] * width, polygon.y[i] * height);
            }
        } else {
            path.moveTo(polygon.x[0], polygon.y[0]);
            for (int i = 1; i < polygon.x.length; i++) {
                path.lineTo(polygon.x[i], polygon.y[i]);
            }
        }
        canvas.drawPath(path, polygon.paint());
    }
}
