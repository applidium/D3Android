package com.applidium.pierreferrand.demo.activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.animation.LinearInterpolator;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.arc.D3Arc;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.mappers.D3StringDataMapperFunction;
import com.applidium.pierreferrand.demo.R;

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
