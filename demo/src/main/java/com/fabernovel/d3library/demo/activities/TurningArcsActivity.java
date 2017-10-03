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

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.animation.LinearInterpolator;

import com.fabernovel.d3library.demo.R;
import com.fabernovel.d3library.D3View;
import com.fabernovel.d3library.arc.D3Arc;
import com.fabernovel.d3library.axes.D3FloatFunction;
import com.fabernovel.d3library.mappers.D3StringDataMapperFunction;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TurningArcsActivity extends Activity {
    private static final int ANIMATION_DURATION = 10000;
    private static final int DATA_LENGTH = 40;

    @BindView(R.id.d3view) D3View view;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
        ButterKnife.bind(this);

        final ValueAnimator animator = buildAnimator();

        Float[] data = new Float[DATA_LENGTH];
        float[] weights = new float[DATA_LENGTH];
        for (int i = 0; i < DATA_LENGTH; i++) {
            data[i] = (float) i + 1;
            weights[i] = (float) i + 1;
        }

        D3Arc<Float> arc =
            new D3Arc<>(data)
                .weights(weights)
                .startAngle(new D3FloatFunction() {
                    @Override public float getFloat() {
                        return (float) animator.getAnimatedValue();
                    }
                })
                .labels(new D3StringDataMapperFunction<Float>() {
                    String noLabel = "";

                    @Override public String compute(Float object, int position, Float[] data) {
                        /* Using a variable rather than returning "" avoids to reallocate a new
                         * String each time the method is called. */
                        return noLabel;
                    }
                })
                .lazyRecomputing(false);

        view.add(arc);
    }

    @NonNull private ValueAnimator buildAnimator() {
        ValueAnimator startAngleAnimator = ValueAnimator.ofFloat(0F, 360F);
        startAngleAnimator.setRepeatMode(ValueAnimator.RESTART);
        startAngleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        startAngleAnimator.setInterpolator(new LinearInterpolator());
        startAngleAnimator.setDuration(ANIMATION_DURATION);
        startAngleAnimator.start();
        return startAngleAnimator;
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
