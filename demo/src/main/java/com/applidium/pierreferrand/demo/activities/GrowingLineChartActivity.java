package com.applidium.pierreferrand.demo.activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.axes.AxisOrientation;
import com.applidium.pierreferrand.d3library.axes.D3Axis;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.axes.HorizontalAlignment;
import com.applidium.pierreferrand.d3library.line.D3Line;
import com.applidium.pierreferrand.d3library.mappers.D3FloatDataMapperFunction;
import com.applidium.pierreferrand.d3library.scale.D3Converter;
import com.applidium.pierreferrand.d3library.scale.D3LabelFunction;
import com.applidium.pierreferrand.demo.R;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GrowingLineChartActivity extends Activity {
    private static final int MAXIMUM_SALES = 500;
    private static final int FRUIT_NUMBER = 3;
    private static final int DEFAULT_DATA_NUMBER = 150;
    private static final int ANIMATION_TIME = 2500;

    @BindView(R.id.d3view) D3View view;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
        ButterKnife.bind(this);

        final D3Axis<Integer> salesAxis =
            new D3Axis<>(AxisOrientation.RIGHT, Integer.class)
                .domain(new Integer[]{0, FRUIT_NUMBER * MAXIMUM_SALES});

        final D3Axis<DateTime> timeAxis =
            new D3Axis<DateTime>(AxisOrientation.BOTTOM)
                .domain(new DateTime[]{
                    new DateTime().plusDays(DEFAULT_DATA_NUMBER / 3),
                    new DateTime().plusDays(DEFAULT_DATA_NUMBER * 2 / 3)
                })
                .converter(getDateTimeConverter())
                .labelFunction(new D3LabelFunction<DateTime>() {
                    String format = "dd/MM/yyyy hh:mm:ss";

                    @Override public String getLabel(DateTime object) {
                        return object.toString(format);
                    }
                })
                .legendHorizontalAlignment(HorizontalAlignment.CENTER);

        Sales[] sales = buildSales(DEFAULT_DATA_NUMBER);
        D3Line<Sales> line =
            new D3Line<Sales>(sales)
                .x(new D3FloatDataMapperFunction<Sales>() {
                    @Override public float compute(Sales object, int position, Sales[] data) {
                        return timeAxis.scale().value(object.date);
                    }
                })
                .y(new D3FloatDataMapperFunction<Sales>() {
                    @Override public float compute(
                        Sales object, int position, Sales[] data
                    ) {
                        return salesAxis.scale().value(
                            (int) object.strawberries.getAnimatedValue()
                                + (int) object.apples.getAnimatedValue()
                                + (int) object.bananas.getAnimatedValue()
                        );
                    }
                })
                .setClipRect(
                    new D3FloatFunction() {
                        @Override public float getFloat() {
                            return 0.05f * view.getWidth();
                        }
                    },
                    new D3FloatFunction() {
                        @Override public float getFloat() {
                            return 0F;
                        }
                    },
                    new D3FloatFunction() {
                        @Override public float getFloat() {
                            return view.getWidth();
                        }
                    },
                    new D3FloatFunction() {
                        @Override public float getFloat() {
                            return view.getHeight() * 0.95f;
                        }
                    }
                )
                .lazyRecomputing(false);
        line.paint().setColor(0xFF0066CC);
        view.add(line);
        view.add(salesAxis);
        view.add(timeAxis);
    }

    @NonNull private D3Converter<DateTime> getDateTimeConverter() {
        return new D3Converter<DateTime>() {
            @Override public float convert(DateTime toConvert) {
                return toConvert.getMillis();
            }

            @Override public DateTime invert(float toInvert) {
                return new DateTime().withMillis((long) toInvert);
            }
        };
    }

    @NonNull private Sales[] buildSales(int size) {
        Sales[] result = new Sales[size];
        for (int i = 0; i < size; i++) {
            result[i] = new Sales(
                new DateTime().plusDays(i + 1),
                (int) (Math.random() * MAXIMUM_SALES),
                (int) (Math.random() * MAXIMUM_SALES),
                (int) (Math.random() * MAXIMUM_SALES)
            );
        }
        return result;
    }

    static class Sales {
        @NonNull final DateTime date;
        final ValueAnimator apples;
        final ValueAnimator bananas;
        final ValueAnimator strawberries;

        private Sales(
            @NonNull DateTime date, int soldApples, int soldBananas, int soldStrawberries
        ) {
            this.date = date;
            apples = setupAnimator(soldApples);
            bananas = setupAnimator(soldBananas);
            strawberries = setupAnimator(soldStrawberries);
        }

        private static ValueAnimator setupAnimator(int apples) {
            ValueAnimator result = ValueAnimator.ofInt(0, apples);
            result.setDuration(ANIMATION_TIME);
            result.start();
            return result;
        }
    }

    @Override protected void onResume() {
        super.onResume();
        view.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        view.onPause();
    }
}
