package com.applidium.pierreferrand.d3library.arc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

class BitmapValueRunnable<T> implements ValueRunnable<Bitmap> {
    @NonNull private final Object key = new Object();
    @NonNull private final D3Arc<T> arc;

    @NonNull private Bitmap bitmap;
    @NonNull private final Canvas canvas;

    BitmapValueRunnable(@NonNull D3Arc<T> arc) {
        this.arc = arc;
        bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    Object getKey() {
        return key;
    }

    void resizeBitmap(float width, float height) {
        bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
    }

    @Override public Bitmap getValue() {
        return bitmap;
    }

    @Override public void run() {
        synchronized (key) {
            bitmap.eraseColor(0);
            D3ArcDrawer.drawArcs(
                canvas, arc.innerRadius(), arc.outerRadius(), arc.offsetX(), arc.offsetY(),
                arc.preComputedAngles.getValue(), arc.paint(), arc.colors
            );
            key.notifyAll();
        }
    }
}
