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
package dk.dma.enav.services.nwnm;

import dk.dma.embryo.common.log.EmbryoLogService;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageLoaderTaskTest {
    private static String message = "[  {\n" +
            "    \"id\": \"b743abd9-7c70-414c-ae4a-eb24647ceb9a\",\n" +
            "    \"created\": 1494412189000,\n" +
            "    \"updated\": 1494412227000,\n" +
            "    \"messageSeries\": {\n" +
            "      \"seriesId\": \"dma-nw-local\"\n" +
            "    },\n" +
            "    \"mainType\": \"NW\",\n" +
            "    \"type\": \"LOCAL_WARNING\",\n" +
            "    \"status\": \"PUBLISHED\",\n" +
            "    \"areas\": [\n" +
            "      {\n" +
            "        \"id\": 486,\n" +
            "        \"active\": true,\n" +
            "        \"parent\": {\n" +
            "          \"id\": 453,\n" +
            "          \"mrn\": \"urn:mrn:iho:country:dk\",\n" +
            "          \"active\": true,\n" +
            "          \"descs\": [\n" +
            "            {\n" +
            "              \"lang\": \"en\",\n" +
            "              \"name\": \"Denmark\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        \"descs\": [\n" +
            "          {\n" +
            "            \"lang\": \"en\",\n" +
            "            \"name\": \"The Waters South of Zealand\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"categories\": [\n" +
            "      {\n" +
            "        \"id\": 152504,\n" +
            "        \"active\": true,\n" +
            "        \"parent\": {\n" +
            "          \"id\": 152489,\n" +
            "          \"active\": true,\n" +
            "          \"descs\": [\n" +
            "            {\n" +
            "              \"lang\": \"en\",\n" +
            "              \"name\": \"Beacon\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        \"descs\": [\n" +
            "          {\n" +
            "            \"lang\": \"en\",\n" +
            "            \"name\": \"Beacon partly obscured\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"publishDateFrom\": 1494412227000,\n" +
            "    \"parts\": [\n" +
            "      {\n" +
            "        \"indexNo\": 0,\n" +
            "        \"type\": \"DETAILS\",\n" +
            "        \"eventDates\": [\n" +
            "          {\n" +
            "            \"allDay\": false,\n" +
            "            \"fromDate\": 1494412227000\n" +
            "          }\n" +
            "        ],\n" +
            "        \"geometry\": {\n" +
            "          \"type\": \"FeatureCollection\",\n" +
            "          \"type\": \"FeatureCollection\",\n" +
            "          \"id\": \"1622afac-944f-42c7-83b7-7ac441171852\",\n" +
            "          \"features\": [\n" +
            "            {\n" +
            "              \"type\": \"Feature\",\n" +
            "              \"type\": \"Feature\",\n" +
            "              \"id\": \"897090dc-355e-4353-9295-fe5991b781f8\",\n" +
            "              \"geometry\": {\n" +
            "                \"type\": \"Point\",\n" +
            "                \"type\": \"Point\",\n" +
            "                \"coordinates\": [\n" +
            "                  11.73842617950698,\n" +
            "                  54.87386033097894\n" +
            "                ]\n" +
            "              },\n" +
            "              \"properties\": {\n" +
            "                \"startCoordIndex\": 1\n" +
            "              }\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        \"descs\": [\n" +
            "          {\n" +
            "            \"lang\": \"en\",\n" +
            "            \"subject\": \"Beacon partly obscured\",\n" +
            "            \"details\": \"<p>The front and rear beacon F&aelig;rgemark in approx. pos. <span data-latitude=\\\"54.8739\\\">54&deg; 52.4'N</span> - <span data-longitude=\\\"11.7384\\\">011&deg; 44.3'E</span> is partly obscured.</p>\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"hideSubject\": true\n" +
            "      }\n" +
            "    ],\n" +
            "    \"descs\": [\n" +
            "      {\n" +
            "        \"lang\": \"en\",\n" +
            "        \"title\": \"Denmark. The Waters South of Zealand. Guldborg Sund. Beacon partly obscured.\",\n" +
            "        \"vicinity\": \"Guldborg Sund\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }]";
    @Mock
    private EmbryoLogService embryoLogService;
    @Mock
    private NwNmConnectionManager connectionManager;
    @Mock
    private InstanceMetadata serviceInstance;

    @InjectMocks
    private MessageLoaderTask cut;

    @Before
    public void setUp() throws Exception {
        when(serviceInstance.getUrl()).thenReturn("an url");
    }

    @Test
    public void shouldLogSuccessToLogService() throws Exception {
        when(connectionManager.getJson(anyString())).thenReturn(message);

        cut.call();

        verify(embryoLogService).info(anyString());
    }

    @Test
    public void shouldLogErrorToLogService() throws Exception {
        when(connectionManager.getJson(anyString())).thenReturn("Bad data");

        cut.call();

        verify(embryoLogService).error(anyString(), any(Exception.class));
    }
}
