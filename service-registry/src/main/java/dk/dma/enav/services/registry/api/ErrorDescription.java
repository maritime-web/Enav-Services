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
package dk.dma.enav.services.registry.api;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * Created by Steen on 23-05-2016.
 *
 */
public class ErrorDescription {
    private final String type;
    private final String message;
    private final String details;

    public ErrorDescription(String type, String message, String details) {
        this.type = type;
        this.message = message;
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorDescription that = (ErrorDescription) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(message, that.message) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, message, details);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("message", message)
                .add("details", details)
                .toString();
    }
}
