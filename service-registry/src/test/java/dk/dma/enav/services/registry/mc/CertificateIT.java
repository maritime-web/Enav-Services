package dk.dma.enav.services.registry.mc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;

/**
 * @author Klaus Groenbaek
 *         Created 03/03/17.
 */
public class CertificateIT {

    @Test
    /**
     * This checks that the JVM can connect to a server that uses LetEncrypt ssl certificates
     * On Oracle JVM this is not supported until 1.8.0_101
     */
    public void checkServiceRegisterHttps() throws Exception {
        URL url = new URL("https://sr.maritimecloud.net/v2/api-docs");

        URLConnection yc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        in.close();
    }
}
