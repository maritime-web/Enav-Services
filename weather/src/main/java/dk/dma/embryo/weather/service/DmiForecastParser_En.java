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
package dk.dma.embryo.weather.service;

import dk.dma.embryo.common.configuration.Property;
import dk.dma.embryo.weather.model.DistrictForecast;
import dk.dma.embryo.weather.model.RegionForecast;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Named
public class DmiForecastParser_En {

    @Property("embryo.weather.dmi.parser.districts.en")
    @Inject
    private Set<String> districts;

    private boolean closeReader;

    RegionForecast parse(InputStream is) throws IOException {
        if (is instanceof BufferedInputStream) {
            return parse((BufferedInputStream) is);
        }
        return parse(new BufferedInputStream(is));
    }

    RegionForecast parse(File file) throws IOException {
        closeReader = true;
        return parse(new FileInputStream(file));
    }

    private RegionForecast parse(BufferedInputStream is) throws IOException {
        RegionForecast result = new RegionForecast();

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new InputSource(is));

            // Normalize text representation
            doc.getDocumentElement().normalize();

            NodeList children = doc.getDocumentElement().getChildNodes();
            String fromText = null;

            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i) instanceof Element) {
                    Element elem = (Element) children.item(i);

                    if (elem.getNodeName().equalsIgnoreCase("dato")) {
                        fromText = extractElementText(elem);
                    } else if (elem.getNodeName().equalsIgnoreCase("oversigttidspunkt")) {
                        String time = extractElementText(elem);
                        time = time.replace("UTC.", "UTC");
                        time = time.replace("Synopsis", "");
                        time = time.replace("synopsis", "");
                        result.setTime(time.trim());
                    } else if (elem.getNodeName().equalsIgnoreCase("gyldighed")) {
                        DateParser parser = new DateParser(fromText, extractElementText(elem));
                        result.setFrom(parser.getFrom().toDate());
                        result.setTo(parser.getTo().toDate());
                    } else if (elem.getNodeName().equalsIgnoreCase("synoptic")) {
                        String text = extractElementText(elem, "oversigt");
                        result.setDesc(text);
                    } else if (districts.contains(elem.getNodeName())) {
                        result.getDistricts().add(extractDistrikt(elem));
                    }
                }
            }
        } catch (RuntimeException | ParserConfigurationException | SAXException e) {
            throw new IOException("Error parsing weather forecast", e);
        } finally {
            if (closeReader) {
                is.close();
            }
        }

        return result;
    }

    private DistrictForecast extractDistrikt(Element distrikt) throws IOException {
        DistrictForecast forecast = new DistrictForecast();

        String name = distrikt.getNodeName();

        String forecastElemName = "udsigtfor" + name;
        String wavesElemName = "waves" + name;
        String iceElemName = "ice" + name;

        if ("nunapisuateakangia".equals(name)) {
            forecastElemName = "udsigtfornunapisuatakangia";
            wavesElemName = "waveskangia";
        } else if ("nunapisuatakitaa".equals(name)) {
            forecastElemName = "udsigtfornunapisuatakitaa";
            wavesElemName = "waveskitaa";
        }

        forecast.setName(distrikt.getAttribute("name").replace(":", ""));
        forecast.setForecast(extractElementText(distrikt, forecastElemName));
        forecast.setWaves(extractElementText(distrikt, wavesElemName));
        if (isElementAvailable(distrikt, iceElemName)) {
            forecast.setIce(extractElementText(distrikt, iceElemName));
        }
        return forecast;
    }

    private boolean isElementAvailable(Element root, String elementName) {
        NodeList uniqueList = root.getElementsByTagName(elementName);
        return uniqueList.getLength() > 0;
    }

    private String extractElementText(Element root, String elementName) throws IOException {
        NodeList uniqueList = root.getElementsByTagName(elementName);
        if (uniqueList.getLength() != 1) {
            throw new IOException("Expected exactly one <" + elementName + "> element within <" + root.getNodeName()
                    + "> element");
        }

        return extractElementText((Element) uniqueList.item(0));
    }

    private String extractElementText(Element element) throws IOException {
        List<Node> textList = new ArrayList<>();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if ("text".equals(n.getNodeName())) {
                textList.add(n);
            }
        }
        if (textList.size() != 1) {
            throw new IOException("Expected exactly one <text> element within <" + element.getNodeName() + "> element");
        }

        return trim(textList.get(0).getTextContent());
    }

    private static String trim(String input) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(input));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(line.trim());
        }
        return result.toString().trim();
    }
}
