package com.applidium.pierreferrand.d3_android;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.applidium.pierreferrand.d3library.action.Action;
import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.Line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.axes.AxisOrientation;
import com.applidium.pierreferrand.d3library.axes.D3Axis;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.axes.D3RangeFunction;
import com.applidium.pierreferrand.d3library.curve.D3Curve;
import com.applidium.pierreferrand.d3library.scale.D3Converter;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

public class MainActivity extends Activity {

    TextView temperatureValue;
    D3View view;
    TextView lightValue;
    D3Axis<Float> leftAxis;
    D3Axis<Float> rightAxis;
    D3Axis<DateTime> timeAxis;
    D3Curve<LightData> lightCurve;
    D3Curve<TemperatureData> temperatureCurve;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        JodaTimeAndroid.init(this);

        view = (D3View) findViewById(R.id.test);
        temperatureValue = (TextView) findViewById(R.id.temperatureValue);
        lightValue = (TextView) findViewById(R.id.lightValue);

        leftAxis =
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
                .domain(new Float[]{25f, 730f})
                .range(new D3RangeFunction<Float>() {
                    @Override public Float[] getRange() {
                        return new Float[]{view.getHeight() * 0.8f, view.getHeight() * 0.1f};
                    }
                });

        rightAxis =
            new D3Axis<Float>(AxisOrientation.LEFT)
                .offsetX(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return view.getWidth() * 0.95f;
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
                .domain(new Float[]{23.5f, 25f})
                .range(new D3RangeFunction<Float>() {
                    @Override public Float[] getRange() {
                        return new Float[]{view.getHeight() * 0.8f, view.getHeight() * 0.1f};
                    }
                });

        timeAxis =
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

        Paint lightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightPaint.setColor(0XFF00F0F0);
        lightPaint.setStrokeWidth(12f);

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
        TemperatureData[] temperatureData =
            new TemperatureData[]{
                new TemperatureData(1, 24.2f),
                new TemperatureData(2, 24.3f),
                new TemperatureData(4, 24.3f),
                new TemperatureData(5, 24.3f),
                new TemperatureData(7, 24.3f),
                new TemperatureData(8, 24.3f),
                new TemperatureData(10, 24.3f),
                new TemperatureData(11, 24.3f),
                new TemperatureData(14, 24.4f)
            };

        lightCurve =
            new D3Curve<>(lightData)
                .x(new D3DataMapperFunction<LightData>() {
                    @Override
                    public float compute(LightData object, int position, LightData[] data) {
                        return timeAxis.scale().value(object.date);
                    }
                })
                .y(new D3DataMapperFunction<LightData>() {
                    @Override
                    public float compute(LightData object, int position, LightData[] data) {
                        return leftAxis.scale().value(object.value);
                    }
                }).paint(lightPaint);

        Paint temperaturePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        temperaturePaint.setColor(0XFFFF0000);
        temperaturePaint.setStrokeWidth(12f);


        temperatureCurve =
            new D3Curve<>(temperatureData)
                .x(new D3DataMapperFunction<TemperatureData>() {
                    @Override
                    public float compute(
                        TemperatureData object,
                        int position,
                        TemperatureData[] data
                    ) {
                        return timeAxis.scale().value(object.date);
                    }
                })
                .y(new D3DataMapperFunction<TemperatureData>() {
                    @Override
                    public float compute(
                        TemperatureData object,
                        int position,
                        TemperatureData[] data
                    ) {
                        return rightAxis.scale().value(object.value);
                    }
                })
                .paint(temperaturePaint);
        view.afterDrawActions.add(new Action() {
            @Override public void execute() {
                updateLightValue();
                updateTemperatureValue();
            }
        });

        view.add(timeAxis);
        view.add(leftAxis);
        view.add(lightCurve);
        view.add(rightAxis);
        view.add(temperatureCurve);
    }

    private void updateLightValue() {
        float value = lightCurve.interpolateValue(view.getWidth() / 2);
        float offset = computeOffset(value);
        lightValue.setY(offset);
        lightValue.setText(leftAxis.scale().invert(value).toString());
    }

    private float computeOffset(float value) {
        return value < view.getHeight() / 2f ?
            Math.max(value, 0f) :
            Math.min(value, view.getHeight() * 0.9f);
    }

    private void updateTemperatureValue() {
        float value = temperatureCurve.interpolateValue(view.getWidth() / 2);
        float offset = computeOffset(value);
        temperatureValue.setY(offset);
        temperatureValue.setText(rightAxis.scale().invert(value).toString());
    }

    private class LightData {
        DateTime date;
        Float value;

        LightData(int sec, float value) {
            date = new DateTime().withHourOfDay(11).withMinuteOfHour(17).withSecondOfMinute(sec);
            this.value = value;
        }
    }

    private class TemperatureData {
        DateTime date;
        Float value;

        TemperatureData(int sec, float value) {
            date = new DateTime().withHourOfDay(11).withMinuteOfHour(17).withSecondOfMinute(sec);
            this.value = value;
        }
    }
}
