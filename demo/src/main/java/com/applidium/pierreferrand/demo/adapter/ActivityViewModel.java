package com.applidium.pierreferrand.demo.adapter;

import io.norberg.automatter.AutoMatter;

@AutoMatter
public interface ActivityViewModel {
    String title();
    Class<?> activityClass();
}
