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
package dk.dma.enav.services.s124;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dma.enav.services.s124.views.DataSet;
import org.geotools.xml.Parser;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@SuppressWarnings("RedundantThrows")
public class DataSetMapperTest {
    private Parser parser;

    @Before
    public void setUp() throws Exception {
        org.geotools.xml.Configuration config = new S124Configuration(S124XSD.getInstance());

        parser = new Parser(config);
        parser.setStrict(true);
    }

    @Test
    public void shouldMapMultipleTitles() throws ParserConfigurationException, SAXException, IOException {
        DataSet res = parse("S124-test-dummy-2.xml");

        assertThat(res.getTitle(), is("Denmark. The Waters South of Zealand. Boegestroem. light unlit."));
        assertThat(res.getNwPreamble().getOther().get("title"), isA(List.class));
        assertThat(res.getNwPreamble().getOther().get("title"), hasSize(2));
    }

    @Test
    public void shouldMapTitle() throws IOException, SAXException, ParserConfigurationException {
        DataSet res = parse("S124-test-dummy.xml");

        assertThat(res.getTitle(), is("Denmark. Kattegat. Randers Fiord. Light buoy replaced."));
    }

    @Test
    public void shouldMapAreaHeading() throws IOException, SAXException, ParserConfigurationException {
        DataSet res = parse("S124-test-dummy.xml");

        assertThat(res.getAreaHeading(), is("Kattegat - Kattegat - Randers Fiord"));
    }

    @Test
    public void shouldMapSingleGeneralAreas() throws ParserConfigurationException, SAXException, IOException {
        DataSet res = parse("S124-test-dummy-4.xml");

        assertThat(res.getNwPreamble().getGeneralAreas(), hasSize(1));
    }

    @Test
    public void shouldMapMultipleGeneralAreas() throws ParserConfigurationException, SAXException, IOException {
        DataSet res = parse("S124-test-dummy-2.xml");

        assertThat(res.getNwPreamble().getGeneralAreas(), hasSize(2));
    }

    @Test
    public void shouldMapReference() throws ParserConfigurationException, SAXException, IOException {
        DataSet res = parse("S124-test-5.xml");

        assertThat(res.getReferences(), hasSize(1));
    }

    @Test
    public void shouldBeJsonSerializable() throws IOException, SAXException, ParserConfigurationException {
        ObjectMapper mapper = new ObjectMapper();

        assertThat(mapper.writer().canSerialize(DataSet.class), is(true));
    }

    @Test
    public void shouldBeAbleToJsonSerializeDataSet() throws IOException, SAXException, ParserConfigurationException {
        DataSet res = parse("S124-test-dummy-2.xml");

        ObjectMapper mapper = new ObjectMapper();

        String actual = mapper.writer().writeValueAsString(res);
        System.out.println(actual);
        assertThat(actual, isA(String.class));
    }

    private DataSet parse(String fileName) throws ParserConfigurationException, SAXException, IOException {
        InputStream testDoc = getClass().getClassLoader().getResourceAsStream(fileName);
        return (DataSet) parser.parse(testDoc);
    }

    @Test
    public void name() {
        double a = 2;
        double b = 1.2;
        int points = 10;
        double step = 2*a/points;

        for (int i = 0; i <= points; i++) {
            double x = -a + i * step;
            double yPos = b/a * Math.sqrt(a*a - x*x);
            double yNeg = -b/a * Math.sqrt(a*a - x*x);
            System.out.printf(Locale.ENGLISH, "%5.2f, %5.2f\n", x, yPos);
        }

    }
}
