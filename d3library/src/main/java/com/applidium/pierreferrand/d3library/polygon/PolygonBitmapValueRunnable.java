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
        value.eraseColor(0);

        if (polygon.x.length == 0) {
            return;
        }

        path.rewind();

        float computedOffsetX = polygon.offsetX();
        float computedOffsetY = polygon.offsetY();

        if (polygon.proportional) {
            float width = polygon.width();
            float height = polygon.height();

            path.moveTo(
                (polygon.x[0] + computedOffsetX) * width,
                (polygon.y[0] + computedOffsetY) * height
            );
            for (int i = 1; i < polygon.x.length; i++) {
                path.lineTo(
                    (polygon.x[i] + computedOffsetX) * width,
                    (polygon.y[i] + computedOffsetY) * height
                );
            }
        } else {
            path.moveTo(polygon.x[0] + computedOffsetX, polygon.y[0] + computedOffsetY);
            for (int i = 1; i < polygon.x.length; i++) {
                path.lineTo(polygon.x[i] + computedOffsetX, polygon.y[i] + computedOffsetY);
            }
        }
        canvas.drawPath(path, polygon.paint());
    }
}
