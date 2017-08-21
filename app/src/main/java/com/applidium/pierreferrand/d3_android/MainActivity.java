package com.applidium.pierreferrand.d3_android;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.axes.AxisOrientation;
import com.applidium.pierreferrand.d3library.axes.D3Axis;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.axes.D3RangeFunction;
import com.applidium.pierreferrand.d3library.line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.line.D3Line;
import com.applidium.pierreferrand.d3library.scale.D3Converter;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

public class MainActivity extends Activity {

    D3View view;
    D3Axis<Float> leftAxis;
    D3Axis<Float> timeAxis;
    D3Line<LightData> lightCurve;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        JodaTimeAndroid.init(this);

        view = (D3View) findViewById(R.id.test);

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
                .range(new D3RangeFunction() {
                    @Override public float[] getRange() {
                        return new float[]{view.getHeight() * 0.8f, view.getHeight() * 0.1f};
                    }
                });

        timeAxis =
            new D3Axis<Float>(AxisOrientation.TOP)
                .domain(new Float[]{1f, 14f,})
                .range(new D3RangeFunction() {
                    @Override public float[] getRange() {
                        return new float[]{0.05f * view.getWidth(), 0.95f * view.getWidth()};
                    }
                }).converter(new D3Converter<Float>() {
                @Override public float convert(Float toConvert) {
                    return toConvert;
                }

                @Override public Float invert(float toInvert) {
                    return toInvert;
                }
            }).offsetY(new D3FloatFunction() {
                @Override public float getFloat() {
                    return (float) (view.getHeight() * 0.98);
                }
            });

        Paint lightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightPaint.setColor(0XFF00F0F0);
        lightPaint.setStrokeWidth(12f);
        lightPaint.setStyle(Paint.Style.STROKE);

        LightData[] lightData = new LightData[500];
        for (int i = 0; i < lightData.length; i++) {
            lightData[i] = new LightData(i, (float) Math.random() * 400f + 125f);
        }
        lightCurve =
            new D3Line<>(lightData)
                .x(new D3DataMapperFunction<LightData>() {
                    @Override
                    public float compute(LightData object, int position, LightData[] data) {
                        return timeAxis.scale().value(object.test);
                    }
                })
                .y(new D3DataMapperFunction<LightData>() {
                    @Override
                    public float compute(LightData object, int position, LightData[] data) {
                        return leftAxis.scale().value(object.value);
                    }
                }).paint(lightPaint)
                .setClipRect(
                    new D3FloatFunction() {
                        @Override public float getFloat() {
                            return view.getWidth() * 0.05f;
                        }
                    },
                    new D3FloatFunction() {
                        @Override public float getFloat() {
                            return 0;
                        }
                    },
                    new D3FloatFunction() {
                        @Override public float getFloat() {
                            return view.getWidth();

                        }
                    },
                    new D3FloatFunction() {
                        @Override public float getFloat() {
                            return (float) (view.getHeight() * 0.98);
                        }
                    }
                )
                .onClickAction(
                    new OnClickAction() {
                        boolean lazyRecomputing = true;

                        @Override public void onClick(float X, float Y) {
                            lazyRecomputing = !lazyRecomputing;
                            lightCurve.lazyRecomputing(lazyRecomputing);
                        }
                    }
                );

        Paint temperaturePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        temperaturePaint.setColor(0XFFFF0000);
        temperaturePaint.setStrokeWidth(12f);

        view.add(timeAxis.ticks(10));
        view.add(leftAxis.ticks(10));
        view.add(lightCurve);
        view.add(new D3Drawable() {
            @Override public void draw(@NonNull Canvas canvas) {
            }
        }.lazyRecomputing(false));
        view.setMinimumTimePerFrame(17);
    }

    private static class LightData {
        DateTime date;
        Float value;
        float test;

        LightData(int sec, float value) {
            date = new DateTime()
                .withHourOfDay(11)
                .withMinuteOfHour(17 + sec / 60)
                .withSecondOfMinute(sec % 60);
            test = sec;
            this.value = value;
        }
    }

    @Override protected void onPause() {
        super.onPause();
        view.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        view.onResume();
    }
}
