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
package dk.dma.enav.services.s124;

import org.jboss.resteasy.annotations.GZIP;
import org.json.XML;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * REST endpoint for searching S-124 services as defined in the Maritime Cloud Service Registry.
 * <p>
 * The endpoint is called with a list of service instance ID's, which are then queried in parallel.
 */
@Path("/s-124")
public class S124RestService {

    @Inject
    private S124Service s124Service;

    /**
     * Fetches the published messages from the given service instances in parallel
     * @param instanceIds the MC Service Registry instance IDs to fetch messages from
     * @param id Get specific S-124 message by supplying an ID (optional)
     * @param status Get all S-124 message with specific status (in-force or cancelled?) (optional)
     * @param wkt WKT for the geometry extent of the area (optional)
     */
    @GET
    @Path("/messages")
    @Produces("application/json;charset=UTF-8")
    @GZIP
    public List<String> getS124Messages(
            @QueryParam("instanceId") List<String> instanceIds,
            @QueryParam("id") Integer id,
            @QueryParam("status") Integer status,
            @QueryParam("wkt") String wkt) throws Exception {

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("S124-test-dummy.xml").toURI()));
            StringWriter writer = new StringWriter();
            XML.toJSONObject(new String(bytes, Charset.forName("UTF-8"))).write(writer);
            ArrayList<String> res = new ArrayList<>();
            res.add(writer.toString());
            return res;
//            return s124Service.getMessages(instanceIds, id, status, wkt);
        } catch (Exception e) {
            throw new WebApplicationException("Failed loading S-124 messages: " + e.getMessage(), 500);
        }
    }

}
