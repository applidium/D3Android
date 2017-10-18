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

package com.fabernovel.d3library.axes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;

import com.fabernovel.d3library.helper.TextHelper;

final class D3AxisDrawer<T> {
    @NonNull private final Path path;
    private final D3Axis<T> axis;
    private Paint paint;

    D3AxisDrawer(D3Axis<T> axis) {
        this.axis = axis;
        path = new Path();
    }

    void setPaint(Paint paint) {
        this.paint = paint;
        paint.setStyle(Paint.Style.STROKE);
    }

    void draw(@NonNull Canvas canvas) {
        initPath();
        computeLine();
        drawTicks();
        drawPath(canvas);
        drawTicksLegend(canvas);
    }

    private void initPath() {
        path.rewind();
    }

    private void computeLine() {
        float startX;
        float startY;
        float endX;
        float endY;
        float computedOffsetX = axis.offsetX.getFloat();
        float computedOffsetY = axis.offsetY.getFloat();
        if (axis.orientation == AxisOrientation.TOP || axis.orientation == AxisOrientation.BOTTOM) {
            startX = computedOffsetX + axis.firstBoundRange();
            startY = computedOffsetY;
            endX = computedOffsetX + axis.lastBoundRange();
            endY = computedOffsetY;
        } else {
            startX = computedOffsetX;
            startY = computedOffsetY + axis.firstBoundRange();
            endX = computedOffsetX;
            endY = computedOffsetY + axis.lastBoundRange();
        }
        path.moveTo(startX, startY);
        path.lineTo(endX, endY);
    }

    private void drawTicks() {
        if (axis.orientation == AxisOrientation.TOP || axis.orientation == AxisOrientation.BOTTOM) {
            drawVerticalTicks();
        } else {
            drawHorizontalTicks();
        }
    }

    private void drawHorizontalTicks() {
        float computedOffsetX = axis.offsetX.getFloat();
        float computedOffsetY = axis.offsetY.getFloat();
        float outerX = computedOffsetX - axis.outerTickSize / 2;
        float innerX = computedOffsetX + axis.innerTickSize / 2;
        float coordinateY;
        int ticksNumber = axis.ticks();
        for (int i = 0; i < ticksNumber; i++) {
            coordinateY = computedOffsetY
                + axis.lastBoundRange() * i / (ticksNumber - 1F)
                + axis.firstBoundRange() * (ticksNumber - i - 1F) / (ticksNumber - 1F);
            path.moveTo(outerX, coordinateY);
            path.lineTo(innerX, coordinateY);
        }
    }

    private void drawVerticalTicks() {
        float computedOffsetX = axis.offsetX.getFloat();
        float computedOffsetY = axis.offsetY.getFloat();
        float outerY = computedOffsetY + axis.outerTickSize / 2;
        float innerY = computedOffsetY - axis.innerTickSize / 2;
        float coordinateX;
        int ticksNumber = axis.ticks();
        for (int i = 0; i < ticksNumber; i++) {
            coordinateX = computedOffsetX
                + axis.lastBoundRange() * i / (ticksNumber - 1F)
                + axis.firstBoundRange() * (ticksNumber - i - 1F) / (ticksNumber - 1F);
            path.moveTo(coordinateX, innerY);
            path.lineTo(coordinateX, outerY);
        }
    }

    private void drawPath(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    private void drawTicksLegend(@NonNull Canvas canvas) {
        if (axis.orientation == AxisOrientation.TOP || axis.orientation == AxisOrientation.BOTTOM) {
            drawHorizontalLegend(canvas);
        } else {
            drawVerticalLegend(canvas);
        }
    }

    private void drawVerticalLegend(@NonNull Canvas canvas) {
        float computedOffsetX = axis.offsetX.getFloat();
        String[] usableTicks = axis.ticksLegend.getValue();
        float coordinateX = axis.orientation == AxisOrientation.LEFT ?
            computedOffsetX - axis.innerTickSize : computedOffsetX + axis.innerTickSize;
        coordinateX += axis.legendProperties.offsetX();

        for (int i = 0; i < usableTicks.length; i++) {
            drawSingleVerticalLegend(canvas, usableTicks[i], coordinateX, i);
        }
    }

    private void drawSingleVerticalLegend(
        @NonNull Canvas canvas, @NonNull String tick, float coordinateX, int i
    ) {
        float computedOffsetY = axis.offsetY.getFloat();
        int ticksNumber = axis.ticks();
        float coordinateY = computedOffsetY + axis.lastBoundRange() * i / (ticksNumber - 1F)
            + axis.firstBoundRange() * (ticksNumber - i - 1F) / (ticksNumber - 1F);
        coordinateY += axis.legendProperties.offsetY();
        coordinateY += alignmentVerticalOffset(tick);
        float realCoordinateX = coordinateX - (axis.orientation == AxisOrientation.LEFT ?
            axis.textPaint.measureText("" + tick) : 0);
        canvas.drawText(tick, realCoordinateX, coordinateY, axis.textPaint);
    }

    private float alignmentVerticalOffset(@NonNull String legend) {
        float height = TextHelper.getTextHeight(legend, axis.textPaint);
        switch (axis.legendProperties.verticalAlignment()) {
            case BOTTOM:
                return height;
            case CENTER:
                return height / 2F;
            default:
                return 0F;
        }
    }

    private void drawHorizontalLegend(@NonNull Canvas canvas) {
        float computedOffsetY = axis.offsetY.getFloat();
        String[] usableTicks = axis.ticksLegend.getValue();
        float coordinateY = axis.orientation == AxisOrientation.TOP ?
            computedOffsetY - axis.innerTickSize : computedOffsetY + axis.innerTickSize;
        coordinateY += axis.legendProperties.offsetY();

        for (int i = 0; i < usableTicks.length; i++) {
            drawSingleHorizontalLegend(canvas, usableTicks, coordinateY, i);
        }
    }

    private void drawSingleHorizontalLegend(
        @NonNull Canvas canvas,
        @NonNull String[] ticks,
        float coordinateY,
        int i
    ) {
        float computedOffsetX = axis.offsetX.getFloat();
        int ticksNumber = axis.ticks();
        float coordinateX = computedOffsetX + axis.lastBoundRange() * i / (ticksNumber - 1F)
            + axis.firstBoundRange() * (ticksNumber - i - 1F) / (ticksNumber - 1F);
        coordinateX += axis.legendProperties.offsetX();
        coordinateX -= alignmentHorizontalOffset(ticks[i]);
        float realCoordinateY = coordinateY + (axis.orientation == AxisOrientation.TOP ? 0 :
            TextHelper.getTextHeight(ticks[i], axis.textPaint));

        canvas.drawText(ticks[i], coordinateX, realCoordinateY, axis.textPaint);
    }

    private float alignmentHorizontalOffset(@NonNull String legend) {
        switch (axis.legendProperties.horizontalAlignement()) {
            case LEFT:
                return axis.textPaint.measureText(legend);
            case CENTER:
                return axis.textPaint.measureText(legend) / 2F;
            default:
                return 0F;
        }
    }
}
