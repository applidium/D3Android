package com.applidium.pierreferrand.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.applidium.pierreferrand.demo.activities.CustomArcsActivity;
import com.applidium.pierreferrand.demo.activities.MultipleBarChartsActivity;
import com.applidium.pierreferrand.demo.activities.ObjectArcsActivity;
import com.applidium.pierreferrand.demo.activities.RectangleActionsActivity;
import com.applidium.pierreferrand.demo.activities.TurningArcsActivity;
import com.applidium.pierreferrand.demo.adapter.ActivityViewModelBuilder;
import com.applidium.pierreferrand.demo.adapter.RecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuActivity extends Activity {
    @BindView(R.id.recycler) RecyclerView recyclerView;

    private RecyclerAdapter adapter;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        adapter = new RecyclerAdapter(getLayoutInflater());
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false
        );
        recyclerView.setLayoutManager(manager);

        adapter.addActivity(
            new ActivityViewModelBuilder()
                .title("Custom arcs")
                .activityClass(CustomArcsActivity.class)
                .build()
        );

        adapter.addActivity(
            new ActivityViewModelBuilder()
                .title("Turning arcs")
                .activityClass(TurningArcsActivity.class)
                .build()
        );

        adapter.addActivity(
            new ActivityViewModelBuilder()
                .title("Object arcs")
                .activityClass(ObjectArcsActivity.class)
                .build()
        );

        adapter.addActivity(
            new ActivityViewModelBuilder()
                .title("Rectangle actions")
                .activityClass(RectangleActionsActivity.class)
                .build()
        );

        adapter.addActivity(
            new ActivityViewModelBuilder()
                .title("Sales bar chart")
                .activityClass(MultipleBarChartsActivity.class)
                .build()
        );
    }
}
