package com.applidium.pierreferrand.d3_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.polygon.D3Polygon;

public class MainActivity extends Activity {

    D3View view;
    D3Polygon polygon;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        view = (D3View) findViewById(R.id.test);

        polygon = new D3Polygon()
            .coordinate(
                new float[]{0.5f, 0.0f, 1f, 1f, 0f, 0.33f, 1f, 0.33f, 0f, 01f}
            )
            .proportional(true);
        view.add(polygon);
    }

    @Override protected void onPause() {
        super.onPause();
        view.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        view.onResume();
    }
}
