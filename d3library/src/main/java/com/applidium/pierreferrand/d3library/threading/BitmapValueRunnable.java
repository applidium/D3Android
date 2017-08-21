package com.applidium.pierreferrand.d3library.threading;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;

public abstract class BitmapValueRunnable extends ValueRunnable<Bitmap> {
    @NonNull protected final Canvas canvas;

    protected BitmapValueRunnable() {
        canvas = new Canvas();
        resizeBitmap(1F, 1F);
    }

    public void resizeBitmap(float width, float height) {
        synchronized (key) {
            value = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
            canvas.setBitmap(value);
        }
    }
}
