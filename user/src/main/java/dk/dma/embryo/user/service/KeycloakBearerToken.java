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
package dk.dma.embryo.user.service;

import dk.dma.embryo.user.model.KnownRoles;
import dk.dma.embryo.user.model.User;
import org.apache.shiro.authc.AuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Steen on 15-03-2016.
 *
 */
public class KeycloakBearerToken implements AuthenticationToken {
    public static final Logger LOGGER = LoggerFactory.getLogger(KeycloakBearerToken.class);

    private final AccessToken token;

    public KeycloakBearerToken(AccessToken token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return getMaritimeId();
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    public String getMaritimeId() {
        return token.getSubject();
    }

    public String getPreferredUserName() {
        return token.getPreferredUsername();
    }

    public String getEmail() {
        return token.getEmail();
    }

    public String getBalticWebRole() {
        String role = getRoleFromToken();
        if (role == null) {
            role = getDefaultRole();
        }
        if (role.equals(KnownRoles.Sailor.toString())) {
            if (getMmsiAttribute() == null) {
                throw new IllegalArgumentException("The user is assigned the role of sailor but the mmsi number missing");
            }
        }

        if (unknownRole(role)) {
            throw new IllegalArgumentException("Don't know the role '" + role + "'");
        }

        return role;
    }

    private String getRoleFromToken() {
        return (String) token.getOtherClaims().get("role");
    }

    private String getDefaultRole() {
        return KnownRoles.Shore.toString();
    }

    private boolean unknownRole(String role) {
        return !KnownRoles.isKnown(role);
    }

    private Long getMmsiAttribute() {
        Long mmsi = null;
        Object mmsiCandidate = token.getOtherClaims().get("mmsi");
        if (mmsiCandidate != null) {
            if (mmsiCandidate instanceof String) {
                mmsi = Long.valueOf((String) mmsiCandidate);
            } else if (mmsiCandidate instanceof Integer) {
                mmsi = Long.valueOf((Integer)mmsiCandidate);
            } else {
                mmsi = (Long) mmsiCandidate;
            }
        }
        return mmsi;
    }


    public User toUser() {
        User user = new User();
        user.setMaritimeCloudId(getMaritimeId());
        user.setLogin(token.getPreferredUsername());
        user.setEmail(token.getEmail());
        user.setShipMmsi(getMmsiAttribute());
        user.setRole(getBalticWebRole());
        user.setAisFilterName(getAisFilterNameAttribute());
        return user;
    }

    private String getAisFilterNameAttribute() {
        return (String) token.getOtherClaims().get("aisFilterName");
    }
}
