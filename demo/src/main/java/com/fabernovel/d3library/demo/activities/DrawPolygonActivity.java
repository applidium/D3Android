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
import com.fabernovel.d3library.action.OnPinchAction;
import com.fabernovel.d3library.action.OnScrollAction;
import com.fabernovel.d3library.action.PinchType;
import com.fabernovel.d3library.action.ScrollDirection;
import com.fabernovel.d3library.polygon.D3Polygon;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DrawPolygonActivity extends Activity {
    @BindView(R.id.d3view) D3View view;
    @BindView(R.id.message) TextView message;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_message_activity);
        ButterKnife.bind(this);
        message.setText(getString(R.string.drawing_polygon_activity_message));

        final D3Polygon polygon = new D3Polygon(new float[0])
            .lazyRecomputing(false);
        polygon
            .onClickAction(new OnClickAction() {
                @Override public void onClick(float X, float Y) {
                    AddPoint(X, Y, polygon);
                }
            })
            .onScrollAction(new OnScrollAction() {
                private static final int FREQUENCY_TAKEN_POINT = 5;
                int count = 0;

                @Override public void onScroll(
                    ScrollDirection direction, float coordinateX, float coordinateY,
                    float dX, float dY
                ) {
                    if (count == 0) {
                        AddPoint(coordinateX, coordinateY, polygon);
                    }
                    count = (count + 1) % FREQUENCY_TAKEN_POINT;
                }
            })
            .onPinchAction(new OnPinchAction() {
                @Override public void onPinch(
                    PinchType pinchType, float coordinateStaticX, float coordinateStaticY,
                    float coordinateMobileX, float coordinateMobileY, float dX, float dY
                ) {
                    polygon.coordinates(new float[0]);
                }
            });

        view.add(polygon);
    }

    private void AddPoint(float x, float y, @NonNull D3Polygon polygon) {
        float[] coordinates = polygon.coordinates();
        float[] newCoordinates = new float[coordinates.length + 2];
        for (int i = 0; i < coordinates.length; i++) {
            newCoordinates[i] = coordinates[i];
        }
        newCoordinates[coordinates.length] = x;
        newCoordinates[coordinates.length + 1] = y;
        polygon.coordinates(newCoordinates);
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
