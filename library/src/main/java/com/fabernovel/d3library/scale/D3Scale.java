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

package com.fabernovel.d3library.scale;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fabernovel.d3library.axes.D3DomainFunction;
import com.fabernovel.d3library.axes.D3RangeFunction;

@SuppressWarnings("unused") public class D3Scale<T> {
    private static final int DEFAULT_TICK_NUMBER = 10;
    private static final String DOMAIN_ERROR = "Domain should not be null";
    private static final String CONVERTER_ERROR = "Converter should not be null";

    @Nullable private D3DomainFunction<T> domain;
    private float[] domainArray;
    @Nullable private D3RangeFunction range;

    @NonNull private Interpolator interpolator;
    @Nullable private D3Converter<T> converter;

    @NonNull private D3LabelFunction<T> labelFunction;

    public D3Scale() {
        this(null, null);
    }

    public D3Scale(@Nullable T[] domain) {
        this(domain, null);
    }

    public D3Scale(@Nullable T[] domain, @Nullable float[] range) {
        this(domain, range, new LinearInterpolator());
    }

    public D3Scale(
        @Nullable T[] domain, @Nullable float[] range, @NonNull Interpolator interpolator
    ) {
        if (domain != null) {
            this.domain(domain);
        }
        if (range != null) {
            this.range(range);
        }
        interpolator(interpolator);
        labelFunction = new D3LabelFunction<T>() {
            @Override public String getLabel(T object) {
                return object.toString();
            }
        };
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
        if (this.domain == null || !(this.domain instanceof WrapperDomainFunction)) {
            domain(new WrapperDomainFunction<>(domain));
        } else {
            ((WrapperDomainFunction<T>)this.domain).setData(domain);
            AdaptDomainArrayLength(this.domain);
        }
        return this;
    }

    private void AdaptDomainArrayLength(@Nullable D3DomainFunction<T> domain) {
        if (domainArray == null || domainArray.length != domain.getRange().length) {
            domainArray = new float[domain.getRange().length];
        }
    }

    /**
     * Sets the domain of the Scale.
     */
    public D3Scale<T> domain(@Nullable D3DomainFunction<T> domain) {
        this.domain = domain;
        AdaptDomainArrayLength(domain);
        return this;
    }

    /**
     * Returns the range of the Scale.
     */
    @Nullable public float[] range() {
        return range.getRange();
    }

    @Nullable private float[] domainFloatValue() {
        if (domain == null) {
            return domainArray;
        }
        if (converter == null) {
            throw new IllegalStateException(CONVERTER_ERROR);
        }
        T[] computedDomain = domain.getRange();
        for (int i = 0; i < computedDomain.length; i++) {
            domainArray[i] = converter.convert(computedDomain[i]);
        }
        return domainArray;
    }

    /**
     * Sets the range of the Scale.
     */
    public D3Scale<T> range(@NonNull final float[] range) {
        range(new D3RangeFunction() {
            private float[] data = range;

            @Override @Nullable public float[] getRange() {
                return data;
            }
        });
        return this;
    }

    /**
     * Sets the range of the Scale.
     */
    public D3Scale<T> range(@Nullable D3RangeFunction range) {
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
            domainFloatValue(),
            range()
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
            range(),
            domainFloatValue()
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
        return converter;
    }

    /**
     * Returns a copy of the Scale.
     */
    public D3Scale<T> copy() {
        return new D3Scale<>(domain(), range(), interpolator);
    }

    public D3LabelFunction<T> labelFunction() {
        return labelFunction;
    }

    public D3Scale<T> labelFunction(D3LabelFunction<T> labelFunction) {
        this.labelFunction = labelFunction;
        return this;
    }

    /**
     * See {@link #ticks(int, float[])}. Uses {@link #DEFAULT_TICK_NUMBER}.
     */
    public float[] ticks(float[] result) {
        return ticks(DEFAULT_TICK_NUMBER, result);
    }

    /**
     * Returns uniformly spaced float domain values.
     */
    public float[] ticks(int count, float[] result) {
        if (domain == null) {
            throw new IllegalStateException(DOMAIN_ERROR);
        }
        float[] computedDomain = domainFloatValue();
        if (count == 1) {
            result[0] = (computedDomain[0] + computedDomain[computedDomain.length - 1]) / 2;
            return result;
        }

        for (int i = 0; i < count; i++) {
            result[i] = i * computedDomain[computedDomain.length - 1] / (count - 1)
                + (count - 1 - i) * computedDomain[0] / (count - 1);
        }

        return result;
    }

    /**
     * Returns the labels of uniformly spaced float domain values.
     */
    public String[] ticksLegend(int count, String[] result) {
        if (domain == null) {
            throw new IllegalStateException(DOMAIN_ERROR);
        }
        if (converter == null) {
            throw new IllegalStateException(CONVERTER_ERROR);
        }
        float[] computedDomain = domainFloatValue();

        if (count == 1) {
            result[0] = labelFunction.getLabel(converter.invert(
                (computedDomain[0] + computedDomain[computedDomain.length - 1]) / 2
            ));
            return result;
        }

        for (int i = 1; i < count - 1; i++) {
            result[i] = labelFunction.getLabel(converter
                .invert(i * computedDomain[computedDomain.length - 1] / (count - 1)
                            + (count - 1 - i) * computedDomain[0] / (count - 1)));
        }
        T[] domainValues = domain();
        result[0] = labelFunction.getLabel(domainValues[0]);
        result[count - 1] = labelFunction.getLabel(domainValues[computedDomain.length - 1]);
        return result;
    }
}
