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
package dk.dma.enav.services.registry;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Objects;

/**
 * Created by Steen on 27-04-2016.
 *
 */
public class ServiceInstanceMetadata {
    private String serviceId;
    private String instanceId;
    private String name;
    private String boundary;
    private String url;


    public ServiceInstanceMetadata(String serviceId, String instanceId, String name, String boundary, String url) {
        this.serviceId = serviceId;
        this.instanceId = instanceId;
        this.name = name;
        this.boundary = boundary;
        this.url = url;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getName() {
        return name;
    }

    public String getBoundary() {
        return boundary;
    }

    public String getUrl() {
        return url;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceInstanceMetadata that = (ServiceInstanceMetadata) o;
        return Objects.equals(instanceId, that.instanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("serviceId", serviceId)
                .append("instanceId", instanceId)
                .append("name", name)
                .append("boundary", boundary)
                .append("url", url)
                .toString();
    }
}
