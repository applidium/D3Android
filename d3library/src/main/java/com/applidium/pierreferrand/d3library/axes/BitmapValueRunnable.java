package com.applidium.pierreferrand.d3library.axes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

public class BitmapValueRunnable<T> extends ValueRunnable<Bitmap> {
    @NonNull private final Canvas canvas;
    @NonNull private final D3AxisDrawer<T> drawer;

    BitmapValueRunnable(@NonNull D3AxisDrawer<T> drawer) {
        this.drawer = drawer;
        value = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(value);
    }

    void resizeBitmap(float width, float height) {
        value = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(value);
    }

    @Override protected void computeValue() {
        value.eraseColor(0);
        drawer.draw(canvas);
    }
}
