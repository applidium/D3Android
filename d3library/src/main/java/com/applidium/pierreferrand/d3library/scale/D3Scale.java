package com.applidium.pierreferrand.d3library.scale;

import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.axes.D3RangeFunction;
import com.applidium.pierreferrand.d3library.helper.ArrayConverterHelper;

public class D3Scale<T> {
    private final static int DEFAULT_TICK_NUMBER = 10;

    private D3RangeFunction<T> domain;
    private D3RangeFunction<Float> range;

    private Interpolator interpolator;
    private D3Converter<T> converter;

    public D3Scale() {
        this(null, null);
    }

    public D3Scale(T[] domain) {
        this(domain, null);
    }

    public D3Scale(T[] domain, Float[] range) {
        this(domain, range, new LinearInterpolator());
    }

    public D3Scale(T[] domain, Float[] range, Interpolator interpolator) {
        this.domain(domain);
        this.range(range);
        this.interpolator(interpolator);
    }

    public T[] domain() {
        return domain.getRange();
    }

    public D3Scale<T> domain(final T[] domain) {
        if (domain != null && domain.length < 2) {
            throw new IllegalStateException("Domain must have at least 2 elements");
        }
        this.domain = new D3RangeFunction<T>() {
            private T[] data = domain != null ? domain.clone() : null;

            @Override @Nullable public T[] getRange() {
                return data.clone();
            }
        };
        return this;
    }

    public D3Scale<T> domain(D3RangeFunction function) {
        domain = function;
        return this;
    }

    public Float[] range() {
        Float[] result = range.getRange();
        if (result != null) {
            return result;
        }
        return domainFloatValue();
    }

    private Float[] domainFloatValue() {
        T[] domain = this.domain.getRange();
        Float[] result = new Float[domain.length];
        for (int i = 0; i < domain.length; i++) {
            result[i] = converter.convert(domain[i]);
        }
        return result;
    }

    public D3Scale<T> range(@Nullable final Float[] range) {
        this.range = new D3RangeFunction<Float>() {
            private Float[] data = range != null ? range.clone() : null;

            @Override @Nullable public Float[] getRange() {
                return data;
            }
        };
        return this;
    }

    public D3Scale<T> range(D3RangeFunction function) {
        range = function;
        return this;
    }

    public D3Scale<T> interpolator(Interpolator interpolator) {
        if (interpolator == null) {
            throw new IllegalStateException("Interpolator must not be null");
        }
        this.interpolator = interpolator;
        return this;
    }

    public float value(T domainValue) {
        if (domain == null) {
            throw new IllegalStateException("Domain should not be null");
        }
        if (range == null) {
            return converter.convert(domainValue);
        }
        return interpolator.interpolate(
            converter.convert(domainValue),
            ArrayConverterHelper.convertArray(domainFloatValue()),
            ArrayConverterHelper.convertArray(range())
        );
    }

    public T invert(float rangeValue) {
        if (domain == null || range == null) {
            throw new IllegalStateException("Domain and range should not be null");
        }
        float interpolation = interpolator.interpolate(
            rangeValue,
            ArrayConverterHelper.convertArray(range()),
            ArrayConverterHelper.convertArray(domainFloatValue())
        );
        return converter.invert(interpolation);
    }

    public D3Scale<T> converter(D3Converter<T> converter) {
        this.converter = converter;
        return this;
    }

    public D3Converter<T> converter() {
        return converter;
    }

    public D3Scale<T> copy() {
        return new D3Scale<>(domain(), range(), interpolator);
    }

    public float[] ticks() {
        return ticks(DEFAULT_TICK_NUMBER);
    }

    public float[] ticks(int count) {
        if (domain == null) {
            throw new IllegalStateException("Domain should not be null");
        }
        float[] domain = ArrayConverterHelper.convertArray(domainFloatValue());
        float ticks[] = new float[count];
        if (count == 1) {
            ticks[0] = (domain[0] + domain[domain.length - 1]) / 2;
            return ticks;
        }

        for (int i = 1; i < count - 1; i++) {
            ticks[i] = i * domain[domain.length - 1] / (count - 1)
                + (count - 1 - i) * domain[0] / (count - 1);
        }
        ticks[0] = domain[0];
        ticks[count - 1] = domain[domain.length - 1];

        return ticks;
    }

    public String[] ticksLegend(int count) {
        if (domain == null) {
            throw new IllegalStateException("Domain should not be null");
        }
        float[] domain = ArrayConverterHelper.convertArray(domainFloatValue());
        String[] legends = new String[count];

        if (count == 1) {
            legends[0] = converter.invert((domain[0] + domain[domain.length - 1]) / 2).toString();
            return legends;
        }

        for (int i = 1; i < count - 1; i++) {
            legends[i] = converter
                .invert(i * domain[domain.length - 1] / (count - 1)
                            + (count - 1 - i) * domain[0] / (count - 1))
                .toString();
        }
        T[] domainValues = domain();
        legends[0] = domainValues[0].toString();
        legends[count - 1] = domainValues[domain.length - 1].toString();

        return legends;
    }
}
