package dk.dma.embryo.user.security.keycloak;

import dk.dma.embryo.user.model.Role;
import dk.dma.embryo.user.model.SecuredUser;
import dk.dma.embryo.user.security.Subject;
import org.keycloak.KeycloakSecurityContext;

import java.util.List;

/**
 * Created by Steen on 24-02-2016.
 *
 */
public class KeycloakSubject implements Subject {
    private final KeycloakSecurityContext securityContext;

    public KeycloakSubject(KeycloakSecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    @Override
    public SecuredUser login(String userName, String password, Boolean rememberMe) {
        return null;
    }

    @Override
    public SecuredUser login(String userName, String password) {
        return null;
    }

    @Override
    public <R extends Role> boolean hasRole(Class<R> roleType) {
        return false;
    }

    @Override
    public Long getUserId() {
        return null;
    }

    @Override
    public SecuredUser getUser() {
        SecuredUser securedUser = new SecuredUser();
        securedUser.setUserName("A Test");
        return securedUser;
    }

    @Override
    public boolean hasOneOfRoles(List<Class<? extends Role>> roleTypes) {
        return false;
    }

    @Override
    public boolean authorizedToModifyVessel(Long mmsi) {
        return false;
    }

    @Override
    public void logout() {

    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public SecuredUser getUserForEmail(String email) {
        return null;
    }

    @Override
    public SecuredUser findUserWithUuid(String uuid) {
        return null;
    }
}
