package com.applidium.pierreferrand.d3library.arc;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

final class D3ArcDrawer {
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
            canvas.drawArc(
                offsetX + diffRadius / 2F,
                offsetY + diffRadius / 2F,
                offsetX + 2F * outerRadius - diffRadius / 2F,
                offsetY + 2F * outerRadius - diffRadius / 2F,
                angles.startAngles[i],
                angles.drawAngles[i],
                false,
                paint
            );
        }
    }
}
