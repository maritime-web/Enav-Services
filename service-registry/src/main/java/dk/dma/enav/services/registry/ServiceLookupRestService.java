package dk.dma.enav.services.registry;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Map;

/**
 * Created by Steen on 20-04-2016.
 *
 */
@Path("/register")
public class ServiceLookupRestService {
    @Inject
    private LostServiceClient lostServiceClient;

    @Path("/lookup")
    @Consumes("application/json")
    @GET
    public Map<String, String> lookup() {
        return null;
    }
}
