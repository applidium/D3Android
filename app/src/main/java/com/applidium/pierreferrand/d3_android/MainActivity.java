package com.applidium.pierreferrand.d3_android;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.Line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.Line.D3Line;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.axes.AxisOrientation;
import com.applidium.pierreferrand.d3library.axes.D3Axis;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.axes.D3RangeFunction;
import com.applidium.pierreferrand.d3library.axes.HorizontalAlignment;
import com.applidium.pierreferrand.d3library.axes.VerticalAlignment;
import com.applidium.pierreferrand.d3library.curve.D3Curve;
import com.applidium.pierreferrand.d3library.scale.D3Converter;

public class MainActivity extends Activity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final D3View view = (D3View) findViewById(R.id.test);

        final D3Axis<Float> horizontalAxis = new D3Axis<>(AxisOrientation.TOP);
        final D3Axis<Float> verticalAxis = new D3Axis<>(AxisOrientation.LEFT);

        view.add(
            horizontalAxis
                .domain(new Float[]{0f, 100f})
                .offsetX(0f)
                .offsetY(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return horizontalAxis.height() * 0.95f;
                    }
                })
                .range(new D3RangeFunction() {
                    @Override public Float[] getRange() {
                        return new Float[]{0.15f * view.getWidth(), 0.95f * view.getWidth()};
                    }
                })
                .converter(new D3Converter<Float>() {
                    @Override public float convert(Float toConvert) {
                        return toConvert;
                    }

                    @Override public Float invert(float toInvert) {
                        return toInvert;
                    }
                })
                .ticks(5)
                .legendHorizontalAlignment(HorizontalAlignment.CENTER)
                .textSizeInPixels(40)
                .axisColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .legendColor(ContextCompat.getColor(this, R.color.colorAccent))
        );

        view.add(
            verticalAxis
                .domain(new Float[]{0f, 1200f})
                .ticks(3)
                .offsetY(0f)
                .converter(new D3Converter<Float>() {
                    @Override public float convert(Float toConvert) {
                        return toConvert;
                    }

                    @Override public Float invert(float toInvert) {
                        return toInvert;
                    }
                })

                .offsetX(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return verticalAxis.width() * 0.15f;
                    }
                })
                .legendVerticalAlignment(VerticalAlignment.CENTER)
                .textSizeInPixels(30)
                .onClickAction(new OnClickAction() {
                    @Override public void onClick(float X, float Y) {
                        Log.v(
                            "DebugActionTest",
                            "domain : " + verticalAxis.scale().domain()[0] + " <> " + verticalAxis
                                .scale()
                                .domain()[1]
                        );
                    }
                })
        );

        Float[] data = new Float[]{750f, 600f, 400f, 450f, 600f, 700f, 750f, 500f};
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xFFFABE8E);
        paint.setStrokeWidth(15f);

        final D3Curve<Float> curve = new D3Curve<>(data);
        curve.paint(paint)
             .pointsNumber(200)
             .y(new D3DataMapperFunction<Float>() {
                 @Override public float compute(Float object, int position, Float[] data) {
                     return verticalAxis.scale().value(object);
                 }
             }).x(new D3DataMapperFunction<Float>() {
            @Override public float compute(Float object, int position, Float[] data) {
                return horizontalAxis.scale().value(100f * position / (data.length - 1));
            }
        });
        view.add(curve);

        final D3Line<Float> line = new D3Line<>(data);
        line.y(new D3DataMapperFunction<Float>() {
            @Override public float compute(Float object, int position, Float[] data) {
                return verticalAxis.scale().value((float) object);
            }
        }).x(new D3DataMapperFunction<Float>() {
            @Override public float compute(Float object, int position, Float[] data) {
                return horizontalAxis.scale().value(100f * position / (data.length - 1));
            }
        });
        view.add(line);
    }
}
