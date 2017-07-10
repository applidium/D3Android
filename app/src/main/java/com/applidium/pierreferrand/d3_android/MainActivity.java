package com.applidium.pierreferrand.d3_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.arc.D3Arc;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;

public class MainActivity extends Activity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final D3View view = (D3View) findViewById(R.id.test);

        view.add(
            new D3Arc<>(new Float[]{1f, 2f, 3f, 4f})
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
        );
    }
}
