package com.applidium.pierreferrand.d3_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.axes.AxisOrientation;
import com.applidium.pierreferrand.d3library.axes.D3Axis;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.axes.D3RangeFunction;
import com.applidium.pierreferrand.d3library.boxplot.D3BoxPlot;
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
                .range(new D3RangeFunction<Float>() {
                    @Override public Float[] getRange() {
                        return new Float[]{view.getHeight() * 0.8f, view.getHeight() * 0.1f};
                    }
                });

        final D3BoxPlot boxPlot = new D3BoxPlot()
            .min(25f)
            .max(57f)
            .scale(numberAxis.scale())
            .median(33f)
            .lowerQuartile(29f)
            .dataWidth(50f)
            .offsetX(new D3FloatFunction() {
                @Override public float getFloat() {
                    return view.getWidth() / 2f;
                }
            })
            .upperQuartile(55f);
        boxPlot.onClickAction(new OnClickAction() {
            @Override public void onClick(float X, float Y) {
                float[] data = new float[50];
                for (int i = 0; i < data.length; i++) {
                    data[i] = (float) (Math.random() * 100);
                }
                boxPlot.data(data);
            }
        });

        view.add(numberAxis);
        view.add(boxPlot);

    }
}
