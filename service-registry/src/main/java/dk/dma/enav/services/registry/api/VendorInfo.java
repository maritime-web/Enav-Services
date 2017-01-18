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
public class VendorInfo {
    private String id;
    private String name;
    private String description;
    private String contactInfo;
    private Boolean commercial;

    public VendorInfo(String id) {
        this.id = id;
    }

    public VendorInfo withName(String name) {
        this.name = name;
        return this;
    }

    public VendorInfo withDescription(String description) {
        this.description = description;
        return this;
    }

    public VendorInfo withContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
        return this;
    }

    public VendorInfo withCommercial(Boolean commercial) {
        this.commercial = commercial;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public Boolean isCommercial() {
        return commercial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VendorInfo that = (VendorInfo) o;
        return commercial == that.commercial &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(contactInfo, that.contactInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, contactInfo, commercial);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("description", description)
                .add("contactInfo", contactInfo)
                .add("commercial", commercial)
                .toString();
    }
}
