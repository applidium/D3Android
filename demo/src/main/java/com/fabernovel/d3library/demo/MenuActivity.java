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

package com.fabernovel.d3library.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fabernovel.d3library.demo.activities.BarChartActivity;
import com.fabernovel.d3library.demo.activities.BoxPlotActivity;
import com.fabernovel.d3library.demo.activities.CustomArcsActivity;
import com.fabernovel.d3library.demo.activities.DrawPolygonActivity;
import com.fabernovel.d3library.demo.activities.GrowingLineChartActivity;
import com.fabernovel.d3library.demo.activities.MultipleAreasActivity;
import com.fabernovel.d3library.demo.activities.MultipleBarChartsActivity;
import com.fabernovel.d3library.demo.activities.ObjectArcsActivity;
import com.fabernovel.d3library.demo.activities.RectangleActionsActivity;
import com.fabernovel.d3library.demo.activities.TurningArcsActivity;
import com.fabernovel.d3library.demo.adapter.ActivityViewModelBuilder;
import com.fabernovel.d3library.demo.adapter.RecyclerAdapter;

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
                .activityClass(BarChartActivity.class)
                .build()
        );

        adapter.addActivity(
            new ActivityViewModelBuilder()
                .title("Sales stacking bar charts")
                .activityClass(MultipleBarChartsActivity.class)
                .build()
        );

        adapter.addActivity(
            new ActivityViewModelBuilder()
                .title("Growing line chart")
                .activityClass(GrowingLineChartActivity.class)
                .build()
        );

        adapter.addActivity(
            new ActivityViewModelBuilder()
                .title("Multiple areas")
                .activityClass(MultipleAreasActivity.class)
                .build()
        );

        adapter.addActivity(
            new ActivityViewModelBuilder()
                .title("Draw polygon")
                .activityClass(DrawPolygonActivity.class)
                .build()
        );

        adapter.addActivity(
            new ActivityViewModelBuilder()
                .title("Box plot")
                .activityClass(BoxPlotActivity.class)
                .build()
        );
    }
}
