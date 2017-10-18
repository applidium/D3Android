/*
 * Copyright 2017, Fabernovel Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fabernovel.d3library.demo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.fabernovel.d3library.demo.R;
import com.fabernovel.d3library.D3View;
import com.fabernovel.d3library.action.OnClickAction;
import com.fabernovel.d3library.area.D3Area;
import com.fabernovel.d3library.axes.AxisOrientation;
import com.fabernovel.d3library.axes.D3Axis;
import com.fabernovel.d3library.axes.D3FloatFunction;
import com.fabernovel.d3library.axes.HorizontalAlignment;
import com.fabernovel.d3library.mappers.D3FloatDataMapperFunction;
import com.fabernovel.d3library.scale.D3Converter;
import com.fabernovel.d3library.scale.D3LabelFunction;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MultipleAreasActivity extends Activity {
    private static final int MAXIMUM_SALES = 500;
    private static final int FRUIT_NUMBER = 3;
    private static final int DEFAULT_DATA_NUMBER = 15;

    @BindView(R.id.d3view) D3View view;
    @BindView(R.id.message) TextView message;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_message_activity);
        ButterKnife.bind(this);

        message.setText(getString(R.string.multiple_areas_activity_message));

        final D3Axis<Integer> salesAxis =
            new D3Axis<>(AxisOrientation.RIGHT, Integer.class)
                .domain(new Integer[]{0, FRUIT_NUMBER * MAXIMUM_SALES})
                .onScrollAction(null)
                .onPinchAction(null);

        final D3Axis<DateTime> timeAxis =
            new D3Axis<DateTime>(AxisOrientation.BOTTOM)
                .domain(new DateTime[]{new DateTime(),
                                       new DateTime().plusDays(DEFAULT_DATA_NUMBER * 2)
                })
                .converter(getDateTimeConverter())
                .labelFunction(new D3LabelFunction<DateTime>() {
                    String format = "dd/MM/yyyy hh:mm:ss";

                    @Override public String getLabel(DateTime object) {
                        return object.toString(format);
                    }
                })
                .legendHorizontalAlignment(HorizontalAlignment.CENTER);

        final Sales[] sales = buildSales(DEFAULT_DATA_NUMBER, new Sales[0]);
        final D3Area<Sales> areaApples =
            new D3Area<>(sales)
                .x(new D3FloatDataMapperFunction<Sales>() {
                    @Override public float compute(Sales object, int position, Sales[] data) {
                        return timeAxis.scale().value(object.date);
                    }
                })
                .y(new D3FloatDataMapperFunction<Sales>() {
                    @Override public float compute(
                        Sales object, int position, Sales[] data
                    ) {
                        return salesAxis.scale().value(object.apples);
                    }
                })
                .ground(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return salesAxis.scale().value(0);
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
                );
        areaApples.paint().setColor(0xFF99CC00);
        final D3Area<Sales> areaBananas =
            new D3Area<>(sales)
                .x(new D3FloatDataMapperFunction<Sales>() {
                    @Override public float compute(Sales object, int position, Sales[] data) {
                        return timeAxis.scale().value(object.date);
                    }
                })
                .y(new D3FloatDataMapperFunction<Sales>() {
                    @Override public float compute(
                        Sales object, int position, Sales[] data
                    ) {
                        return salesAxis.scale().value(object.apples + object.bananas);
                    }
                })
                .ground(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return salesAxis.scale().value(0);
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
                );
        areaBananas.paint().setColor(0xFFFFFF00);
        final D3Area<Sales> areaStrawberries =
            new D3Area<>(sales)
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
                            object.strawberries + object.apples + object.bananas
                        );
                    }
                })
                .ground(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return salesAxis.scale().value(0);
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
                );
        areaApples.onClickAction(new OnClickAction() {
            @Override public void onClick(float X, float Y) {
                Sales[] newSales = buildSales(
                    areaStrawberries.data().length + 1, areaStrawberries.data()
                );
                areaApples.data(newSales);
                areaApples.updateNeeded();
                areaBananas.data(newSales);
                areaBananas.updateNeeded();
                areaStrawberries.data(newSales);
                areaStrawberries.updateNeeded();
            }
        });
        areaStrawberries.paint().setColor(0xFFD20E07);
        view.add(areaStrawberries);
        view.add(areaBananas);
        view.add(areaApples);
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

    @NonNull private Sales[] buildSales(int size, Sales[] first) {
        Sales[] result = new Sales[size];
        for (int i = 0; i < first.length; i++) {
            result[i] = first[i];
        }
        for (int i = first.length; i < size; i++) {
            result[i] = new Sales(
                new DateTime().plusDays(i),
                (int) (Math.random() * MAXIMUM_SALES / 2 + MAXIMUM_SALES / 3),
                (int) (Math.random() * MAXIMUM_SALES / 2 + MAXIMUM_SALES / 3),
                (int) (Math.random() * MAXIMUM_SALES / 2 + MAXIMUM_SALES / 3)
            );
        }
        return result;
    }

    static class Sales {
        @NonNull final DateTime date;
        final int apples;
        final int bananas;
        final int strawberries;

        private Sales(
            @NonNull DateTime date, int soldApples, int soldBananas, int soldStrawberries
        ) {
            this.date = date;
            apples = soldApples;
            bananas = soldBananas;
            strawberries = soldStrawberries;
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
