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

import dk.dma.enav.services.registry.mc.api.XsdresourceApi;
import dk.dma.enav.services.registry.mc.model.Xsd;
import org.apache.commons.collections.iterators.ArrayListIterator;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.junit.Test;
import org.xml.sax.InputSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Steen on 13-10-2016.
 */
public class ATest {
    @Test
    public void name() throws Exception {
        Charset.availableCharsets().keySet().forEach(System.out::println);

    }

    @Test
    public void aaname() throws Exception {
        String base64Instance = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4NCjxTZXJ2aWNlSW5zdGFuY2VTY2hlbWE6c2VydmljZUluc3RhbmNlIHhtbG5zOlNlcnZpY2VJbnN0YW5jZVNjaGVtYT0iaHR0cDovL2VmZmljaWVuc2VhMi5vcmcvbWFyaXRpbWUtY2xvdWQvc2VydmljZS1yZWdpc3RyeS92MS9TZXJ2aWNlSW5zdGFuY2VTY2hlbWEueHNkIiB4bWxuczpTZXJ2aWNlU3BlY2lmaWNhdGlvblNjaGVtYT0iaHR0cDovL2VmZmljaWVuc2VhMi5vcmcvbWFyaXRpbWUtY2xvdWQvc2VydmljZS1yZWdpc3RyeS92MS9TZXJ2aWNlU3BlY2lmaWNhdGlvblNjaGVtYS54c2QiIHhtbG5zOnhzaT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEtaW5zdGFuY2UiIHhzaTpzY2hlbWFMb2NhdGlvbj0iaHR0cDovL2VmZmljaWVuc2VhMi5vcmcvbWFyaXRpbWUtY2xvdWQvc2VydmljZS1yZWdpc3RyeS92MS9TZXJ2aWNlSW5zdGFuY2VTY2hlbWEueHNkIFNlcnZpY2VJbnN0YW5jZVNjaGVtYS54c2QgIj4NCjxuYW1lPlZJUyBJTU8xMjM0NTY3PC9uYW1lPg0KPGlkPnVybjptcm54OnN0bTpzZXJ2aWNlOmluc3RhbmNlOlZJUzpTTUE6SU1PMTIzNDU2NzwvaWQ+DQo8dmVyc2lvbj4wLjE8L3ZlcnNpb24+DQo8c3RhdHVzPnByb3Zpc2lvbmFsPC9zdGF0dXM+DQo8ZGVzY3JpcHRpb24+UHVibGlzaGVzIHZveWFnZSBwbGFuczwvZGVzY3JpcHRpb24+DQo8a2V5d29yZHM+VklTLFZveWFnZSBJbmZvcm1hdGlvbiBTZXJ2aWNlLElNTzEyMzQ1Njc8L2tleXdvcmRzPg0KPFVSTD5odHRwczovL3Nqb2ZhcnRzdmVya2V0LnNlL3N0bS92aXMvdmlzX2ltbzEyMzQ1Njc8L1VSTD4NCjxyZXF1aXJlc0F1dGhvcml6YXRpb24+dHJ1ZTwvcmVxdWlyZXNBdXRob3JpemF0aW9uPg0KDQo8aW1wbGVtZW50c1NlcnZpY2VEZXNpZ24+DQoJPGlkPnVybjptcm54OnN0bTpzZXJ2aWNlOmRlc2lnbjpWSVM6U01BPC9pZD4NCgk8dmVyc2lvbj4wLjE8L3ZlcnNpb24+DQo8L2ltcGxlbWVudHNTZXJ2aWNlRGVzaWduPg0KDQo8b2ZmZXJzU2VydmljZUxldmVsPg0KCTxhdmFpbGFiaWxpdHk+MDwvYXZhaWxhYmlsaXR5Pg0KCTxuYW1lPlNlcnZpY2UgbGV2ZWw8L25hbWU+DQoJPGRlc2NyaXB0aW9uPjwvZGVzY3JpcHRpb24+DQo8L29mZmVyc1NlcnZpY2VMZXZlbD4NCjxjb3ZlcnNBcmVhcz4NCgk8Y292ZXJzQXJlYT4NCgkJPG5hbWU+VGhlIHdvcmxkPC9uYW1lPg0KCQk8ZGVzY3JpcHRpb24+Q292ZXJzIHRoZSB3b3JsZDwvZGVzY3JpcHRpb24+DQoJCTxnZW9tZXRyeUFzV0tUPjwvZ2VvbWV0cnlBc1dLVD4NCgk8L2NvdmVyc0FyZWE+DQo8L2NvdmVyc0FyZWFzPg0KPHByb2R1Y2VkQnk+DQoJPGlkPnVybjptcm46bWNsOm9yZzpDSU1ORTwvaWQ+DQoJPG5hbWU+Q0lNTkU8L25hbWU+DQoJPGRlc2NyaXB0aW9uPjwvZGVzY3JpcHRpb24+DQoJPGNvbnRhY3RJbmZvPjwvY29udGFjdEluZm8+DQoJPGlzQ29tbWVyY2lhbD5mYWxzZTwvaXNDb21tZXJjaWFsPg0KPC9wcm9kdWNlZEJ5Pg0KPHByb3ZpZGVkQnk+DQoJPGlkPnVybjptcm46bWNsOm9yZzpTTUE8L2lkPg0KCTxuYW1lPlNNQTwvbmFtZT4NCgk8ZGVzY3JpcHRpb24+PC9kZXNjcmlwdGlvbj4NCgk8Y29udGFjdEluZm8+PC9jb250YWN0SW5mbz4NCgk8aXNDb21tZXJjaWFsPmZhbHNlPC9pc0NvbW1lcmNpYWw+DQo8L3Byb3ZpZGVkQnk+DQo8L1NlcnZpY2VJbnN0YW5jZVNjaGVtYTpzZXJ2aWNlSW5zdGFuY2U+DQoNCg==";

        byte[] decodedInstance = Base64.getDecoder().decode(base64Instance.getBytes("US-ASCII"));
        System.out.println(new String(decodedInstance, "UTF-8"));
        XPath xPath = XPathFactory.newInstance().newXPath();

        Map<String, String> nsm = new HashMap<>();
        nsm.put("ServiceInstanceSchema", "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd");
        nsm.put("http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd", "ServiceInstanceSchema");
        NamespaceContext context = new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                System.out.println(prefix);
                return nsm.get(prefix);
            }

            @Override
            public String getPrefix(String namespaceURI) {
                System.out.println(namespaceURI);
                return nsm.get(namespaceURI);
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                System.out.println(namespaceURI);
                List<String> list = Collections.singletonList(nsm.get(namespaceURI));
                return list.iterator();
            }
        };
        xPath.setNamespaceContext(context);
        String url = xPath.evaluate("//ServiceInstanceSchema:serviceInstance/URL", new InputSource(new InputStreamReader(new ByteArrayInputStream(decodedInstance), "UTF-8")));
        String coverage = xPath.evaluate("//ServiceInstanceSchema:serviceInstance/coversAreas/coversArea/geometryAsWKT", new InputSource(new InputStreamReader(new ByteArrayInputStream(decodedInstance), "UTF-8")));

        System.out.println(url);
        System.out.println(coverage);

    }

    @Test
    public void loadXsds() throws Exception {
        XsdresourceApi api = new XsdresourceApi();

        List<Xsd> xsds = api.getAllXsdsUsingGET(null, null, null);

        xsds.forEach(this::writeToFile);
    }

    private void writeToFile(Xsd xsd) {
        byte[] theXml = new Base64Decoder().decode(xsd);
        FileWriterWithEncoding writer;
        try {
            writer = new FileWriterWithEncoding(xsd.getName(), StandardCharsets.UTF_8);
            writer.write(new String(theXml, StandardCharsets.UTF_8));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
