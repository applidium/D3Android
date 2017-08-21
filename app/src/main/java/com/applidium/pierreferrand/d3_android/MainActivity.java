package com.applidium.pierreferrand.d3_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.action.Action;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.arc.D3Arc;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;

public class MainActivity extends Activity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final D3View view = (D3View) findViewById(R.id.test);

        final D3Arc<Float> arc = new D3Arc<>(new Float[]{1f, 2f, 3f, 4f})
            .weights(new float[]{1f, 2f, 3f, 4f})
            .innerRadius(new D3FloatFunction() {
                @Override public float getFloat() {
                    return Math.min(view.getHeight(), view.getWidth()) * 3f / 8f;
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
            .labels(true)
            .padAngle(50F);
        arc.onClickAction(new OnClickAction() {
            @Override public void onClick(float X, float Y) {
                Log.v("Debug", "" + arc.dataFromPosition(X, Y));
            }
        });
        view.afterDrawActions.add(new Action() {
            @Override public void execute() {
                arc.startAngle(arc.startAngle() + 0.5F);
            }
        });
        view.add(arc);
    }
}
