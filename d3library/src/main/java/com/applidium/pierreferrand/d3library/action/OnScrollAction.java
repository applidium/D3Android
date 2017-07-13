package com.applidium.pierreferrand.d3library.action;

public interface OnScrollAction {
    void onScroll(
        ScrollDirection direction,
        float coordinateX,
        float coordinateY,
        float dX,
        float dY
    );
}
