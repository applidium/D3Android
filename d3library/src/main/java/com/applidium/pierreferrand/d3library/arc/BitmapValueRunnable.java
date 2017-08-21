package com.applidium.pierreferrand.d3library.arc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

class BitmapValueRunnable<T> extends ValueRunnable<Bitmap> {
    @NonNull private final D3Arc<T> arc;
    @NonNull private final Canvas canvas;

    BitmapValueRunnable(@NonNull D3Arc<T> arc) {
        this.arc = arc;
        value = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(value);
    }

    void resizeBitmap(float width, float height) {
        value = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(value);
    }

    @Override protected void computeValue() {
        value.eraseColor(0);
        D3ArcDrawer.drawArcs(
            canvas, arc.innerRadius(), arc.outerRadius(), arc.offsetX(), arc.offsetY(),
            arc.preComputedAngles.getValue(), arc.paint(), arc.colors
        );
    }
}
