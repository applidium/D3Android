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

package com.applidium.pierreferrand.demo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.arc.D3Arc;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.mappers.D3FloatDataMapperFunction;
import com.applidium.pierreferrand.d3library.mappers.D3IntDataMapperFunction;
import com.applidium.pierreferrand.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ObjectArcsActivity extends Activity {
    @BindView(R.id.d3view) D3View view;
    @BindView(R.id.message) TextView message;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_message_activity);
        ButterKnife.bind(this);
        message.setText(getString(R.string.object_arcs_activity_message));

        final FruitSale[] data = buildData();

        final D3Arc<FruitSale> arc = new D3Arc<>(data)
            .weights(new D3FloatDataMapperFunction<FruitSale>() {
                @Override public float compute(
                    FruitSale object, int position, FruitSale[] data
                ) {
                    return object.sales;
                }
            })
            .innerRadius(new D3FloatFunction() {
                @Override public float getFloat() {
                    return Math.min(view.getWidth(), view.getHeight()) / 4F;
                }
            })
            .padAngle(10)
            .colors(new D3IntDataMapperFunction<FruitSale>() {
                @Override public int compute(
                    FruitSale object, int position, FruitSale[] data
                ) {
                    return object.color;
                }
            })
            .lazyRecomputing(false);
        arc.onClickAction(new OnClickAction() {
            @Override public void onClick(float X, float Y) {
                FruitSale fruitSale = arc.dataFromPosition(X, Y);
                if (fruitSale == null) {
                    int sale = (int) (Math.random() * 3);
                    data[sale].sales += 5;
                } else {
                    fruitSale.sales += 5;
                }
            }
        });
        view.add(arc);
    }

    @NonNull private FruitSale[] buildData() {
        return new FruitSale[]{
            new FruitSale("Banana", 0xFFFFFF00),
            new FruitSale("Apple", 0xFF99CC00),
            new FruitSale("Strawberry", 0xFFD20E07)
        };
    }

    @Override protected void onResume() {
        super.onResume();
        view.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        view.onPause();
    }

    private static class FruitSale {
        private String fruitName;
        private int sales;
        private int color;

        FruitSale(String fruitName, int color) {
            this.fruitName = fruitName;
            this.sales = 10;
            this.color = color;
        }

        @Override public String toString() {
            return fruitName + " (" + sales + ")";
        }
    }
}
