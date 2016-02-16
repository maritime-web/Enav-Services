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
package dk.dma.embryo.vessel.service;

/**
 * Created by andreas on 2/1/16.
 */

import dk.dma.embryo.common.configuration.Property;

import javax.inject.Inject;

public class AisDataServiceProperties {

    private double aisDataLimitLatitude;

    private String baseAreaFilter;

    private String defaultSources;

    private String defaultArea;

    @Inject
    public AisDataServiceProperties(@Property("embryo.aisDataLimit.latitude") double aisDataLimitLatitude,
                                    @Property("embryo.ais.filters.baseArea") String baseAreaFilter,
                                    @Property("embryo.ais.filters.defaultSources") String defaultSources,
                                    @Property("embryo.ais.filters.defaultArea") String defaultArea
    ){
        this.aisDataLimitLatitude = aisDataLimitLatitude;
        this.baseAreaFilter = baseAreaFilter;
        this.defaultSources = defaultSources;
        this.defaultArea = defaultArea;
    }


    public double getAisDataLimitLatitude() {
        return aisDataLimitLatitude;
    }

    public String getBaseAreaFilter() {
        return baseAreaFilter;
    }

    public String getDefaultSources() {
        return defaultSources;
    }

    public String getDefaultArea() {
        return defaultArea;
    }
}
