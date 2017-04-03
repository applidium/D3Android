package com.applidium.pierreferrand.d3library.arc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.Line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;

public class D3Arc<T> extends D3Drawable {
    private int[] colors = new int[]{0xFF0000FF, 0xFFFF0000, 0xFF00FF00, 0xFF000000};

    private D3FloatFunction outerRadius;
    private D3FloatFunction innerRadius;

    private D3FloatFunction offsetX;
    private D3FloatFunction offsetY;

    private T[] data;
    private D3DataMapperFunction<T> values;
    private D3DataMapperFunction<T> weights;

    public D3Arc() {
        this(null);
    }

    public D3Arc(T[] data) {
        data(data);
        weights(new D3DataMapperFunction<T>() {
            @Override public float compute(T object, int position, T[] data) {
                return 1f;
            }
        });
        innerRadius(0f);
        outerRadius(new D3FloatFunction() {
            @Override public float getFloat() {
                return Math.min(height(), width()) / 2;
            }
        });
        offsetX(0f);
        offsetY(0f);
    }

    public float innerRadius() {
        return innerRadius.getFloat();
    }

    public D3Arc<T> innerRadius(final float innerRadius) {
        this.innerRadius = new D3FloatFunction() {
            @Override public float getFloat() {
                return innerRadius;
            }
        };
        return this;
    }

    public D3Arc<T> innerRadius(D3FloatFunction innerRadius) {
        this.innerRadius = innerRadius;
        return this;
    }

    public float outerRadius() {
        return outerRadius.getFloat();
    }

    public D3Arc<T> outerRadius(final float outerRadius) {
        this.outerRadius = new D3FloatFunction() {
            @Override public float getFloat() {
                return outerRadius;
            }
        };
        return this;
    }

    public D3Arc<T> outerRadius(D3FloatFunction outerRadius) {
        this.outerRadius = outerRadius;
        return this;
    }

    public T[] data() {
        return data;
    }

    public D3Arc<T> data(T[] data) {
        this.data = data.clone();
        return this;
    }

    public int[] colors() {
        return colors;
    }

    public D3Arc<T> colors(int[] colors) {
        this.colors = colors;
        return this;
    }

    public float offsetX() {
        return offsetX.getFloat();
    }

    public D3Arc<T> offsetX(final float offsetX) {
        this.offsetX = new D3FloatFunction() {
            @Override public float getFloat() {
                return offsetX;
            }
        };
        return this;
    }

    public D3Arc<T> offsetX(D3FloatFunction offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public float offsetY() {
        return offsetY.getFloat();
    }

    public D3Arc<T> offsetY(final float offsetY) {
        this.offsetY = new D3FloatFunction() {
            @Override public float getFloat() {
                return offsetY;
            }
        };
        return this;
    }

    public D3Arc<T> offsetY(D3FloatFunction offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    public float[] weights() {
        float[] result = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = weights.compute(data[i], i, data);
        }
        return result;
    }

    public D3Arc<T> weights(final float[] weights) {
        this.weights = new D3DataMapperFunction<T>() {
            private float[] customWeights = weights.clone();

            @Override public float compute(T object, int position, T[] data) {
                return customWeights[position];
            }
        };
        return this;
    }

    public D3Arc<T> weights(D3DataMapperFunction<T> weights) {
        this.weights = weights;
        return this;
    }

    public float[] values() {
        float[] result = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = values.compute(data[i], i, data);
        }
        return result;
    }

    public D3Arc<T> values(D3DataMapperFunction<T> values) {
        this.values = values;
        return this;
    }


    @Override public void draw(Canvas canvas) {
        float[] weights = this.weights();
        float totalWeight = 0f;

        for (int i = 0; i < data.length; i++) {
            totalWeight += weights[i];
        }
        if (totalWeight == 0f) {
            return;
        }

        drawPie(canvas, weights, totalWeight);
    }

    private void drawPie(Canvas canvas, float[] weights, float totalWeight) {
        float outerRadius = outerRadius();
        float currentAngle = 0f;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        Bitmap bitmap = Bitmap.createBitmap(
            (int) (2 * outerRadius), (int) (2 * outerRadius), Bitmap.Config.ARGB_8888
        );
        Canvas c = new Canvas(bitmap);
        for (int i = 0; i < data.length; i++) {
            paint.setColor(colors[i % colors.length]);
            c.drawArc(
                0f,
                0f,
                2f * outerRadius,
                2f * outerRadius,
                0f,
                360f - currentAngle,
                true,
                paint
            );
            currentAngle += 360.0f * weights[i] / totalWeight;
        }
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        c.drawCircle(outerRadius, outerRadius, innerRadius(), paint);

        canvas.drawBitmap(bitmap, offsetX(), offsetY(), null);
    }
}
