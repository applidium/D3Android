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
