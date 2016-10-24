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

import dk.dma.embryo.common.json.AbstractRestService;
import dk.dma.enav.services.registry.api.EnavServiceRegister;
import dk.dma.enav.services.registry.api.NoServicesFoundException;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.TechnicalDesignId;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 *
 */
@Path("/service")
public class ServiceLookupRestService extends AbstractRestService {
    @Inject
    private EnavServiceRegister enavServiceRegister;

    @Path("/lookup/{serviceTechnicalDesignId}/{version}")
    @Produces("application/json")
    @GET
    public List<InstanceMetadata> lookupInstances(@PathParam("serviceTechnicalDesignId") String serviceTechnicalDesignId,
                                                  @PathParam("version") String version,
                                                  @QueryParam("wkt") String location) {
        List<InstanceMetadata> res;

        try {
            res = enavServiceRegister.getServiceInstances(new TechnicalDesignId(serviceTechnicalDesignId, version), location);
        } catch (NoServicesFoundException e) {
            throw new WebApplicationException(Response.status(NOT_FOUND).entity(new String[] {"Unable to find any service implementation of \""+serviceTechnicalDesignId+"\" with a boundary defined by \"" + location + "\""}).build());
        }

        return res;
    }
}
