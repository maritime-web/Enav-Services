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

import dk.dma.embryo.common.configuration.Configuration;
import dk.dma.embryo.user.model.SecuredUser;
import dk.dma.embryo.user.model.User;
import dk.dma.embryo.user.persistence.RealmDao;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Steen on 15-03-2016.
 *
 */
public class KeycloakBearerOnlyRealm extends JpaRealm {
    public static final Logger LOGGER = LoggerFactory.getLogger(KeycloakBearerOnlyRealm.class);

    private static final String REALM_NAME = "KeycloakBearerOnlyRealm";

    @SuppressWarnings("unused")
    public KeycloakBearerOnlyRealm() {
        setAuthenticationTokenClass(KeycloakBearerToken.class);
    }

    @Override
    public String getName() {
        return REALM_NAME;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        KeycloakBearerToken keycloakBearerToken = (KeycloakBearerToken) token;

        SecuredUser user = updateUser(keycloakBearerToken);

        return new SimpleAuthenticationInfo(user.getId(), token.getCredentials(), getName());
    }

    private SecuredUser updateUser(KeycloakBearerToken token) {
        RealmDao realmDao = Configuration.getBean(RealmDao.class);
        UserService userService = Configuration.getBean(UserService.class);
        SecuredUser user = realmDao.findByMaritimeCloudId(token.getMaritimeId());
        if (user == null) {
            userService.createFrom(token.toUser());
        } else {
            userService.mergeWith(token.toUser());
        }
        return realmDao.findByMaritimeCloudId(token.getMaritimeId());
    }
}
