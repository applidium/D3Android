package com.applidium.pierreferrand.d3_android;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.axes.AxisOrientation;
import com.applidium.pierreferrand.d3library.axes.D3Axis;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.axes.D3RangeFunction;
import com.applidium.pierreferrand.d3library.boxplot.D3BoxPlot;
import com.applidium.pierreferrand.d3library.line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.scale.D3Converter;

import net.danlew.android.joda.JodaTimeAndroid;

public class MainActivity extends Activity {

    D3View view;

    D3Axis<Float> numberAxis;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        JodaTimeAndroid.init(this);

        view = (D3View) findViewById(R.id.test);

        numberAxis =
            new D3Axis<Float>(AxisOrientation.RIGHT)
                .offsetX(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return view.getWidth() * 0.05f;
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
                .domain(new Float[]{0f, 100f})
                .range(new D3RangeFunction() {
                    float[] range = new float[2];

                    @Override public float[] getRange() {
                        range[0] = view.getHeight() * 0.8f;
                        range[1] = view.getHeight() * 0.1f;
                        return range;
                    }
                });

        Float[] data = new Float[50];
        rerollData(data);

        final D3BoxPlot<Float> boxPlot = new D3BoxPlot<>(data)
            .scale(numberAxis.scale())
            .dataWidth(50f)
            .offsetX(new D3FloatFunction() {
                @Override public float getFloat() {
                    return view.getWidth() / 2f;
                }
            })
            .setDataMapper(new D3DataMapperFunction<Float>() {
                @Override public float compute(Float object, int position, Float[] data) {
                    return object;
                }
            });
        boxPlot.onClickAction(new OnClickAction() {
            @Override public void onClick(float X, float Y) {
                boxPlot.updateNeeded();
                rerollData(boxPlot.data());
            }
        }).lazyRecomputing(true);

        view.add(numberAxis);
        view.add(boxPlot);
        view.add(new D3Drawable() {
            @Override public void draw(@NonNull Canvas canvas) {

            }
        }.lazyRecomputing(false));
    }

    private void rerollData(@NonNull Float[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = (float) (Math.random() * 100);
        }
    }
}
