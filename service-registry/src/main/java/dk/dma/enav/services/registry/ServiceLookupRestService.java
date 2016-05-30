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
 * Created by Steen on 20-04-2016.
 *
 */
@Path("/service")
public class ServiceLookupRestService extends AbstractRestService {
    @Inject
    private ServiceLookupService serviceLookupService;

    @Path("/lookup")
    @Produces("application/json")
    @GET
    public List<ServiceInstanceMetadata> lookup(@QueryParam("serviceTechnicalDesignId") String serviceTechnicalDesignId, @QueryParam("p1") double p1, @QueryParam("p2") double p2) {
        List<ServiceInstanceMetadata> res;

        try {
            res = serviceLookupService.getServiceInstancesForService(serviceTechnicalDesignId, p1, p2);
        } catch (NoServicesFoundException e) {
            throw new WebApplicationException(Response.status(NOT_FOUND).entity("Unable to find any service implementation of \""+serviceTechnicalDesignId+"\" with a boundary containing [" + p1 + ", " + p2 + "]").build());
        }

        return res;
    }

    @Path("/lookup/{serviceTechnicalDesignId}")
    @Produces("application/json")
    @GET
    public List<ServiceInstanceMetadata> lookupInstances(@PathParam("serviceTechnicalDesignId") String serviceTechnicalDesignId, @QueryParam("wkt") String location) {
        List<ServiceInstanceMetadata> res;

        try {
            res = serviceLookupService.getServiceInstancesForService(serviceTechnicalDesignId, location);
        } catch (NoServicesFoundException e) {
            throw new WebApplicationException(Response.status(NOT_FOUND).entity("Unable to find any service implementation of \""+serviceTechnicalDesignId+"\" with a boundary defined by \"" + location + "\"").build());
        }

        return res;
    }
}
