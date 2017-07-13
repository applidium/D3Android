package com.applidium.pierreferrand.d3library.axes;

import android.support.annotation.NonNull;

import com.applidium.pierreferrand.d3library.action.OnPinchAction;
import com.applidium.pierreferrand.d3library.action.OnScrollAction;
import com.applidium.pierreferrand.d3library.action.PinchType;
import com.applidium.pierreferrand.d3library.action.ScrollDirection;
import com.applidium.pierreferrand.d3library.scale.D3Converter;

final class D3AxisActionsInitializer<T> {
    private static final String CONVERTER_ERROR = "Converter should not be null";
    private static final String DOMAIN_ERROR = "Domain should not be null";
    private static final String RANGE_ERROR = "Range should not be null";
    private static final float PINCH_MIN_SPACING = 100F;

    private final D3Axis<T> axis;

    D3AxisActionsInitializer(D3Axis<T> axis) {
        this.axis = axis;
    }

    @NonNull OnScrollAction getHorizontalOnScrollAction() {
        return new OnScrollAction() {
            @Override public void onScroll(
                ScrollDirection direction, float coordinateX, float coordinateY, float dX, float dY
            ) {
                D3Converter<T> converter = axis.scale.converter();
                if (converter == null) {
                    throw new IllegalStateException(CONVERTER_ERROR);
                }
                T[] domain = axis.scale.domain();
                if (domain == null) {
                    throw new IllegalStateException(DOMAIN_ERROR);
                }
                converter.convert(domain[0]);
                Float[] range = axis.scale.range();
                if (range == null) {
                    throw new IllegalStateException(RANGE_ERROR);
                }
                float sign = dY < 0F ? 1F : -1F;
                float absDy = Math.abs(dY);
                float offset = converter.convert(domain[0])
                    - converter.convert(axis.scale.invert(range[0] + absDy));
                offset *= sign * 2;
                domain[0] = converter.invert(converter.convert(domain[0]) - offset);
                domain[1] = converter.invert(converter.convert(domain[1]) - offset);
                axis.scale.domain(domain);
                axis.updateNeeded();
            }
        };
    }

    @NonNull OnPinchAction getHorizontalOnPinchAction() {
        return new OnPinchAction() {
            @Override public void onPinch(
                PinchType pinchType, float coordinateStaticX, float coordinateStaticY,
                float coordinateMobileX, float coordinateMobileY, float dX, float dY
            ) {
                resizeOnPinch(coordinateStaticY, coordinateMobileY, dY);
                axis.updateNeeded();
            }
        };
    }

    private void resizeOnPinch(
        float coordinateStatic, float coordinateMobile, float diffCoordinate
    ) {
        if (Math.abs(coordinateMobile - coordinateStatic) < PINCH_MIN_SPACING) {
            return;
        }

        Float[] range = axis.range();

        float coordinateMin = range[0];
        float coordinateMax = range[1];

        int inverted = 0;
        if (coordinateMin > coordinateMax) {
            inverted = 1;
            float tmp = coordinateMin;
            coordinateMin = coordinateMax;
            coordinateMax = tmp;
        }

        if ((coordinateMobile < coordinateMin || coordinateMobile > coordinateMax)
            || (coordinateStatic < coordinateMin || coordinateStatic > coordinateMax)) {
            return;
        }

        D3Converter<T> converter = axis.scale.converter();
        if (converter == null) {
            throw new IllegalStateException(CONVERTER_ERROR);
        }
        float propMobile = (coordinateMobile + diffCoordinate - coordinateMin)
            / (coordinateMax - coordinateMin);
        float propStatic = (coordinateStatic - coordinateMin) / (coordinateMax - coordinateMin);
        float valueStatic = converter.convert(axis.scale.invert(coordinateStatic));
        float valueMobile = converter.convert(axis.scale.invert(coordinateMobile - diffCoordinate));

        float newDomainMin = (valueMobile * propStatic - valueStatic * propMobile)
            / (propStatic - propMobile);
        float newDomainMax = (newDomainMin * (propMobile - 1) + valueMobile) /
            propMobile;

        T[] domain = axis.domain();
        domain[inverted] = converter.invert(newDomainMin);
        domain[1 - inverted] = converter.invert(newDomainMax);
        axis.domain(domain);
    }

    @NonNull OnScrollAction getVerticalOnScrollAction() {
        return new OnScrollAction() {
            @Override public void onScroll(
                ScrollDirection direction, float coordinateX, float coordinateY, float dX, float dY
            ) {
                D3Converter<T> converter = axis.scale.converter();
                if (converter == null) {
                    throw new IllegalStateException(CONVERTER_ERROR);
                }
                T[] domain = axis.scale.domain();
                if (domain == null) {
                    throw new IllegalStateException(DOMAIN_ERROR);
                }
                Float[] range = axis.scale.range();
                if (range == null) {
                    throw new IllegalStateException(RANGE_ERROR);
                }
                float sign = dX < 0F ? 1F : -1F;
                float absoluteDx = Math.abs(dX);
                float offset = converter.convert(domain[0])
                    - converter.convert(axis.scale.invert(range[0] + absoluteDx));
                offset *= sign * 2;
                domain[0] = converter.invert(converter.convert(domain[0]) - offset);
                domain[1] = converter.invert(converter.convert(domain[1]) - offset);
                axis.scale.domain(domain);
                axis.updateNeeded();
            }
        };
    }

    @NonNull OnPinchAction getVerticalOnPinchAction() {
        return new OnPinchAction() {
            @Override public void onPinch(
                PinchType pinchType, float coordinateStaticX, float coordinateStaticY,
                float coordinateMobileX, float coordinateMobileY, float dX, float dY
            ) {
                resizeOnPinch(coordinateStaticX, coordinateMobileX, dX);
                axis.updateNeeded();
            }
        };
    }
}
