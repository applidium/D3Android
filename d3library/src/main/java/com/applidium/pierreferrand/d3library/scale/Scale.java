package com.applidium.pierreferrand.d3library.scale;

public class Scale {
    private final static int DEFAULT_TICK_NUMBER = 10;

    private float[] domain;
    private float[] range;

    private Interpolator interpolator;

    public Scale() {
        this(null, null);
    }

    public Scale(float[] domain) {
        this(domain, null);
    }

    public Scale(float[] domain, float[] range) {
        this(domain, range, new LinearInterpolator());
    }

    public Scale(float[] domain, float[] range, Interpolator interpolator) {
        verifyParametersValidity(domain, range);
        this.domain(domain);
        this.range(range);
        this.interpolator(interpolator);
    }

    private void verifyParametersValidity(float[] domain, float[] range) {
        if ((domain != null && range != null) && (domain.length != range.length)) {
            throw new IllegalStateException("Domain and range must be the same size");
        }
    }

    public Scale domain(float[] domain) {
        verifyParametersValidity(domain, range);
        if (domain != null && domain.length < 2) {
            throw new IllegalStateException("Domain must have at least 2 elements");
        }
        if (domain == null) {
            this.domain = null;
        } else {
            this.domain = domain.clone();
        }
        return this;
    }

    public Scale range(float[] range) {
        verifyParametersValidity(domain, range);
        if (range != null && range.length < 2) {
            throw new IllegalStateException("Range must have at least 2 elements");
        }
        if (range == null) {
            this.range = null;
        } else {
            this.range = range.clone();
        }
        return this;
    }

    public Scale interpolator(Interpolator interpolator) {
        if (interpolator == null) {
            throw new IllegalStateException("Interpolator must not be null");
        }
        this.interpolator = interpolator;
        return this;
    }

    public float value(float domainValue) {
        if (domain == null) {
            throw new IllegalStateException("Domain should not be null");
        }
        if (range == null) {
            return domainValue;
        }
        return interpolator.interpolate(domainValue, domain, range);
    }

    public float invert(float rangeValue) {
        if (domain == null || range == null) {
            throw new IllegalStateException("Domain and range should not be null");
        }
        return interpolator.interpolate(rangeValue, range, domain);
    }

    public Scale copy() {
        return new Scale(domain, range, interpolator);
    }

    public float[] ticks() {
        return ticks(DEFAULT_TICK_NUMBER);
    }

    public float[] ticks(int count) {
        if (domain == null) {
            throw new IllegalStateException("Domain should not be null");
        }
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
}
