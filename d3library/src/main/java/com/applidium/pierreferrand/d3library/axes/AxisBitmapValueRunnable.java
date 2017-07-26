package com.applidium.pierreferrand.d3library.axes;

import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.threading.BitmapValueRunnable;

class AxisBitmapValueRunnable<T> extends BitmapValueRunnable {
    @NonNull private final D3AxisDrawer<T> drawer;

    AxisBitmapValueRunnable(@NonNull D3AxisDrawer<T> drawer) {
        this.drawer = drawer;
    }

    @Override protected void computeValue() {
        value.eraseColor(0);
        drawer.draw(canvas);
    }
}
