package com.applidium.pierreferrand.d3_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.Line.D3DataMapperFunction;
import com.applidium.pierreferrand.d3library.Line.D3Line;
import com.applidium.pierreferrand.d3library.scale.D3Scale;

public class MainActivity extends Activity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        D3View view = (D3View) findViewById(R.id.test);


        final D3Line<Float> line = new D3Line<>(new Float[]{
            750f, 300f, 350f, 100f, 1000f, 300f, 300f, 500f
        });
        line.y(new D3DataMapperFunction<Float>() {
            @Override public float compute(Float object, int position, Float[] data) {
                D3Scale scale = new D3Scale()
                    .domain(new Float[]{0f, 1200f})
                    .range(new Float[]{line.height(), 0f});
                float test = scale.value(object);
                Log.v("DebugValue", "" + object + " -> " + test);
                return test;
            }
        }).x(new D3DataMapperFunction<Float>() {
            @Override public float compute(Float object, int position, Float[] data) {
                return position * line.width() / (line.data().length - 1);
            }
        });

        view.add(line);
    }
}
