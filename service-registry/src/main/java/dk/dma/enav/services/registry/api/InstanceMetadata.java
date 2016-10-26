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
import com.google.common.base.Strings;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 */
public class InstanceMetadata {
    private String instanceId;
    private String version;
    private String name;
    private String description;
    private String status;
    private Float availability;
    private TechnicalDesignId technicalDesignId;
    private String boundary;
    private String url;
    private VendorInfo producedBy;
    private VendorInfo providedBy;
    private Set<Error> errors;
    private List<Error> warnings;

    public InstanceMetadata(String instanceId, String version) {
        if (instanceId == null) {
            throw new IllegalArgumentException("Can not create InstanceMetadata with missing instanceId");
        }
        if (version == null) {
            throw new IllegalArgumentException("Can not create InstanceMetadata with missing version");
        }
        this.instanceId = instanceId;
        this.version = version;
        this.warnings = new ArrayList<>();
        this.errors = new HashSet<>();
    }

    public boolean intersects(String wktFilter) {
        WKTReader reader = new WKTReader();
        try {
            Geometry serviceBoundary = reader.read(getBoundary());
            Geometry filter = reader.read(wktFilter);

            return serviceBoundary.intersects(filter);
        } catch (Exception e) {
            return false;
        }
    }

    public List<Error> validate() {
        ArrayList<Error> res = new ArrayList<>();
        res.addAll(errors);
        if (Strings.isNullOrEmpty(name)) {
            res.add(createErrorforMissingAttribute(ErrorId.MISSING_INSTANCE_NAME, "name"));
        }
        if (technicalDesignId == null) {
            res.add(createErrorforMissingAttribute(ErrorId.MISSING_DESIGN_ID, "technicalDesignId"));
        }
        if (Strings.isNullOrEmpty(boundary)) {
            res.add(createErrorforMissingAttribute(ErrorId.MISSING_BOUNDARY, "boundary"));
        }
        if (Strings.isNullOrEmpty(url)) {
            res.add(createErrorforMissingAttribute(ErrorId.MISSING_URL, "url"));
        }
        if (Strings.isNullOrEmpty(description)) {
            res.add(createErrorforMissingAttribute(ErrorId.MISSING_DESCRIPTION, "description"));
        }
        if (Strings.isNullOrEmpty(status)) {
            res.add(createErrorforMissingAttribute(ErrorId.MISSING_STATUS, "status"));
        }
        if (availability == null) {
            res.add(createErrorforMissingAttribute(ErrorId.MISSING_AVAILABILITY, "availability"));
        }
        if (producedBy == null) {
            res.add(createErrorforMissingAttribute(ErrorId.MISSING_PRODUCED_BY, "producedBy"));
        }
        if (providedBy == null) {
            res.add(createErrorforMissingAttribute(ErrorId.MISSING_PROVIDED_BY, "providedBy"));
        }
        return res;
    }

    private Error createErrorforMissingAttribute(ErrorId id,  String attributeName) {
        return new Error(id, ErrorType.MISSING_DATA , "Missing required attribute '"+attributeName+"'");
    }

    public InstanceMetadata withInstanceName(String name) {
        this.name = name;
        return this;
    }

    public InstanceMetadata withDescription(String description) {
        this.description = description;
        return this;
    }

    public InstanceMetadata withAvailability(Float availability) {
        this.availability = availability;
        return this;
    }

    public InstanceMetadata withStatus(String status) {
        this.status = status;
        return this;
    }

    public InstanceMetadata withTechnicalDesignId(TechnicalDesignId technicalDesignId) {
        this.technicalDesignId = technicalDesignId;
        return this;
    }

    /**
     * Sets the geographical area from which the service instance is accessible.
     * @param boundary as WKT
     * @return this
     * @throws IllegalArgumentException if boundary is not valid WKT
     */
    public InstanceMetadata withBoundary(String boundary) {
        WKTReader reader = new WKTReader();
        try {
            reader.read(boundary);
        } catch (Exception e) {
            throw new IllegalArgumentException("Boundary could not be parsed as WKT '"+boundary+"'", e);
        }
        this.boundary = boundary;
        return this;
    }

    public InstanceMetadata withUrl(String url) {
        this.url = url;
        return this;
    }

    public InstanceMetadata withProducedBy(VendorInfo producedBy) {
        this.producedBy = producedBy;
        return this;
    }

    public InstanceMetadata withProvidedBy(VendorInfo providedBy) {
        this.providedBy = providedBy;
        return this;
    }

    public InstanceMetadata addWarning(Error warning) {
        if (warning == null) {
            throw new IllegalArgumentException("Warning must not be null");
        }
        this.warnings.add(warning);
        return this;
    }

    public InstanceMetadata addError(Error error) {
        if (error == null) {
            throw new IllegalArgumentException("Error must not be null");
        }
        this.errors.add(error);
        return this;
    }

    public InstanceMetadata addAllErrors(List<Error> errors) {
        if (errors == null) {
            throw new IllegalArgumentException("Error list must not be null");
        }

        errors.forEach(this::addError);
        return this;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public Float getAvailability() {
        return availability;
    }

    public TechnicalDesignId getTechnicalDesignId() {
        return technicalDesignId;
    }

    public String getBoundary() {
        return boundary;
    }

    public String getUrl() {
        return url;
    }

    public VendorInfo getProducedBy() {
        return producedBy;
    }

    public VendorInfo getProvidedBy() {
        return providedBy;
    }

    public List<Error> getErrors() {
        return new ArrayList<>(errors);
    }

    public List<Error> getWarnings() {
        return new ArrayList<>(warnings);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InstanceMetadata that = (InstanceMetadata) o;
        return Objects.equals(instanceId, that.instanceId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(technicalDesignId, that.technicalDesignId) &&
                Objects.equals(boundary, that.boundary) &&
                Objects.equals(url, that.url) &&
                Objects.equals(errors, that.errors) &&
                Objects.equals(warnings, that.warnings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId, name, technicalDesignId, boundary, url, errors, warnings);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("instanceId", instanceId)
                .add("name", name)
                .add("technicalDesignId", technicalDesignId)
                .add("boundary", boundary)
                .add("url", url)
                .add("errors", errors)
                .add("warnings", warnings)
                .toString();
    }
}
