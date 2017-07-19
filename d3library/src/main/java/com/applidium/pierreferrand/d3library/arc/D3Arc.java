package com.applidium.pierreferrand.d3library.arc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.helper.ColorHelper;
import com.applidium.pierreferrand.d3library.line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.threading.ValueStorage;

@SuppressWarnings({"unused", "WeakerAccess"})
public class D3Arc<T> extends D3Drawable {
    private static final float DEFAULT_LABEL_TEXT_SIZE = 25F;
    private static final float DEFAULT_PAD_ANGLE = 1F;
    private static final float HALF_CIRCLE_ANGLE = 180F;
    private static final float CIRCLE_ANGLE = 360F;
    private static final String INNER_RADIUS_ERROR = "InnerRadius should not be null.";
    private static final String OUTER_RADIUS_ERROR = "OuterRadius should not be null.";
    private static final String DATA_ERROR = "Data should not be null.";

    @NonNull int[] colors = new int[]{0xFF0000FF, 0xFFFF0000, 0xFF00FF00, 0xFF000000};

    @Nullable private D3FloatFunction outerRadius;
    @Nullable private D3FloatFunction innerRadius;

    private boolean optimize = false;
    float padAngle;

    @NonNull private D3FloatFunction offsetX;
    @NonNull private D3FloatFunction offsetY;
    @NonNull D3FloatFunction startAngle;
    @NonNull final ValueStorage<Angles> preComputedAngles;
    @NonNull private final ValueStorage<Bitmap> preComputedArc;
    @NonNull private final ValueStorage<LabelsCoordinates> preComputedLabels;

    @Nullable T[] data;
    @Nullable private D3DataMapperFunction<T> weights;
    private float[] weightArray;

    @Nullable String[] labels;
    @NonNull private Paint textPaint;

    @NonNull private final BitmapValueRunnable<T> bitmapValueRunnable;
    @NonNull private final LabelsValueRunnable<T> labelsValueRunnable;
    @NonNull private final AnglesValueRunnable<T> anglesValueRunnable;

    public D3Arc() {
        this(null);
    }

    public D3Arc(@Nullable T[] data) {
        setupPaint();
        setupActions();
        preComputedAngles = new ValueStorage<>();
        preComputedArc = new ValueStorage<>();
        preComputedLabels = new ValueStorage<>();
        bitmapValueRunnable = new BitmapValueRunnable<>(this);
        labelsValueRunnable = new LabelsValueRunnable<>(this, new Object(), textPaint);
        anglesValueRunnable = new AnglesValueRunnable<>(this, new Object());

        if (data != null) {
            data(data);
        }
        weights(new D3DataMapperFunction<T>() {
            @Override public float compute(T object, int position, T[] data) {
                return 1F;
            }
        });
        innerRadius(0F);
        outerRadius(new D3FloatFunction() {
            @Override public float getFloat() {
                return Math.min(height(), width()) / 2;
            }
        });
        startAngle(0F);
        offsetX(0F);
        offsetY(0F);
        padAngle(DEFAULT_PAD_ANGLE);
    }

    @Override protected void setupPaint() {
        super.setupPaint();
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(DEFAULT_LABEL_TEXT_SIZE);
        textPaint.setStyle(Paint.Style.FILL);
    }

    private void setupActions() {
        onClickAction(null);
        onScrollAction(null);
        onPinchAction(null);
    }

    /**
     * Returns the inner radius of the Arc.
     */
    public float innerRadius() {
        if (innerRadius == null) {
            throw new IllegalStateException(INNER_RADIUS_ERROR);
        }
        return innerRadius.getFloat();
    }

    /**
     * Sets the inner radius of the Arc.
     */
    public D3Arc<T> innerRadius(final float innerRadius) {
        this.innerRadius = new D3FloatFunction() {
            @Override public float getFloat() {
                return innerRadius;
            }
        };
        return this;
    }

    /**
     * Sets the inner radius of the Arc.
     */
    public D3Arc<T> innerRadius(@NonNull D3FloatFunction innerRadius) {
        this.innerRadius = innerRadius;
        return this;
    }

    /**
     * Returns the outer radius of the Arc.
     */
    public float outerRadius() {
        if (outerRadius == null) {
            throw new IllegalStateException(OUTER_RADIUS_ERROR);
        }
        return outerRadius.getFloat();
    }

    /**
     * Sets the outer radius of the Arc.
     */
    public D3Arc<T> outerRadius(final float outerRadius) {
        this.outerRadius = new D3FloatFunction() {
            @Override public float getFloat() {
                return outerRadius;
            }
        };
        return this;
    }

    /**
     * Sets the outer radius of the Arc.
     */
    public D3Arc<T> outerRadius(@NonNull D3FloatFunction outerRadius) {
        this.outerRadius = outerRadius;
        return this;
    }

    /**
     * Returns the data used by the Arc.
     */
    @Nullable public T[] data() {
        return data != null ? data.clone() : null;
    }

    /**
     * Sets the data used by the Arc.
     */
    public D3Arc<T> data(@NonNull T[] data) {
        weightArray = new float[data.length];
        anglesValueRunnable.setDataLenght(data.length);
        labelsValueRunnable.setDataLength(data.length);
        this.data = data.clone();
        return this;
    }

    /**
     * Returns the colors used when the Arc is drawn.
     */
    @NonNull public int[] colors() {
        return colors;
    }

    /**
     * Sets the colors used when the Arc is drawn.
     * The colors should have a 0xAARRGGBB format. If there are more data to display than colors,
     * the colors will be used circularly.
     */
    public D3Arc<T> colors(@NonNull int[] colors) {
        this.colors = colors;
        return this;
    }

    /**
     * Returns the horizontal offset of the Arc.
     */
    public float offsetX() {
        return offsetX.getFloat();
    }

    /**
     * Sets the horizontal offset of the Arc.
     */
    public D3Arc<T> offsetX(final float offsetX) {
        this.offsetX = new D3FloatFunction() {
            @Override public float getFloat() {
                return offsetX;
            }
        };
        return this;
    }

    /**
     * Sets the horizontal offset of the Arc.
     */
    public D3Arc<T> offsetX(@NonNull D3FloatFunction offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    /**
     * Returns the vertical offset of the Arc.
     */
    public float offsetY() {
        return offsetY.getFloat();
    }

    /**
     * Sets the vertical offset of the Arc.
     */
    public D3Arc<T> offsetY(final float offsetY) {
        this.offsetY = new D3FloatFunction() {
            @Override public float getFloat() {
                return offsetY;
            }
        };
        return this;
    }

    /**
     * Sets the vertical offset of the Arc.
     */
    public D3Arc<T> offsetY(@NonNull D3FloatFunction offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    /**
     * Returns the weight used to compute the proportion of each data.
     */
    @NonNull public float[] weights() {
        if (data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        if (weights == null) {
            return new float[0];
        }
        for (int i = 0; i < data.length; i++) {
            weightArray[i] = weights.compute(data[i], i, data);
        }
        return weightArray;
    }

    /**
     * Sets the weight used to compute the proportion of each data.
     */
    public D3Arc<T> weights(@NonNull final float[] weights) {
        this.weights = new D3DataMapperFunction<T>() {
            private float[] customWeights = weights.clone();

            @Override public float compute(T object, int position, T[] data) {
                return customWeights[position];
            }
        };
        return this;
    }

    /**
     * Sets the weight used to compute the proportion of each data.
     */
    public D3Arc<T> weights(@NonNull D3DataMapperFunction<T> weights) {
        this.weights = weights;
        return this;
    }

    /**
     * Sets the float values associated to each data.
     */
    @Nullable public String[] labels() {
        return labels != null ? labels.clone() : null;
    }

    /**
     * Defines if the labels must be drawn. If parameter is set to false, no label will be drawn.
     * If parameter is set to true, the labels will be computed to the current data using toString()
     * method on each data.
     */
    public D3Arc<T> labels(boolean drawLabelsDependingOnData) {
        if (data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }

        if (drawLabelsDependingOnData) {
            labels = new String[data.length];
            for (int i = 0; i < data.length; i++) {
                labels[i] = data[i].toString();
            }
        } else {
            labels = null;
        }
        return this;
    }

    /**
     * Sets the labels to use.
     */
    public D3Arc<T> labels(@NonNull String[] labels) {
        this.labels = labels.clone();
        return this;
    }

    /**
     * Returns the pad angle between each part.
     */
    public float padAngle() {
        return padAngle;
    }

    /**
     * Sets the pad angle between each part.
     */
    public D3Arc<T> padAngle(float padAngle) {
        this.padAngle = padAngle;
        return this;
    }

    /**
     * Returns the data corresponding to the part draw using the given coordinates.
     * Return null if it does not match any part.
     */
    @Nullable public T dataFromPosition(float x, float y) {
        if (data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        float xCenter = offsetX() + outerRadius();
        float yCenter = offsetY() + outerRadius();

        float diffX = x - xCenter;
        float diffY = y - yCenter;

        float radius = (float) Math.hypot(diffX, diffY);
        if (radius < innerRadius() || radius > outerRadius()) {
            return null;
        }

        float angle = (float) Math.atan(diffY / diffX);
        angle += angle < 0F ? Math.PI : 0F;
        angle += diffY < 0F ? Math.PI : 0F;
        angle = (float) (angle * HALF_CIRCLE_ANGLE / Math.PI);

        Angles computedAngles = preComputedAngles.getValue();

        for (int i = 0; i < data.length; i++) {
            if (inAngles(angle, computedAngles.startAngles[i], computedAngles.drawAngles[i])) {
                return data[i];
            }
        }
        return null;
    }

    private boolean inAngles(float angle, float startAngle, float drawAngle) {
        if (startAngle + drawAngle > CIRCLE_ANGLE) {
            return (angle > startAngle) || (angle < (startAngle + drawAngle) % CIRCLE_ANGLE);
        } else {
            return (angle > startAngle) && (angle < startAngle + drawAngle);
        }
    }

    /**
     * Returns the start angle for the arc
     */
    public float startAngle() {
        return startAngle.getFloat();
    }

    /**
     * Sets the start angle for the arc
     */
    public D3Arc<T> startAngle(final float startAngle) {
        startAngle(new D3FloatFunction() {
            @Override public float getFloat() {
                return startAngle;
            }
        });
        return this;
    }

    /**
     * Sets the start angle for the arc
     */
    public D3Arc<T> startAngle(@NonNull D3FloatFunction startAngle) {
        this.startAngle = startAngle;
        return this;
    }

    @Override public D3Arc<T> onClickAction(@Nullable OnClickAction onClickAction) {
        super.onClickAction(onClickAction);
        return this;
    }

    @Override public D3Arc<T> onScrollAction(@Nullable OnScrollAction onScrollAction) {
        super.onScrollAction(onScrollAction);
        return this;
    }

    @Override public D3Arc<T> onPinchAction(@Nullable OnPinchAction onPinchAction) {
        super.onPinchAction(onPinchAction);
        return this;
    }

    @Override public void draw(@NonNull Canvas canvas) {
        if (optimize) {
            D3ArcDrawer.drawArcs(
                canvas, innerRadius(), outerRadius(), offsetX(), offsetY(),
                preComputedAngles.getValue(), paint, colors
            );
        } else {
            canvas.drawBitmap(preComputedArc.getValue(), 0F, 0F, null);
        }
        drawLabels(canvas);
    }

    @Override public D3Arc<T> lazyRecomputing(boolean lazyRecomputing) {
        super.lazyRecomputing(lazyRecomputing);
        return this;
    }

    private void drawLabels(@NonNull Canvas canvas) {
        if (labels == null) {
            return;
        }

        float offsetX = offsetX();
        float offsetY = offsetY();
        float[] coordinatesX = labelsValueRunnable.getValue().coordinatesX;
        float[] coordinatesY = labelsValueRunnable.getValue().coordinatesY;

        for (int i = 0; i < data.length; i++) {
            textPaint.setColor(ColorHelper.colorDependingOnBackground(colors[i % colors.length]));
            canvas.drawText(
                labels[i], coordinatesX[i] + offsetX, coordinatesY[i] + offsetY, textPaint
            );
        }
    }

    @Override public void prepareParameters() {
        if (lazyRecomputing && calculationNeeded == 0) {
            return;
        }
        preComputedAngles.setValue(anglesValueRunnable, anglesValueRunnable.getKey());
        preComputedLabels.setValue(labelsValueRunnable, labelsValueRunnable.getKey());
        if (!optimize) {
            preComputedArc.setValue(bitmapValueRunnable, bitmapValueRunnable.getKey());
        }
    }

    @Override public D3Arc<T> setClipRect(
        @NonNull D3FloatFunction leftLimit,
        @NonNull D3FloatFunction topLimit,
        @NonNull D3FloatFunction rightLimit,
        @NonNull D3FloatFunction bottomLimit
    ) {
        super.setClipRect(leftLimit, topLimit, rightLimit, bottomLimit);
        return this;
    }

    @Override public D3Arc<T> deleteClipRect() {
        super.deleteClipRect();
        return this;
    }

    @Override protected void onDimensionsChange(float width, float height) {
        bitmapValueRunnable.resizeBitmap(width, height);
    }

    public D3Arc<T> optimize(boolean optimize) {
        this.optimize = optimize;
        updateNeeded();
        return this;
    }

    @Override public D3Arc<T> paint(@NonNull Paint paint) {
        super.paint(paint);
        paint.setStyle(Paint.Style.STROKE);
        return this;
    }
}
