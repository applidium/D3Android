package com.applidium.pierreferrand.d3library.scale;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.axes.D3RangeFunction;
import com.applidium.pierreferrand.d3library.helper.ArrayConverterHelper;

public class D3Scale<T> {
    private static final int DEFAULT_TICK_NUMBER = 10;
    private static final String DOMAIN_ERROR = "Domain should not be null";
    private static final String CONVERTER_ERROR = "Converter should not be null";

    @Nullable private D3RangeFunction<T> domain;
    @Nullable private D3RangeFunction<Float> range;

    @NonNull private Interpolator interpolator;
    @Nullable private D3Converter<T> converter;

    public D3Scale() {
        this(null, null);
    }

    public D3Scale(@Nullable T[] domain) {
        this(domain, null);
    }

    public D3Scale(@Nullable T[] domain, @Nullable Float[] range) {
        this(domain, range, new LinearInterpolator());
    }

    public D3Scale(
        @Nullable T[] domain, @Nullable Float[] range, @NonNull Interpolator interpolator
    ) {
        if (domain != null) {
            this.domain(domain);
        }
        if (range != null) {
            this.range(range);
        }
        this.interpolator(interpolator);
    }

    /**
     * Returns the domain of the Scale.
     */
    @Nullable public T[] domain() {
        return domain != null ? domain.getRange() : null;
    }

    /**
     * Sets the domain of the Scale.
     */
    public D3Scale<T> domain(@NonNull final T[] domain) {
        this.domain = new D3RangeFunction<T>() {
            private T[] data = domain.clone();

            @Override public T[] getRange() {
                return data.clone();
            }
        };
        return this;
    }

    /**
     * Sets the domain of the Scale.
     */
    public D3Scale<T> domain(@Nullable D3RangeFunction<T> domain) {
        this.domain = domain;
        return this;
    }

    /**
     * Returns the range of the Scale.
     */
    @Nullable public Float[] range() {
        if (range == null) {
            return domainFloatValue();
        }
        Float[] result = range.getRange();
        if (result != null) {
            return result;
        }
        return domainFloatValue();
    }

    @Nullable private Float[] domainFloatValue() {
        if (domain == null) {
            return null;
        }
        if (converter == null) {
            throw new IllegalStateException(CONVERTER_ERROR);
        }
        T[] computedDomain = domain.getRange();
        Float[] result = new Float[computedDomain.length];
        for (int i = 0; i < computedDomain.length; i++) {
            result[i] = converter.convert(computedDomain[i]);
        }
        return result;
    }

    /**
     * Sets the range of the Scale.
     */
    public D3Scale<T> range(@NonNull final Float[] range) {
        this.range = new D3RangeFunction<Float>() {
            private Float[] data = range != null ? range.clone() : null;

            @Override @Nullable public Float[] getRange() {
                return data.clone();
            }
        };
        return this;
    }

    /**
     * Sets the range of the Scale.
     */
    public D3Scale<T> range(@Nullable D3RangeFunction<Float> range) {
        this.range = range;
        return this;
    }


    /**
     * Sets the interpolator.
     */
    public D3Scale<T> interpolator(@NonNull Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    /**
     * Returns the float value associated to the given domain value.
     */
    public float value(@NonNull T domainValue) {
        if (domain == null) {
            throw new IllegalStateException(DOMAIN_ERROR);
        }
        if (converter == null) {
            throw new IllegalStateException(CONVERTER_ERROR);
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

    /**
     * Returns the domain value associated to the range value.
     */
    @NonNull public T invert(float rangeValue) {
        if (domain == null || range == null) {
            throw new IllegalStateException("Domain and range should not be null");
        }
        if (converter == null) {
            throw new IllegalStateException(CONVERTER_ERROR);
        }
        float interpolation = interpolator.interpolate(
            rangeValue,
            ArrayConverterHelper.convertArray(range()),
            ArrayConverterHelper.convertArray(domainFloatValue())
        );
        return converter.invert(interpolation);
    }

    /**
     * Sets the converter.
     */
    public D3Scale<T> converter(@NonNull D3Converter<T> converter) {
        this.converter = converter;
        return this;
    }

    /**
     * Returns the converter.
     */
    @Nullable public D3Converter<T> converter() {
        ticks();
        return converter;
    }

    /**
     * Returns a copy of the Scale.
     */
    public D3Scale<T> copy() {
        return new D3Scale<>(domain(), range(), interpolator);
    }

    /**
     * See {@link #ticks(int)}. Uses {@link #DEFAULT_TICK_NUMBER}.
     */
    public float[] ticks() {
        return ticks(DEFAULT_TICK_NUMBER);
    }

    /**
     * Returns uniformly spaced float domain values.
     */
    public float[] ticks(int count) {
        if (domain == null) {
            throw new IllegalStateException(DOMAIN_ERROR);
        }
        float[] computedDomain = ArrayConverterHelper.convertArray(domainFloatValue());
        float[] ticks = new float[count];
        if (count == 1) {
            ticks[0] = (computedDomain[0] + computedDomain[computedDomain.length - 1]) / 2;
            return ticks;
        }

        for (int i = 0; i < count; i++) {
            ticks[i] = i * computedDomain[computedDomain.length - 1] / (count - 1)
                + (count - 1 - i) * computedDomain[0] / (count - 1);
        }

        return ticks;
    }

    /**
     * Returns the labels of uniformly spaced float domain values.
     */
    public String[] ticksLegend(int count) {
        if (domain == null) {
            throw new IllegalStateException(DOMAIN_ERROR);
        }
        if (converter == null) {
            throw new IllegalStateException(CONVERTER_ERROR);
        }
        float[] computedDomain = ArrayConverterHelper.convertArray(domainFloatValue());
        String[] legends = new String[count];

        if (count == 1) {
            legends[0] = converter.invert(
                (computedDomain[0] + computedDomain[computedDomain.length - 1]) / 2
            ).toString();
            return legends;
        }

        for (int i = 1; i < count - 1; i++) {
            legends[i] = converter
                .invert(i * computedDomain[computedDomain.length - 1] / (count - 1)
                            + (count - 1 - i) * computedDomain[0] / (count - 1))
                .toString();
        }
        T[] domainValues = domain();
        legends[0] = domainValues[0].toString();
        legends[count - 1] = domainValues[computedDomain.length - 1].toString();

        return legends;
    }
}
