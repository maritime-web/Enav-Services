package dk.dma.embryo.user.json;

import com.google.common.io.Resources;
import dk.dma.embryo.common.configuration.Property;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Steen on 01-04-2016.
 *
 */
@WebServlet(value = "/keycloak.json")
public class KeycloakConfigurationServlet extends HttpServlet {
    @Inject @Property(value = "enav-service.keycloak.web-client.configuration.url", substituteSystemProperties = true)
    private String configUrl;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.getWriter().print(getConfig());
    }

    private String getConfig() throws IOException {
        return Resources.toString(new URL(configUrl), Charset.forName("UTF-8"));
    }
}
