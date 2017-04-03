package com.applidium.pierreferrand.d3library;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class D3View extends View {

    public final List<D3Drawable> drawables;

    public D3View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        drawables = new ArrayList<>();
    }

    public void add(D3Drawable drawable) {
        drawables.add(drawable);
    }

    @Override public void draw(Canvas canvas) {
        super.draw(canvas);
        for (D3Drawable drawable : drawables) {
            drawable.setDimensions(getHeight(), getWidth());
            drawable.draw(canvas);
        }
    }
}
