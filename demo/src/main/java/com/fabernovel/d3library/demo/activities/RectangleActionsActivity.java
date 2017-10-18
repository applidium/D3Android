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
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.fabernovel.d3library.demo.R;
import com.fabernovel.d3library.D3View;
import com.fabernovel.d3library.action.OnPinchAction;
import com.fabernovel.d3library.action.OnScrollAction;
import com.fabernovel.d3library.action.PinchType;
import com.fabernovel.d3library.action.ScrollDirection;
import com.fabernovel.d3library.axes.D3FloatFunction;
import com.fabernovel.d3library.polygon.D3Polygon;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RectangleActionsActivity extends Activity {
    private static final int FPS = 40;
    private static final int TIME_PER_FRAME = 1000 / FPS;
    private static final float RECTANGLE_DIMENSION = 400F;
    @BindView(R.id.d3view) D3View view;
    @BindView(R.id.message) TextView message;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_message_activity);
        ButterKnife.bind(this);

        message.setText(getString(R.string.rectangle_actions_activity_message));

        final float[] coordinatesX = new float[4];
        setXCoordinates(coordinatesX, 0F, RECTANGLE_DIMENSION);
        final float[] coordinatesY = new float[4];
        setYCoordinates(coordinatesY, 0F, RECTANGLE_DIMENSION);


        final Float[] offsetX = new Float[]{0F};
        final Float[] offsetY = new Float[]{0F};

        final D3Polygon polygon = new D3Polygon()
            .x(coordinatesX)
            .y(coordinatesY)
            .offsetX(new D3FloatFunction() {
                @Override public float getFloat() {
                    return offsetX[0];
                }
            })
            .offsetY(new D3FloatFunction() {
                @Override public float getFloat() {
                    return offsetY[0];
                }
            })
            .lazyRecomputing(false);
        polygon
            .onScrollAction(new OnScrollAction() {
                @Override public void onScroll(
                    ScrollDirection direction,
                    float coordinateX,
                    float coordinateY,
                    float dX,
                    float dY
                ) {
                    offsetX[0] += dX;
                    offsetY[0] += dY;
                }
            })
            .onPinchAction(new OnPinchAction() {
                private float leftCoordinate = 0F;
                private float rightCoordinate = RECTANGLE_DIMENSION;
                private float topCoordinate = 0F;
                private float bottomCoordinate = RECTANGLE_DIMENSION;

                @Override public void onPinch(
                    PinchType pinchType, float coordinateStaticX, float coordinateStaticY,
                    float coordinateMobileX, float coordinateMobileY, float dX, float dY
                ) {
                    if (coordinateMobileX < coordinateStaticX) {
                        leftCoordinate += dX;
                    } else {
                        rightCoordinate += dX;
                    }
                    if (coordinateMobileY < coordinateStaticY) {
                        topCoordinate += dY;
                    } else {
                        bottomCoordinate += dY;
                    }
                    setXCoordinates(coordinatesX, leftCoordinate, rightCoordinate);
                    setYCoordinates(coordinatesY, topCoordinate, bottomCoordinate);
                }
            });

        view.add(polygon);
        view.setMinimumTimePerFrame(TIME_PER_FRAME);
    }

    private void setXCoordinates(
        float[] coordinatesX, float leftCoordinate, float rightCoordinate
    ) {
        coordinatesX[0] = leftCoordinate;
        coordinatesX[1] = rightCoordinate;
        coordinatesX[2] = rightCoordinate;
        coordinatesX[3] = leftCoordinate;
    }

    private void setYCoordinates(
        float[] coordinatesY, float topCoordinate, float bottomCoordinate
    ) {
        coordinatesY[0] = topCoordinate;
        coordinatesY[1] = topCoordinate;
        coordinatesY[2] = bottomCoordinate;
        coordinatesY[3] = bottomCoordinate;
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
