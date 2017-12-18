/* Copyright (c) 2011 Danish Maritime Authority.
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
package dk.dma.embryo.common.servlet;

import java.util.Arrays;
import java.util.Objects;

public class WeakETag implements ETag {
    private String value;

    WeakETag(String value) {
        this.value = "W/\"".concat(Objects.requireNonNull(value)).concat("\"");
    }

    public String getValue() {
        return value;
    }

    public boolean matches(String matchingValue) {
        return !Objects.isNull(matchingValue) && matches(this.value, matchingValue);
    }

    private boolean matches(String matchHeader, String toMatch) {
        String[] matchValues = matchHeader.split("\\s*,\\s*");
        Arrays.sort(matchValues);
        return Arrays.binarySearch(matchValues, toMatch) > -1
                || Arrays.binarySearch(matchValues, "*") > -1;
    }
}
