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
 *
 */
public class Error {
    private final ErrorId id;
    private final ErrorType errorType;
    private final String description;

    public Error(ErrorId id, ErrorType errorType, String errorDescription) {
        this.id = id;
        this.errorType = errorType;
        description = errorDescription;
    }

    public Error(IllegalArgumentException e) {
        this(ErrorId.INVALID_DATA, ErrorType.INVALID_DATA, e.getMessage());
    }

    public ErrorId getId() {
        return id;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Error error = (Error) o;
        return id == error.id &&
                errorType == error.errorType &&
                Objects.equals(description, error.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, errorType, description);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("errorType", errorType)
                .add("description", description)
                .toString();
    }
}
