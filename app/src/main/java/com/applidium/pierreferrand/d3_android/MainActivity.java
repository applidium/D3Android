package com.applidium.pierreferrand.d3_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.axes.AxisOrientation;
import com.applidium.pierreferrand.d3library.axes.D3Axis;
import com.applidium.pierreferrand.d3library.axes.HorizontalAlignment;
import com.applidium.pierreferrand.d3library.axes.VerticalAlignment;
import com.applidium.pierreferrand.d3library.scale.D3Scale;

public class MainActivity extends Activity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        D3View view = (D3View) findViewById(R.id.test);

        D3Scale horizontalScale = new D3Scale()
            .domain(new float[]{0, 100})
            .range(new float[]{0, 500});

        D3Scale verticalScale = new D3Scale()
            .domain(new float[]{150, 3000})
            .range(new float[]{700, 0});

        view.add(
            new D3Axis(AxisOrientation.TOP, horizontalScale)
                .translate(25, 1200)
                .ticks(5)
                .legendHorizontalAlignment(HorizontalAlignment.CENTER)
                .textSizeInPixels(40)
                .axisColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .legendColor(ContextCompat.getColor(this, R.color.colorAccent))
        );
        view.add(
            new D3Axis(AxisOrientation.RIGHT, verticalScale)
                .ticks(3)
                .translate(100, 100)
                .legendVerticalAlignment(VerticalAlignment.CENTER)
                .textSizeInPixels(30)
        );
    }

}
