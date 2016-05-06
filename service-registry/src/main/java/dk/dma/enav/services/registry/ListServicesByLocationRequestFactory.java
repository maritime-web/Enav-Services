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
package dk.dma.enav.services.registry;

import ietf.lost1.ListServicesByLocation;
import ietf.lost1.Location;
import ietf.lost1.ObjectFactory;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

/**
 * Created by Steen on 28-04-2016.
 *
 */
public class ListServicesByLocationRequestFactory {
    private static final String POINT_TEMPLATE = "<p2:Point id=\"point1\" srsName=\"urn:ogc:def:crs:EPSG::4326\" xmlns:p2=\"http://www.opengis.net/gml\"><p2:pos>%1$f %2$f</p2:pos></p2:Point>";

    @Inject
    private Logger logger;

    public String createRequest(double p1, double p2) {
        Document document;
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(new InputSource(new StringReader(String.format(Locale.ENGLISH, POINT_TEMPLATE, p1, p2) )));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException("Bugger!", e);
        }

        ObjectFactory objectFactory = new ObjectFactory();
        ListServicesByLocation listServicesByLocationRequest = objectFactory.createListServicesByLocation();
        listServicesByLocationRequest.setRecursive(true);
        Location location = objectFactory.createLocation();
        location.setId(String.valueOf(document.getDocumentElement().hashCode()));
        location.setProfile("geodetic-2d");
        location.getExtensionPoint().add(document.getDocumentElement());
        listServicesByLocationRequest.getLocation().add(location);
        listServicesByLocationRequest.setService("urn");

        JAXBContext jaxbContext;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            jaxbContext = JAXBContext.newInstance(ListServicesByLocation.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(listServicesByLocationRequest, os);

            String res = os.toString("UTF-8");
            logger.debug(res);
            return res;

        } catch (JAXBException | IOException e) {
            throw new RuntimeException("");
        }

    }

}
