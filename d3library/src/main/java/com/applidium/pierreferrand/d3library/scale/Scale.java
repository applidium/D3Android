package com.applidium.pierreferrand.d3library.scale;

public class Scale {
    private float[] domain;
    private float[] range;

    public Scale() {
        this(null, null);
    }

    public Scale(float[] domain) {
        this(domain, null);
    }

    public Scale(float[] domain, float[] range) {
        verifyParametersValidity(domain, range);
        this.domain(domain);
        this.range(range);
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
}
