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

import dk.dma.embryo.common.log.EmbryoLogService;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.VendorInfo;
import dk.dma.enav.services.s124.views.DataSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@RunWith(MockitoJUnitRunner.class)
public class S124MessageLoaderTaskTest {
    @Mock
    private EmbryoLogService logService;

    @Test
    public void messageLoaderTest() throws Exception {
        String url = "https://niord.e-navigation.net/rest";
        ApiClientFactory factory = new ApiClientFactory();
        S124MessageLoaderTask.S124MessageLoaderTaskBuilder builder;
        DataSetXmlParser parser = new DataSetXmlParser();
        builder = new S124MessageLoaderTask.S124MessageLoaderTaskBuilder(logService, factory, parser);

        InstanceMetadata instance = new InstanceMetadata("S-124", "0.1", 1L);
        instance.setDescription("S-124 DMA service")
                .setName("S-124 Service Endpoint")
                .setProducedBy(new VendorInfo("DMA"))
                .setProvidedBy(new VendorInfo("DMA"))
                .setUrl(url);

        S124MessageLoaderTask task = builder
                .serviceInstance(instance)
                .wkt("POLYGON((10.0000 55.1000, 10.0000 56.8000, 10.8000 56.8000, 10.8000 55.1000, 10.0000 55.1000))")
                .build();

        List<DataSet> result = task.call();

        assertThat(result, not(nullValue()));
    }
}
