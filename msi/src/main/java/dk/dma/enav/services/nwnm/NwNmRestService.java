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
package dk.dma.enav.services.nwnm;

import org.jboss.resteasy.annotations.GZIP;
import org.niord.model.message.MessageVo;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import java.util.List;

/**
 * REST endpoint for searching NW-NM services as defined in the Maritime Cloud Service Registry.
 * <p>
 * The endpoint is called with a list of service instance ID's, which are then queried in parallel.
 */
@Path("/nw-nm")
public class NwNmRestService {

    @Inject
    NwNmService nwNmService;

    /**
     * Fetches the published messages from the given service instances in parallel
     * @param instanceIds the MC Service Registry instance IDs to fetch messages from
     * @param lang the ISO-2 language code, e.g. "en"
     * @param wkt optionally a WKT for the geometry extent of the area
     */
    @GET
    @Path("/messages")
    @Produces("application/json;charset=UTF-8")
    @GZIP
    public List<MessageVo> getNwNmMessages(
            @QueryParam("instanceId") List<String> instanceIds,
            @QueryParam("mainType") String mainType,
            @QueryParam("lang") @DefaultValue("en") String lang,
            @QueryParam("wkt") String wkt) throws Exception {

        try {
            return nwNmService.getNwNmMessages(instanceIds, mainType, lang, wkt);
        } catch (Exception e) {
            throw new WebApplicationException("Failed loading NW-NM messages: " + e.getMessage(), 500);
        }
    }

}
