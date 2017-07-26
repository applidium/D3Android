package com.applidium.pierreferrand.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

class ActivityViewHolder extends RecyclerView.ViewHolder {
    ActivityViewHolder(View itemView) {
        super(itemView);
    }

    void bindView(ActivityViewModel activityViewModel) {
        ((ActivityView) itemView).setTitle(activityViewModel.title());
        ((ActivityView) itemView).setActivity(activityViewModel.activityClass());
    }
}
