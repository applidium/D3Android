package com.applidium.pierreferrand.d3library.arc;

import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.helper.TextHelper;
import com.applidium.pierreferrand.d3library.threading.ValueRunnable;

class LabelsValueRunnable<T> extends ValueRunnable<LabelsCoordinates> {
    private static final String DATA_ERROR = "Data should not be null.";

    @NonNull private final D3Arc<T> arc;
    @NonNull private final Paint paint;

    LabelsValueRunnable(@NonNull D3Arc<T> arc, @NonNull Paint paint) {
        this.arc = arc;
        this.paint = paint;
    }

    void setDataLength(int length) {
        value = new LabelsCoordinates(length);
    }

    protected void computeValue() {
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
            value.coordinatesX[i] = coordinateX;
            value.coordinatesY[i] = coordinateY;
        }
    }
}
