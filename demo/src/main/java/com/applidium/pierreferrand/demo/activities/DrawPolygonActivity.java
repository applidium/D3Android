package com.applidium.pierreferrand.demo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.action.OnClickAction;
import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.action.PinchType;
import com.applidium.pierreferrand.d3library.action.ScrollDirection;
import com.applidium.pierreferrand.d3library.polygon.D3Polygon;
import com.applidium.pierreferrand.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DrawPolygonActivity extends Activity {
    @BindView(R.id.d3view) D3View view;
    @BindView(R.id.message) TextView message;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_message_activity);
        ButterKnife.bind(this);
        message.setText(getString(R.string.drawing_polygon_activity_message));

        final D3Polygon polygon = new D3Polygon(new float[0])
            .lazyRecomputing(false);
        polygon
            .onClickAction(new OnClickAction() {
                @Override public void onClick(float X, float Y) {
                    AddPoint(X, Y, polygon);
                }
            })
            .onScrollAction(new OnScrollAction() {
                private static final int FREQUENCY_TAKEN_POINT = 5;
                int count = 0;

                @Override public void onScroll(
                    ScrollDirection direction, float coordinateX, float coordinateY,
                    float dX, float dY
                ) {
                    if (count == 0) {
                        AddPoint(coordinateX, coordinateY, polygon);
                    }
                    count = (count + 1) % FREQUENCY_TAKEN_POINT;
                }
            })
            .onPinchAction(new OnPinchAction() {
                @Override public void onPinch(
                    PinchType pinchType, float coordinateStaticX, float coordinateStaticY,
                    float coordinateMobileX, float coordinateMobileY, float dX, float dY
                ) {
                    polygon.coordinates(new float[0]);
                }
            });

        view.add(polygon);
    }

    private void AddPoint(float x, float y, @NonNull D3Polygon polygon) {
        float[] coordinates = polygon.coordinates();
        float[] newCoordinates = new float[coordinates.length + 2];
        for (int i = 0; i < coordinates.length; i++) {
            newCoordinates[i] = coordinates[i];
        }
        newCoordinates[coordinates.length] = x;
        newCoordinates[coordinates.length + 1] = y;
        polygon.coordinates(newCoordinates);
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
