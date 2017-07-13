package com.applidium.pierreferrand.d3_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.Line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.action.PinchType;
import com.applidium.pierreferrand.d3library.action.ScrollDirection;
import com.applidium.pierreferrand.d3library.axes.AxisOrientation;
import com.applidium.pierreferrand.d3library.axes.D3Axis;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.axes.D3RangeFunction;
import com.applidium.pierreferrand.d3library.barchart.D3StackBarChart;
import com.applidium.pierreferrand.d3library.scale.D3Converter;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    D3View view;

    D3Axis<Integer> numberAxis;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        JodaTimeAndroid.init(this);

        view = (D3View) findViewById(R.id.test);

        numberAxis =
            new D3Axis<Integer>(AxisOrientation.RIGHT)
                .offsetX(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return view.getWidth() * 0.05f;
                    }
                })
                .converter(new D3Converter<Integer>() {
                    @Override public float convert(Integer toConvert) {
                        return toConvert;
                    }

                    @Override public Integer invert(float toInvert) {
                        return (int) toInvert;
                    }
                })
                .domain(new Integer[]{0, 8000})
                .range(new D3RangeFunction<Float>() {
                    @Override public Float[] getRange() {
                        return new Float[]{view.getHeight() * 0.8f, view.getHeight() * 0.1f};
                    }
                });

        final D3Axis<Integer> saleAxis =
            new D3Axis<Integer>(AxisOrientation.TOP)
                .domain(new Integer[]{0, 5})
                .ticks(6)
                .range(new D3RangeFunction<Float>() {
                    @Override public Float[] getRange() {
                        return new Float[]{0.05f * view.getWidth(), 0.95f * view.getWidth()};
                    }
                }).converter(new D3Converter<Integer>() {
                @Override public float convert(Integer toConvert) {
                    return toConvert;
                }

                @Override public Integer invert(float toInvert) {
                    return (int) toInvert;
                }
            }).offsetY(new D3FloatFunction() {
                @Override public float getFloat() {
                    return (float) (view.getHeight() * 0.98);
                }
            })
                .onScrollAction(new OnScrollAction() {
                    @Override public void onScroll(
                        ScrollDirection direction,
                        float coordinateX,
                        float coordinateY,
                        float dX,
                        float dY
                    ) {
                    }
                })
                .onPinchAction(new OnPinchAction() {
                    @Override public void onPinch(
                        PinchType pinchType,
                        float coordinateStaticX,
                        float coordinateStaticY,
                        float coordinateMobileX,
                        float coordinateMobileY,
                        float dX,
                        float dY
                    ) {
                    }
                });

        Sales[] sales = new Sales[]{
            new Sales(1, 3840, 1920, 960, 400),
            new Sales(2, 1600, 1440, 960, 400),
            new Sales(3, 640, 960, 940, 400),
            new Sales(4, 320, 480, 640, 400)
        };

        D3StackBarChart<Sales> stackBarChart =
            new D3StackBarChart<>(sales, 4)
                .x(new D3DataMapperFunction<Sales>() {
                    @Override public float compute(
                        Sales object, int position, Sales[] data
                    ) {
                        return saleAxis.scale().value(object.sale);
                    }
                })
                .y(new D3DataMapperFunction<Sales>() {
                    @Override public float compute(
                        Sales object, int position, Sales[] data
                    ) {
                        return numberAxis.scale().value(0);
                    }
                })
                .dataWidth(100f)
                .colors(new int[][]{
                    new int[]{0xFF0000FF},
                    new int[]{0xFF00FF00},
                    new int[]{0xFF0000FF},
                    new int[]{0xFFFF0000},
                    });
        List<D3DataMapperFunction<Sales>> heights = new ArrayList<>();
        heights.add(
            new D3DataMapperFunction<Sales>() {
                @Override public float compute(
                    Sales object, int position, Sales[] data
                ) {
                    return numberAxis.scale().value(0) - numberAxis
                        .scale()
                        .value(object.apples);
                }
            });
        heights.add(
            new D3DataMapperFunction<Sales>() {
                @Override public float compute(
                    Sales object, int position, Sales[] data
                ) {
                    return numberAxis.scale().value(0) - numberAxis
                        .scale()
                        .value(object.bananas);
                }
            });
        heights.add(
            new D3DataMapperFunction<Sales>() {
                @Override public float compute(
                    Sales object, int position, Sales[] data
                ) {
                    return numberAxis.scale().value(0) - numberAxis
                        .scale()
                        .value(object.cherries);
                }
            });
        heights.add(
            new D3DataMapperFunction<Sales>() {
                @Override public float compute(
                    Sales object, int position, Sales[] data
                ) {
                    return numberAxis.scale().value(0) - numberAxis
                        .scale()
                        .value(object.dates);
                }
            });
        stackBarChart.dataHeight(heights);
        view.add(numberAxis);
        view.add(saleAxis);
        view.add(stackBarChart);
    }

    class Sales {
        int sale;
        int apples;
        int bananas;
        int cherries;
        int dates;

        private Sales(int sale, int apples, int bananas, int cherries, int dates) {
            this.sale = sale;
            this.apples = apples;
            this.bananas = bananas;
            this.cherries = cherries;
            this.dates = dates;
        }
    }
}
