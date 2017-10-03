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
import com.fabernovel.d3library.axes.AxisOrientation;
import com.fabernovel.d3library.axes.D3Axis;
import com.fabernovel.d3library.axes.D3FloatFunction;
import com.fabernovel.d3library.boxplot.D3BoxPlot;
import com.fabernovel.d3library.mappers.D3FloatDataMapperFunction;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BoxPlotActivity extends Activity {
    private static final float MAXIMUM_SALES = 500F;
    private static final int DEFAULT_DATA_NUMBER = 4;

    @BindView(R.id.d3view) D3View view;
    @BindView(R.id.message) TextView message;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_message_activity);
        ButterKnife.bind(this);

        message.setText(getString(R.string.multiple_areas_activity_message));

        final D3Axis<Float> salesAxis =
            new D3Axis<>(AxisOrientation.RIGHT, Float.class)
                .domain(new Float[]{0F, MAXIMUM_SALES});

        final Sales[] sales = buildSales(DEFAULT_DATA_NUMBER, new Sales[0]);

        final D3BoxPlot<Sales> applesBoxPlot =
            new D3BoxPlot<>(sales)
                .dataWidth(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return view.getWidth() * 0.20F;
                    }
                })
                .offsetX(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return view.getWidth() * 0.10F;
                    }
                })
                .dataMapper(new D3FloatDataMapperFunction<Sales>() {
                    @Override public float compute(Sales object, int position, Sales[] data) {
                        return object.apples;
                    }
                })
                .scale(salesAxis.scale());
        applesBoxPlot.paint().setColor(0xFF99CC00);

        final D3BoxPlot<Sales> bananasBoxPlot =
            new D3BoxPlot<>(sales)
                .dataWidth(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return view.getWidth() * 0.20F;
                    }
                })
                .offsetX(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return view.getWidth() * 0.40F;
                    }
                })
                .dataMapper(new D3FloatDataMapperFunction<Sales>() {
                    @Override public float compute(Sales object, int position, Sales[] data) {
                        return object.bananas;
                    }
                })
                .scale(salesAxis.scale());
        bananasBoxPlot.paint().setColor(0xFFFFFF00);

        final D3BoxPlot<Sales> strawberriesBoxPlot =
            new D3BoxPlot<>(sales)
                .dataWidth(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return view.getWidth() * 0.20F;
                    }
                })
                .offsetX(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return view.getWidth() * 0.70F;
                    }
                })
                .dataMapper(new D3FloatDataMapperFunction<Sales>() {
                    @Override public float compute(Sales object, int position, Sales[] data) {
                        return object.strawberries;
                    }
                })
                .scale(salesAxis.scale());
        strawberriesBoxPlot.paint().setColor(0xFFD20E07);

        applesBoxPlot.onClickAction(new OnClickAction() {
            @Override public void onClick(float X, float Y) {
                Sales[] data = applesBoxPlot.data();
                Sales[] newData = buildSales(data.length + 1, data);
                applesBoxPlot.data(newData);
                applesBoxPlot.updateNeeded();
                bananasBoxPlot.data(newData);
                bananasBoxPlot.updateNeeded();
                strawberriesBoxPlot.data(newData);
                strawberriesBoxPlot.updateNeeded();
            }
        });

        view.add(applesBoxPlot);
        view.add(bananasBoxPlot);
        view.add(strawberriesBoxPlot);
        view.add(salesAxis);
    }

    @NonNull private Sales[] buildSales(int size, Sales[] first) {
        Sales[] result = new Sales[size];
        for (int i = 0; i < first.length; i++) {
            result[i] = first[i];
        }
        for (int i = first.length; i < size; i++) {
            result[i] = new Sales(
                new DateTime().plusDays(i),
                (int) (Math.random() * MAXIMUM_SALES / 2 + MAXIMUM_SALES / 4),
                (int) (Math.random() * MAXIMUM_SALES * 2 / 3 + MAXIMUM_SALES / 6),
                (int) (Math.random() * MAXIMUM_SALES)
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
