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

package com.applidium.pierreferrand.d3library.scale;

public class LinearInterpolator implements Interpolator {
    @Override public float interpolate(
        float initialValue, float[] initialScope, float[] destinationScope
    ) {
        if (destinationScope[0] == destinationScope[1]) {
            return destinationScope[0];
        }
        float proportion = (initialValue - initialScope[0]) / (initialScope[1] - initialScope[0]);
        return (1 - proportion) * destinationScope[0] + proportion * destinationScope[1];
    }
}
