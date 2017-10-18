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

package com.fabernovel.d3library.axes;

import com.fabernovel.d3library.scale.D3Converter;

final class AxisDefaultInitializer {
    private AxisDefaultInitializer() {
    }

    static <T> D3Converter<T> getDefaultConverter(Class<T> tClass) {
        if (tClass.equals(Float.class)) {
            return new D3Converter<T>() {
                @Override public float convert(T toConvert) {
                    return (Float) toConvert;
                }

                @Override public T invert(float toInvert) {
                    return (T) new Float(toInvert);
                }
            };
        } else if (tClass.equals(Integer.class)) {
            return new D3Converter<T>() {
                @Override public float convert(T toConvert) {
                    return (Integer) toConvert;
                }

                @Override public T invert(float toInvert) {
                    return (T) Integer.valueOf((int) toInvert);
                }
            };
        }
        return null;
    }
}
