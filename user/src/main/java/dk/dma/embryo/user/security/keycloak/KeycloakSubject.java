package dk.dma.embryo.user.security.keycloak;

import dk.dma.embryo.user.model.Role;
import dk.dma.embryo.user.model.SecuredUser;
import dk.dma.embryo.user.persistence.RealmDao;
import dk.dma.embryo.user.security.Subject;
import dk.dma.embryo.vessel.persistence.ScheduleDao;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Steen on 24-02-2016.
 *
 */
@KeycloakSubjectBean
public class KeycloakSubject implements Subject {
    @Inject
    private RealmDao realmDao;

    @Inject
    private ScheduleDao scheduleDao;

    @Inject
    private Logger logger;

    private KeycloakSecurityContext securityContext;

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
        Role role = getUser().getRole();
        try {
            return role.getLogicalName().equals(roleType.newInstance().getLogicalName());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long getUserId() {
        return getUser().getId();
    }

    @Override
    public SecuredUser getUser() {
        AccessToken token = securityContext.getToken();
        logger.info("===========================");
        logger.info("Subject: " + token.getSubject());
        logger.info("Id: " + token.getId());
        logger.info("PreferredUsername: " + token.getPreferredUsername());
        logger.info("Name: " + token.getName());
        logger.info("===========================");

        return realmDao.findByUsername(token.getPreferredUsername());
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
        return securityContext != null;
    }

    @Override
    public SecuredUser getUserForEmail(String email) {
        return null;
    }

    @Override
    public SecuredUser findUserWithUuid(String uuid) {
        return null;
    }

    public void setSecurityContext(KeycloakSecurityContext securityContext) {
        this.securityContext = securityContext;
    }
}
