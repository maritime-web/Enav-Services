package dk.dma.embryo.user.security.keycloak;

import dk.dma.embryo.user.security.Subject;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.slf4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * Created by Steen on 24-02-2016.
 *
 */
@WebListener
public class SubjectProducer implements ServletRequestListener, ServletRequestAttributeListener {
    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    private Logger logger;

    private final static ThreadLocal<HttpServletRequest> holder = new ThreadLocal<>();

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        holder.remove();
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        holder.set((HttpServletRequest)sre.getServletRequest());
    }

    @SuppressWarnings("unused")
    @Produces @RequestScoped
    public Subject produceSubject(@New KeycloakSubject keycloakSubject) {
        HttpServletRequest httpServletRequest = holder.get();
        if (httpServletRequest == null) {
            logger.info("httpServletRequest is NULL");
            return keycloakSubject;
        }
        KeycloakSecurityContext securityContext = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
        if (securityContext == null) {
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>   KeycloakSecurityContext is null?");
        } else {
            logger.info("********************************************");
            logger.info(securityContext.getRealm());
            logger.info("TokenString as json:\n" +toJson(securityContext.getTokenString()));
            logger.info("********************************************");
        }
        keycloakSubject.setSecurityContext(securityContext);
        return keycloakSubject;
    }

    private String toJson(String tokenString) {
        String res = "???? ERROR ?????";
        try {
            JWSInput j = new JWSInput(tokenString);
            res = new String(j.getContent(), "UTF-8");
        } catch (UnsupportedEncodingException | JWSInputException e) {
            logger.info("====== Got error converting Base64 to json =========", e);
        }
        return res;
    }

    @Override
    public void attributeAdded(ServletRequestAttributeEvent srae) {
    }

    @Override
    public void attributeRemoved(ServletRequestAttributeEvent srae) {

    }

    @Override
    public void attributeReplaced(ServletRequestAttributeEvent srae) {

    }
}
