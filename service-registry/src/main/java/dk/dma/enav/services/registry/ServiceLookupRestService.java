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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * Created by Steen on 20-04-2016.
 *
 */
@Path("/service")
public class ServiceLookupRestService extends AbstractRestService {
    @Inject
    private LostService lostService;

    @Path("/lookup")
    @Produces("application/json")
    @GET
    public List<ServiceInstanceMetadata> lookup(@QueryParam("p1") double p1, @QueryParam("p2") double p2) {
        List<ServiceInstanceMetadata> res = new ArrayList<>();
        ServiceInstanceMetadata metadata = new ServiceInstanceMetadata(
                "urn:mrnx:mcl:service:dma:nw-nm:v0.1",
                "urn:mrnx:mcl:service:instance:dma:nw-nm:v0.1",
                "DK NW-NM",
                "<p2:LinearRing xmlns:p2=\"http://www.opengis.net/gml\">\n" +
                "      <p2:pos>14.0020751953125 54.95869417101662</p2:pos> +\n" +
                "      <p2:pos>15.0457763671875 55.6930679264579</p2:pos> +\n" +
                "      <p2:pos>16.5069580078125 55.363502833950776</p2:pos> +\n" +
                "      <p2:pos>14.633789062500002 54.53383250794428</p2:pos> +\n" +
                "      <p2:pos>14.414062499999998 54.65794628989232</p2:pos> +\n" +
                "      <p2:pos>14.3975830078125 54.81334841741929</p2:pos> +\n" +
                "      <p2:pos>14.161376953124998 54.81334841741929</p2:pos> +\n" +
                "      <p2:pos>14.0020751953125 54.95869417101662</p2:pos> +\n" +
                "</p2:LinearRing>",
                "http://niord.e-navigation.net/rest/public/v1/messages");
        res.add(metadata);

        try {
            res = lostService.findAllServices(p1, p2);
        } catch (LostResourceNotFoundException e) {
            throw new WebApplicationException(Response.status(NOT_FOUND).entity("Unable to find any services with a boundary containing [" + p1 + ", " + p2 + "]").build());
        }

        return res;
    }
}
