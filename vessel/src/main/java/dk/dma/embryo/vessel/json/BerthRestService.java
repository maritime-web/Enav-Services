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
package dk.dma.embryo.vessel.json;

import dk.dma.embryo.vessel.model.Berth;
import dk.dma.embryo.vessel.service.GeographicService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @author Jesper Tejlgaard
 */
@Path("/berth")
@Slf4j
public class BerthRestService {

    @Inject
    private GeographicService geoService;

    @GET
    @Path("/search")
    @Produces("application/json")
    @GZIP
    @NoCache
    public List<BerthDatum> remote(@QueryParam("q") String query) {
        log.debug("remoteFetch({})", query);

        List<Berth> berths;
        List<BerthDatum> transformed = null;
        
        if(query != null && query.trim().length() > 0){
            berths = geoService.findBerths(query);
        }else{
            berths = geoService.findBerths("");
        }
        
        if(berths != null){
            transformed = berths.stream().map(new BerthTransformerFunction()).collect(Collectors.toList());
        }

        log.debug("berths={}", transformed);
        return transformed;
    }

    @Getter
    public static class BerthDatum extends TypeaheadDatum {
        private final Double latitude;
        private final Double longitude;

        public BerthDatum(String value, String[] tokens, Double latitude, Double longitude) {
            super(value, tokens);
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
    

    public static final class BerthTransformerFunction implements Function<Berth, BerthDatum> {
        private String value(final Berth input) {
            return input.getName() + (input.getAlias() != null ? " (" + input.getAlias() + ")" : "");
        }

        private String[] tokens(final Berth input) {
            if (input.getAlias() != null) {
                return new String[] { input.getName(), input.getAlias() };
            }
            return new String[] { input.getName() };
        }

        @Override
        public BerthDatum apply(final Berth input) {
            return new BerthDatum(value(input), tokens(input), input.getPosition().getLatitude(), input
                    .getPosition().getLongitude());
        }
    }
}
