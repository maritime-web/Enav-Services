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
package dk.dma.embryo.user.shiro;

import dk.dma.embryo.user.service.KeycloakBearerToken;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.BearerTokenRequestAuthenticator;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.servlet.OIDCServletHttpFacade;
import org.keycloak.adapters.spi.AuthOutcome;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * Created by Steen on 15-03-2016.
 *
 */
public class KeycloakAuthenticationFilter extends AuthenticatingFilter {
    public static final Logger LOGGER = LoggerFactory.getLogger(KeycloakAuthenticationFilter.class);

    private AdapterDeploymentContext deploymentContext;

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        return new KeycloakBearerToken(getAccessToken(request, response));
    }

    private AccessToken getAccessToken(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        OIDCServletHttpFacade facade = new OIDCServletHttpFacade(httpRequest, httpResponse);
        KeycloakDeployment keycloakDeployment = getDeploymentContext(httpRequest).resolveDeployment(facade);
        BearerTokenRequestAuthenticator authenticator = new BearerTokenRequestAuthenticator(keycloakDeployment);
        AuthOutcome outcome = authenticator.authenticate(facade);
        if (outcome == AuthOutcome.AUTHENTICATED ) {
            return authenticator.getToken();
            //todo verifySSL
        }
        return null;
    }

    private AdapterDeploymentContext getDeploymentContext(HttpServletRequest request) {
        if (deploymentContext == null) {
            deploymentContext = createDeploymentContext(request);
        }

        return deploymentContext;
    }

    private AdapterDeploymentContext createDeploymentContext(HttpServletRequest request) {
        String path = "/WEB-INF/keycloak.json";
        InputStream is = request.getServletContext().getResourceAsStream(path);
        KeycloakDeployment kd = KeycloakDeploymentBuilder.build(is);

        return new AdapterDeploymentContext(kd);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (!executeLogin(request, response)) {
            HttpServletResponse httpResp = WebUtils.toHttp(response);
            httpResp.setContentType("application/json");
            httpResp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            Util.writeJson(httpResp, new Error(Error.AuthCode.UNAUTHENTICATED, "User not logged in"));
            return false;
        }
        return true;
    }
}
