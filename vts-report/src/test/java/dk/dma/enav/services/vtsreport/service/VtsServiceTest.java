package dk.dma.enav.services.vtsreport.service;
import dk.dma.embryo.common.configuration.PropertiesReader;
import dk.dma.embryo.common.configuration.PropertyFileService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import javax.inject.Inject;
//
import org.junit.BeforeClass;

/**
 * Created by rob on 6/13/17.
 */
@Slf4j
public class VtsServiceTest {
//    @BeforeClass
//    public static void init() throws Exception {
//        try {
//            String name = VtsServiceTest.class.getResource("/external-configuration.properties").toURI()
//                    .toString();
//            System.setProperty("arcticweb.configuration", name);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }

    @Inject
    private PropertyFileService propertyFileService;

    @Test
    public void test() throws IOException, URISyntaxException {

        PropertiesReader reader = new PropertiesReader();
        Properties properties = reader.read();
//        log.info("Reading configuration from");

        // VERIFY: default-configuration.properties
        Assert.assertEquals("dma.vts.test@gmail.com", properties.get("vtsservice.emailaddress.by.vtsshortname.BELTREP"));
        Assert.assertEquals("false", properties.get("embryo.notification.mail.enabled"));
        Assert.assertEquals("localhost", properties.get("embryo.notification.mail.smtp.host"));
    }

}

