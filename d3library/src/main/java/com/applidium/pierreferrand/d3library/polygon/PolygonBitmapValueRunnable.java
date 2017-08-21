package com.applidium.pierreferrand.d3library.polygon;

import android.graphics.Path;
import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.threading.BitmapValueRunnable;

class PolygonBitmapValueRunnable extends BitmapValueRunnable {
    @NonNull private final D3Polygon polygon;
    @NonNull private final Path path;


    PolygonBitmapValueRunnable(@NonNull D3Polygon polygon) {
        this.polygon = polygon;
        path = new Path();
        path.setFillType(Path.FillType.WINDING);
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
