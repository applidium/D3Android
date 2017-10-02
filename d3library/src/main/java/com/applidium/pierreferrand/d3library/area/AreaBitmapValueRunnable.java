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

package com.applidium.pierreferrand.d3library.area;

import android.graphics.Path;
import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.threading.BitmapValueRunnable;

class AreaBitmapValueRunnable<T> extends BitmapValueRunnable {
    private static final String GROUND_ERROR = "Ground should not be null";
    private static final String DATA_ERROR = "Data should not be null";

    @NonNull private final Path path;

    private final D3Area<T> area;

    AreaBitmapValueRunnable(D3Area<T> area) {
        this.area = area;
        path = new Path();
    }

    @Override protected void computeValue() {
        T[] data = area.data();
        if (data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        if (data.length < 2) {
            return;
        }
        if (area.ground == null) {
            throw new IllegalStateException(GROUND_ERROR);
        }

        value.eraseColor(0);
        float computedGrounded = area.ground.getFloat();
        float[] x = area.x();
        float[] y = area.y();

        path.rewind();
        path.moveTo(x[0], y[0]);
        for (int i = 1; i < Math.min(x.length, y.length); i++) {
            path.lineTo(x[i], y[i]);
        }
        path.lineTo(x[x.length - 1], computedGrounded);
        path.lineTo(x[0], computedGrounded);

        canvas.drawPath(path, area.paint());
    }
}
