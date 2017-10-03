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

import com.fabernovel.d3library.demo.R;
import com.fabernovel.d3library.D3View;
import com.fabernovel.d3library.arc.D3Arc;
import com.fabernovel.d3library.axes.D3FloatFunction;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomArcsActivity extends Activity {
    private static final int DATA_LENGTH = 5;

    @BindView(R.id.d3view) D3View view;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
        ButterKnife.bind(this);

        Float[] data = new Float[DATA_LENGTH];
        float[] weights = new float[DATA_LENGTH];
        for (int i = 0; i < DATA_LENGTH; i++) {
            data[i] = (float) i + 1;
            weights[i] = (float) i + 1;
        }

        final D3Arc<Float> arc = new D3Arc<>(data)
            .weights(weights)
            .offsetX(new D3FloatFunction() {
                @Override public float getFloat() {
                    return view.getHeight() <= view.getWidth() ?
                        (view.getWidth() - view.getHeight()) * 2 / 3F :
                        view.getWidth() / 6F;
                }
            })
            .offsetY(new D3FloatFunction() {
                @Override public float getFloat() {
                    return view.getWidth() <= view.getHeight() ?
                        (view.getHeight() - view.getWidth()) * 2 / 3F :
                        view.getHeight() / 6F;
                }
            })
            .innerRadius(new D3FloatFunction() {
                @Override public float getFloat() {
                    return Math.min(view.getWidth(), view.getHeight()) / 6F;
                }
            })
            .outerRadius(new D3FloatFunction() {
                @Override public float getFloat() {
                    return Math.min(view.getWidth(), view.getHeight()) * 2F / 6F;
                }
            })
            .padAngle(0)
            .colors(new int[]{
                0xFF0066CC, 0xFF7F00FF, 0xFFCD7F32, 0xFFC0C0C0, 0xFFFFD700
            })
            .labels(new String[]{"Label1", "Label2", "Label3", "Label4", "Label5"});
        view.add(arc);
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
