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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by Steen on 23-05-2016.
 *
 */
public class Errors {
    private final String serviceRegistryId;
    private final List<ErrorDescription> descriptions;

    public Errors(String serviceRegistryId, List<ErrorDescription> descriptions) {
        this.serviceRegistryId = serviceRegistryId;
        this.descriptions = descriptions;
    }

    public Errors(String serviceRegistryId, ErrorDescription errorDescription) {
        this(serviceRegistryId, Collections.singletonList(errorDescription));
    }

    public String getServiceRegistryId() {
        return serviceRegistryId;
    }

    public List<ErrorDescription> getDescriptions() {
        return descriptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Errors errors = (Errors) o;
        return Objects.equals(serviceRegistryId, errors.serviceRegistryId) &&
                Objects.equals(descriptions, errors.descriptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceRegistryId, descriptions);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("serviceRegistryId", serviceRegistryId)
                .add("descriptions", descriptions)
                .toString();
    }
}
