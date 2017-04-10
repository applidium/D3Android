package com.applidium.pierreferrand.d3_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.Line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.axes.AxisOrientation;
import com.applidium.pierreferrand.d3library.axes.D3Axis;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.axes.D3RangeFunction;
import com.applidium.pierreferrand.d3library.barchart.D3BarChart;
import com.applidium.pierreferrand.d3library.scale.D3Converter;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

public class MainActivity extends Activity {

    D3View view;

    D3Axis<Float> lightAxis;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        JodaTimeAndroid.init(this);

        view = (D3View) findViewById(R.id.test);

        lightAxis =
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
                .domain(new Float[]{0f, 730f})
                .range(new D3RangeFunction<Float>() {
                    @Override public Float[] getRange() {
                        return new Float[]{view.getHeight() * 0.8f, view.getHeight() * 0.1f};
                    }
                });

        final D3Axis<DateTime> timeAxis =
            new D3Axis<DateTime>(AxisOrientation.TOP)
                .domain(new DateTime[]{
                    new DateTime().withHourOfDay(11).withMinuteOfHour(17).withSecondOfMinute(1),
                    new DateTime().withHourOfDay(11).withMinuteOfHour(17).withSecondOfMinute(14),
                    }
                ).range(new D3RangeFunction<Float>() {
                @Override public Float[] getRange() {
                    return new Float[]{0.05f * view.getWidth(), 0.95f * view.getWidth()};
                }
            }).converter(new D3Converter<DateTime>() {
                @Override public float convert(DateTime toConvert) {
                    return (float) (toConvert.getMillis() - 1491470254080L);
                }

                @Override public DateTime invert(float toInvert) {

                    return new DateTime().withMillis((long) toInvert + 1491470254080L);
                }
            }).offsetY(new D3FloatFunction() {
                @Override public float getFloat() {
                    return (float) (view.getHeight() * 0.98);
                }
            });

        LightData[] lightData = new LightData[]{
            new LightData(1, 70f),
            new LightData(2, 510f),
            new LightData(4, 434f),
            new LightData(5, 431f),
            new LightData(7, 428f),
            new LightData(8, 457f),
            new LightData(10, 210f),
            new LightData(11, 100f),
            new LightData(14, 100f)
        };


        D3BarChart<LightData> lightBarChart =
            new D3BarChart<>(lightData)
                .dataHeight(new D3DataMapperFunction<LightData>() {
                    @Override public float compute(
                        LightData object, int position, LightData[] data
                    ) {
                        return lightAxis.scale().value(0f)
                            - lightAxis.scale().value(object.value);
                    }
                }).dataWidth(new D3FloatFunction() {
                @Override public float getFloat() {
                    return 50f;
                }
            }).x(new D3DataMapperFunction<LightData>() {
                D3Converter<DateTime> converter = timeAxis.scale().converter();

                @Override public float compute(
                    LightData object, int position, LightData[] data
                ) {
                    return timeAxis.scale().value(
                        converter.invert(timeAxis.scale().ticks(data.length + 1)[position + 1])
                    );
                }
            }).y(new D3DataMapperFunction<LightData>() {
                @Override public float compute(
                    LightData object, int position, LightData[] data
                ) {
                    return lightAxis.scale().value(0f);
                }
            });

        view.add(lightAxis);
        view.add(lightBarChart);
    }

    private class LightData {
        DateTime date;
        Float value;

        LightData(int sec, float value) {
            date = new DateTime().withHourOfDay(11).withMinuteOfHour(17).withSecondOfMinute(sec);
            this.value = value;
        }
    }
}
