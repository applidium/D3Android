package com.applidium.pierreferrand.d3_android;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.applidium.pierreferrand.d3library.D3Drawable;
import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.action.ScrollDirection;
import com.applidium.pierreferrand.d3library.arc.D3Arc;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.helper.ArrayConverterHelper;

public class MainActivity extends Activity {
    D3View view;
    private Float innerRadius = 0F;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        view = (D3View) findViewById(R.id.test);
        Float[] test = new Float[40];
        for (int i = 0; i < test.length; i++) {
            test[i] = (float) i + 1;
        }
        final ValueAnimator animator = ValueAnimator.ofFloat(0F, 360F);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000);
        animator.start();

        final D3Arc<Float> arc = new D3Arc<>(test)
            .weights(ArrayConverterHelper.convertArray(test))
            .innerRadius(new D3FloatFunction() {
                @Override public float getFloat() {
                    return innerRadius;
                }
            })
            .offsetX(new D3FloatFunction() {
                @Override public float getFloat() {
                    return view.getHeight() <= view.getWidth() ?
                        (view.getWidth() - view.getHeight()) / 2f : 0f;
                }
            })
            .offsetY(new D3FloatFunction() {
                @Override public float getFloat() {
                    return view.getHeight() <= view.getWidth() ? 0f :
                        (view.getHeight() - view.getWidth()) / 2f;
                }
            })
            .padAngle(0);
        arc
            .onClickAction(
                new OnClickAction() {
                    boolean lazyRecomputing = false;

                    @Override public void onClick(float X, float Y) {
                        Log.v("DebugTest", "Click");
                        lazyRecomputing = !lazyRecomputing;
                        arc.lazyRecomputing(lazyRecomputing);
                    }
                }
            )
            .onScrollAction(new OnScrollAction() {
                float optimize = -1;

                @Override public void onScroll(
                    ScrollDirection direction,
                    float coordinateX,
                    float coordinateY,
                    float dX,
                    float dY
                ) {
                    if (dX * optimize < 0) {
                        if (dX < 0) {
                            arc.optimize(false);
                        } else {
                            arc.optimize(true);
                        }
                        optimize = dX;
                    }
                }
            })
            .startAngle(new D3FloatFunction() {
                @Override public float getFloat() {
                    return (float) animator.getAnimatedValue();
                }
            })
            .lazyRecomputing(false)
            .labels(true);
        view.afterDrawActions.add(new Runnable() {
            int sign = -1;

            @Override public void run() {
                innerRadius += sign;
                if (innerRadius > Math.min(view.getHeight(), view.getWidth()) / 4 ||
                    (innerRadius < Math.min(view.getHeight(), view.getWidth()) / 8)
                        && (sign == -1)) {
                    sign *= -1;
                }
            }
        });
        view.add(arc);
        view.add(
            new D3Drawable() {
                @Override public void draw(@NonNull Canvas canvas) {

                }
            }
                .lazyRecomputing(false)
        );
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
