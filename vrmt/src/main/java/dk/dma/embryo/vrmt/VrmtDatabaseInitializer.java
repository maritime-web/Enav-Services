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
package dk.dma.embryo.vrmt;

import dk.dma.embryo.common.EmbryonicException;
import dk.dma.embryo.common.configuration.Property;
import dk.dma.embryo.user.model.SailorRole;
import dk.dma.embryo.user.model.SecuredUser;
import dk.dma.embryo.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.IOException;


/**
 * Initialization of risk management databases for all arctic web users with the sailor role i.e. vessels.
 */
@SuppressWarnings("unused")
@Singleton
@Startup
@Slf4j
public class VrmtDatabaseInitializer {

    private UserService userService;
    private String host;
    private int port;
    private String user;
    private String password;

    //Required for EJB
    private VrmtDatabaseInitializer() {
    }

    @Inject
    public VrmtDatabaseInitializer(UserService userService,
                                   @Property("embryo.couchDb.host") String host,
                                   @Property("embryo.couchDb.port") int port,
                                   @Property("embryo.couchDb.user") String user,
                                   @Property("embryo.couchDb.password") String password) {
        this.userService = userService;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing VRMT databases");
        userService.list().stream().filter(this::onlySailors).forEach(this::createDb);
    }

    private boolean onlySailors(SecuredUser securedUser) {
        return securedUser.getRole() instanceof SailorRole;
    }

    private void createDb(SecuredUser securedUser) {
        String dbUrl = "http://" + host + ":" + port + "/" + securedUser.getUserName();
        try {
            Request request = Request.Put(dbUrl).connectTimeout(30000).socketTimeout(30000);
            HttpResponse httpResponse =  createExecutor().execute(request).returnResponse();
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (!(statusCode == 412 || statusCode < 300)) {
                throw new EmbryonicException("Unable to create CouchDb from url: " + dbUrl + " Status code: " + statusCode);
            }
        } catch (IOException e) {
            throw new EmbryonicException("Unable to create CouchDb from url: " + dbUrl, e);
        }
    }

    private Executor createExecutor() {
        HttpHost httpHost = new HttpHost(host, port);
        return Executor.newInstance()
                .auth(httpHost, user, password)
                .authPreemptive(httpHost);
    }

}
