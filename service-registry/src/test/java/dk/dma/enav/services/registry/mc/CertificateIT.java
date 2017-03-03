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
