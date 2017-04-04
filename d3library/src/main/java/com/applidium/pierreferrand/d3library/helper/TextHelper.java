package com.applidium.pierreferrand.d3library.helper;

import android.graphics.Paint;
import android.graphics.Rect;

public final class TextHelper {
    private TextHelper() {}

    public static float getTextHeight(String text, Paint textPaint) {
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return (float) bounds.height();
    }
}
