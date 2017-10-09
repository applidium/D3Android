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

package com.fabernovel.d3library.arc;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;

final class D3ArcDrawer {
    private static RectF rectangle = new RectF();

    private D3ArcDrawer() {}

    static void drawArcs(
        @NonNull Canvas canvas, float innerRadius, float outerRadius, float offsetX, float offsetY,
        @NonNull Angles angles, @NonNull Paint paint, int[] colors
    ) {
        float diffRadius = outerRadius - innerRadius;
        paint.setStrokeWidth(diffRadius);
        paint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < angles.drawAngles.length; i++) {
            paint.setColor(colors[i % colors.length]);
            rectangle.left = offsetX + diffRadius / 2F;
            rectangle.top = offsetY + diffRadius / 2F;
            rectangle.right = offsetX + 2F * outerRadius - diffRadius / 2F;
            rectangle.bottom = offsetY + 2F * outerRadius - diffRadius / 2F;
            canvas.drawArc(
                rectangle,
                angles.startAngles[i],
                angles.drawAngles[i],
                false,
                paint
            );
        }
    }
}
