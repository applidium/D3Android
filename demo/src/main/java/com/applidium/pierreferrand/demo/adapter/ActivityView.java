package com.applidium.pierreferrand.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.applidium.pierreferrand.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityView extends FrameLayout {
    @BindView(R.id.title) TextView title;

    public ActivityView(Context context) {
        super(context);
        init();
    }

    public ActivityView(
        Context context,
        @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_activity, this);
        ButterKnife.bind(this);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setActivity(final Class<?> activityClass) {
        setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(getContext(), activityClass);
                getContext().startActivity(intent);
            }
        });
    }
}
