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
import dk.dma.embryo.common.log.EmbryoLogService;
import dk.dma.embryo.user.couchdb.CouchToken;
import dk.dma.embryo.user.model.SailorRole;
import dk.dma.embryo.user.model.SecuredUser;
import dk.dma.embryo.user.model.UsernamePassword;
import dk.dma.embryo.user.persistence.RealmDao;
import dk.dma.embryo.user.security.Subject;
import dk.dma.embryo.user.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.ejb.FinderException;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/authentication")
@Slf4j
public class AuthenticationService extends AbstractRestService {
    @Inject
    private Subject subject;

    @Inject
    private UserService userService;

    @Inject
    private RealmDao realmRepository;

    @Inject
    private EmbryoLogService embryoLogService;

    @Inject
    private CouchToken couchToken;

    @Inject
    @Property("embryo.osm.url")
    private String osm;

    @GET
    @Path("/details")
    @Produces("application/json")
    @GZIP
    public Response details(@Context Request request) {
        SecuredUser user = subject.getUser();
        if (user == null) {
            throw new UserNotAuthenticated("details");
        }

        Details details = new Details();

        if (subject.hasRole(SailorRole.class)) {
            SailorRole sailor = realmRepository.getSailor(subject.getUserId());
            details.setShipMmsi("" + sailor.getVessel().getMmsi());
        }

        String[] rolesJson = new String[] { user.getRole().getLogicalName() };
        details.setProjection("EPSG:900913");
        details.setUserName(user.getUserName());
        details.setPermissions(rolesJson);
        details.setOsm(osm);
        details.setT(couchToken.generate(user.getUserName()));

        log.info("details() : {}", details);
        
        return super.getResponse(request, details, NO_CACHE);
    }

    @GET
    @Path("/logout")
    @Produces("application/json")
    @GZIP
    @NoCache
    public void logout() {
        if (subject != null && subject.getUser() != null) {
            log.info("User {} logged out", subject.getUser().getUserName());
            embryoLogService.info("User " + subject.getUser().getUserName() + " logged out");
        } else {
            log.error("Attempt to logout all though not logged in");
            embryoLogService.error("Attempt to logout all though not logged in");
        }
        subject.logout();
    }

    @GET
    @Path("/login")
    @Produces("application/json")
    @GZIP
    @NoCache
    public Details login(
            @Context Request request,
            @QueryParam("userName") String userName, 
            @QueryParam("password") String password) {
        
        try {
            SecuredUser user = subject.login(userName, password);

            if (user != null) {
                log.info("User {} logged in", userName);
                embryoLogService.info("User " + userName + " logged in");
                return (Details)details(request).getEntity();
            } else {
                // We should probably never end in this block.
                log.info("User {} not logged in (wrong username / password)", userName);
                embryoLogService.info("User " + userName + " not logged in (wrong username / password)");
                throw new UserNotAuthenticated("not shiro");
            }
        } catch (org.apache.shiro.authc.IncorrectCredentialsException e) {
            log.info("User {} not logged in (wrong username / password)", userName);
            embryoLogService.info("User " + userName + " not logged in (wrong username / password)");
            throw new UserNotAuthenticated("login failed");
        }

    }

    @GET
    @Path("/change-password")
    @GZIP
    public Response getUserForUuid(@Context Request request, @QueryParam("uuid") String uuid) {
        SecuredUser user = subject.findUserWithUuid(uuid);
        
        String userName = null;
        if (user != null) {
            userName = user.getUserName();
        }
        
        return super.getResponse(request, userName, NO_CACHE);
    }

    @POST
    @Path("/change-password")
    @Consumes("application/json")
    @GZIP
    public void changePassword(ChangedPassword changedPassword) {
        if (changedPassword.getUuid() == null || changedPassword.getUuid().isEmpty()) {
            throw new ParameterMissing("UUID is missing.");
        } else if (changedPassword.getPassword() == null || changedPassword.getPassword().isEmpty()) {
            throw new ParameterMissing("Password is missing.");
        } else {
            try {
                userService.changePassword(changedPassword.getUuid(), changedPassword.getPassword());
            } catch (FinderException e) {
                throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity(e.getMessage()).build());
            }
        }
    }
    
    @POST
    @Path("/change-password-direct")
    @Consumes("application/json")
    @GZIP
    public void changePasswordDirect(UsernamePassword newPassword) {
        if (newPassword == null) {
            throw new ParameterMissing("Must supply Username and Password.");
        }

        try {
            userService.changePassword(newPassword);
        } catch (FinderException e) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity(e.getMessage()).build());
        }
    }

    @GET
    @Path("/isloggedin")
    @Produces("application/json")
    @GZIP
    @NoCache
    public void isLoggedIn() {
        // All this method does is return 200 if user is logged in and 401 otherwise (through Shiro config).
    }
    
    public static class UserNotAuthenticated extends WebApplicationException {
        private static final long serialVersionUID = 7940360206022406100L;

        public UserNotAuthenticated(String error) {
            super(Response.status(Status.UNAUTHORIZED).entity("{\"error\":\"" + error + "\"}").build());
        }
    }

    public static class ParameterMissing extends WebApplicationException {
        private static final long serialVersionUID = 3153251523607924598L;

        public ParameterMissing(String error) {
            super(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
        }
    }
    @Data
    public static class ChangedPassword {
        private String uuid;
        private String password;
    }

    @Data
    public static class Details {
        private String shipMmsi;
        private String projection;
        private String userName;
        private String[] permissions;
        private String osm;
        private String t;
    }
}
