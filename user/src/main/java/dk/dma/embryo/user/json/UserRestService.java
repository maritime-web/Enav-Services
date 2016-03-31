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
package dk.dma.embryo.user.json;

import dk.dma.embryo.common.configuration.Property;
import dk.dma.embryo.common.json.AbstractRestService;
import dk.dma.embryo.user.model.SecuredUser;
import dk.dma.embryo.user.model.User;
import dk.dma.embryo.user.service.UserService;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/user")
public class UserRestService extends AbstractRestService {

    @Inject
    private Logger logger;

    @Inject
    private UserService userService;

    @Inject
    @Property("embryo.ais.filters.namedSourceFilters")
    private Map<String, String> namedSourceFilters;

    @DELETE
    @Path("/delete/{login}")
    @Produces("application/json")
    public void delete(@PathParam("login") String login) {
        logger.info("Deleting " + login);
        userService.delete(login);
    }

    @PUT
    @Path("/create")
    @Consumes("application/json")
    public void create(User user) {
        logger.info("Creating new user '" + user.getLogin() + "'  in role '" + user.getRole() + "' access to AIS data '" + user.getAisFilterName() + "'");
        userService.create(user.getLogin(), user.getPassword(), user.getShipMmsi(), user.getEmail(), user.getRole(), user.getAisFilterName());
    }

    @PUT
    @Path("/edit")
    @Consumes("application/json")
    public void edit(User user) {
        logger.info("Editing new user '" + user.getLogin() + "'  in role '" + user.getRole() + "' access to AIS data '" + user.getAisFilterName() + "'");
        userService.edit(user.getLogin(), user.getShipMmsi(), user.getEmail(), user.getRole(), user.getAisFilterName());
    }

    
    @GET
    @GZIP
    @Path("/list")
    @Produces("application/json")
    public Response list(@Context Request request) {
        logger.info("/user/list called.");

        List<SecuredUser> users = userService.list();
        List<User> result = SecuredUser.toJsonModel(users);

        return super.getResponse(request, result, NO_CACHE);
    }

    @GET
    @Path("/available-source-filters")
    @Produces("application/json")
    @GZIP
    public Response namedSourceFilters(@Context Request request) {
        List sourceFilterNames = new ArrayList<>(namedSourceFilters.size());
        sourceFilterNames.addAll(namedSourceFilters.keySet());
        return super.getResponse(request, sourceFilterNames, MAX_AGE_15_MINUTES);
    }


}
