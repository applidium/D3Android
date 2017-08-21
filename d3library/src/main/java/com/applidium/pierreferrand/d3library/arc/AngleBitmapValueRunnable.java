package com.applidium.pierreferrand.d3library.arc;

import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.threading.BitmapValueRunnable;

class AngleBitmapValueRunnable<T> extends BitmapValueRunnable {
    @NonNull private final D3Arc<T> arc;

    AngleBitmapValueRunnable(@NonNull D3Arc<T> arc) {
        this.arc = arc;
    }

    @Override protected void computeValue() {
        value.eraseColor(0);
        D3ArcDrawer.drawArcs(
            canvas, arc.innerRadius(), arc.outerRadius(), arc.offsetX(), arc.offsetY(),
            arc.preComputedAngles.getValue(), arc.paint(), arc.colors
        );
    }
}
