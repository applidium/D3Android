package com.applidium.pierreferrand.d3library.arc;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.helper.TextHelper;
import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

class LabelsValueRunnable<T> implements ValueRunnable<LabelsCoordinates> {
    private static final String DATA_ERROR = "Data should not be null.";

    @NonNull private final Object key;
    @NonNull private final D3Arc<T> arc;
    @NonNull private final Paint paint;

    @Nullable private LabelsCoordinates properties;

    LabelsValueRunnable(@NonNull D3Arc<T> arc, @NonNull Object key, @NonNull Paint paint) {
        this.key = key;
        this.arc = arc;
        this.paint = paint;
    }

    Object getKey() {
        return key;
    }

    @Override public LabelsCoordinates getValue() {
        return properties;
    }

    @Override public void run() {
        synchronized (key) {
            computeLabels();
            key.notifyAll();
        }
    }

    void setDataLength(int length) {
        properties = new LabelsCoordinates(length);
    }

    private void computeLabels() {
        if (arc.labels == null) {
            return;
        }
        if (arc.data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }

        Angles computedAngles = arc.preComputedAngles.getValue();
        float outerRadius = arc.outerRadius();
        float radius = (outerRadius + arc.innerRadius()) / 2F;
        float currentAngle;
        float radianAngle;
        float coordinateX;
        float coordinateY;

        for (int i = 0; i < arc.data.length; i++) {
            currentAngle = computedAngles.startAngles[i];
            radianAngle = (float) Math.toRadians(
                -(currentAngle + computedAngles.drawAngles[i] / 2F)
            );

            coordinateX = outerRadius + radius * (float) Math.cos(radianAngle);
            coordinateX -= paint.measureText(arc.labels[i]) / 2F;
            coordinateY = outerRadius - radius * (float) Math.sin(radianAngle);
            coordinateY += TextHelper.getTextHeight(arc.labels[i], paint) / 2F;
            properties.coordinatesX[i] = coordinateX;
            properties.coordinatesY[i] = coordinateY;
        }
    }
}
