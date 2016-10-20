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
package dk.dma.enav.services.registry.mc;

/**
 * Created by Steen on 13-10-2016.
 *
 */
class InstanceDetails {
    static final String DEFAULT_COVERAGE = "POLYGON((-180 -90, 180 -90, 180 90, -180 90, -180 -90))";
    private final String url;
    private String coverage;
    private String designId;
    private String designVersion;

    InstanceDetails(String url) {
        this.url = url;
    }

    InstanceDetails withCoverage(String coverage) {
        this.coverage = coverage;
        return this;
    }

    InstanceDetails withDesignId(String designId) {
        this.designId = designId;
        return this;
    }

    InstanceDetails withDesignVersion(String designVersion) {
        this.designVersion = designVersion;
        return this;
    }

    public String getUrl() {
        return url;
    }

    String getCoverage() {
        return coverage != null ? coverage : DEFAULT_COVERAGE;
    }

    String getDesignId() {
        return designId;
    }

    String getDesignVersion() {
        return designVersion;
    }
}