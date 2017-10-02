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

package com.applidium.pierreferrand.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applidium.pierreferrand.demo.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<ActivityViewHolder> {
    private final List<ActivityViewModel> activities;
    private final LayoutInflater inflater;

    public RecyclerAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        activities = new ArrayList<>();
    }

    public void setActivities(Collection<ActivityViewModel> collection) {
        activities.clear();
        activities.addAll(collection);
        notifyDataSetChanged();
    }

    public void addActivity(ActivityViewModel activityViewModel) {
        activities.add(activityViewModel);
        notifyDataSetChanged();
    }

    @Override public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_view, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override public void onBindViewHolder(ActivityViewHolder holder, int position) {
        holder.bindView(activities.get(position));
    }

    @Override public int getItemCount() {
        return activities.size();
    }
}
