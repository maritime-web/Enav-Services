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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Steen on 27-04-2016.
 *
 */
public class InstanceMetadata {
    private String instanceId;
    private String name;
    private TechnicalDesignId technicalDesignId;
    private String boundary;
    private String url;
    private Errors errors;
    private List<Errors> warnings;

    public InstanceMetadata() {
        this(null, null, null, null, null);
    }

    public InstanceMetadata(String instanceId, String name, TechnicalDesignId technicalDesignId, String boundary, String url) {
        this.instanceId = instanceId;
        this.name = name;
        this.technicalDesignId = technicalDesignId;
        this.boundary = boundary;
        this.url = url;
        this.warnings = new ArrayList<>();
    }

    public InstanceMetadata(String id, Errors errorDescriptions) {
        this(id, null, null, null, null);
        setErrors(errorDescriptions);
    }

    public boolean intersects(String wktFilter) {
        WKTReader reader = new WKTReader();
        try {
            Geometry serviceBoundary = reader.read(getBoundary());
            Geometry filter = reader.read(wktFilter);

            return serviceBoundary.intersects(filter);
        } catch (Exception e) {
//            throw new RuntimeException("Can't parse WKT", e);
            return false;
        }

    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getName() {
        return name;
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

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTechnicalDesignId(TechnicalDesignId technicalDesignId) {
        this.technicalDesignId = technicalDesignId;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public List<Errors> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<Errors> warnings) {
        this.warnings = warnings != null ? warnings : this.warnings;
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
