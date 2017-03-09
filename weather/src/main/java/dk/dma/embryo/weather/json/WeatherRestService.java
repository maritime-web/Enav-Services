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
package dk.dma.embryo.weather.json;

import dk.dma.embryo.weather.model.RegionForecast;
import dk.dma.embryo.weather.model.Warnings;
import dk.dma.embryo.weather.model.Weather;
import dk.dma.embryo.weather.service.WeatherServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * 
 * @author Jesper Tejlgaard
 */
@Path("/weather")
@Slf4j
public class WeatherRestService {

    @Inject
    private WeatherServiceImpl weatherService;

    @GET
    @Path("/warning/{provider}/{region}")
    @Produces("application/json")
    @GZIP
    @NoCache
    public Warnings getWarning(String provider, String region) {
        log.debug("getWarning({})");

        Warnings warning = weatherService.getWarning();
        
        log.debug("getWarning({}, {}) : {}", provider, region, warning);
        return warning;
    }

    @GET
    @Path("/forecast/{provider}/{region}")
    @Produces("application/json")
    @GZIP
    @NoCache
    public RegionForecast getForecast(String provider, String region) {
        log.debug("getForecast({}, {})", provider, region);

        RegionForecast forecast = weatherService.getRegionForecast();

        log.debug("getForecast({}, {}) : {}", provider, region, forecast);
        return forecast;
    }

    @GET
    @Path("/{provider}/{region}")
    @Produces("application/json")
    @GZIP
    @NoCache
    public Weather getWeather(String provider, String region) {
        log.debug("getWeather({}, {})", provider, region);

        RegionForecast forecast = weatherService.getRegionForecast();
        Warnings warning = weatherService.getWarning();
        
        Weather weather = new Weather(forecast, warning);

        log.debug("getWeather({}, {}) : {}", provider, region, weather);
        return weather;
    }
}
