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

import dk.dma.enav.services.s124.views.DataSet;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class DataSetXmlParserTest {

    @Test
    public void shouldParse() throws IOException {
        InputStream testDoc = getClass().getClassLoader().getResourceAsStream("S124-test-dummy.xml");
        InputStreamReader reader = new InputStreamReader(testDoc, StandardCharsets.UTF_8);

        DataSetXmlParser cut = new DataSetXmlParser();
        DataSet res = cut.parseDataSetXml(IOUtils.toString(reader));

        assertThat(res, not(nullValue(DataSet.class)));
    }
}
