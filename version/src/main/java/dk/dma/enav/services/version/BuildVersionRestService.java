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
package dk.dma.enav.services.version;

import dk.dma.embryo.common.configuration.Property;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Returns the server buildtimestamp allowing the client to assess whether client and server are from the same build.
 */
@Path("/version")
public class BuildVersionRestService {
        @Inject @Property(value = "embryo.buildtimestamp", defaultValue = "-1")
        private String version;

        @GET
        @Path("/build-version")
        @Produces("text/plain")
        @NoCache
        public String buildVersion() {
            return version;
        }
    }
