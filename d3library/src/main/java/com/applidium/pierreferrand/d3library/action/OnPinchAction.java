package com.applidium.pierreferrand.d3library.action;

public interface OnPinchAction {
    void onPinch(
        PinchType pinchType,
        float coordinateStaticX,
        float coordinateStaticY,
        float coordinateMobileX,
        float coordinateMobileY,
        float dX,
        float dY
    );
}
