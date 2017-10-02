/*
 * Copyright 2017, Fabernovel Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        if (arc.data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }

        String[] labels = arc.labels();

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
            coordinateX -= paint.measureText(labels[i]) / 2F;
            coordinateY = outerRadius - radius * (float) Math.sin(radianAngle);
            coordinateY += TextHelper.getTextHeight(labels[i], paint) / 2F;
            value.coordinatesX[i] = coordinateX;
            value.coordinatesY[i] = coordinateY;
        }
    }
}
