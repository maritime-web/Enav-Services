package dk.dma.enav.services.registry.mc.api;

import dk.dma.enav.services.registry.mc.ApiClient;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class InstanceResourceApiTest {
    @Test
    public void name() throws Exception {
        InstanceResourceApi cut = new InstanceResourceApi(new ApiClient());
        cut.getAll();
    }

    @Test
    public void namea() throws Exception {
        String s = "";
        Pattern pattern = Pattern.compile(".*page=(?<page>\\d+).*size=(?<size>\\d+)");
        Matcher matcher = pattern.matcher(s);

    }
}