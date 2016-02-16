package dk.dma.embryo.vessel.service;

/**
 * Created by andreas on 2/1/16.
 */

import dk.dma.embryo.common.configuration.Property;

import javax.inject.Inject;


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
