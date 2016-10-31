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

import dk.dma.enav.services.registry.mc.model.Xml;
import dk.dma.enav.services.registry.mc.model.Xsd;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 *
 */
public class Base64Decoder {
    byte[] decode(Xml xmlRef) {
        assertNotNull(xmlRef, "xml reference must not be null");
        assertNotNull(xmlRef.getContent(), "xml reference must refer to xml content");
        assertValidcontentType(xmlRef.getContentContentType());

        return xmlRef.getContent().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] decode(Xsd xsdRef) {
        assertNotNull(xsdRef, "xsd reference must not be null");
        assertNotNull(xsdRef.getContent(), "xsd reference must refer to xsd content");
        assertValidcontentType(xsdRef.getContentContentType());

//        return decode(xsdRef.getContent());
        return new byte[]{};
    }

    private byte[] decode(String base64String) {
        return Base64.getDecoder().decode(base64String.getBytes(StandardCharsets.US_ASCII));
    }

    private void assertNotNull(Object o, String msg) {
        if (o == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    private void assertValidcontentType(String contentType) {
        if (!(contentType.equals("text/xml") || contentType.equals("application/xml"))) {
            throw new RuntimeException("Unknown content type: " + contentType);
        }
    }
}
