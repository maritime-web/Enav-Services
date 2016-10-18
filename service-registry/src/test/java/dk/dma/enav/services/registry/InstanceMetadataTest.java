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

import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.TechnicalDesignId;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by Steen on 24-05-2016.
 *
 */
public class InstanceMetadataTest {

    @Test
    public void shouldEnsureThatWarningsListIsInitializedOnCreationWithEmptyConstructor() throws Exception {
        InstanceMetadata cut = new InstanceMetadata();

        assertThat(cut.getWarnings(), notNullValue());
    }

    @Test
    public void shouldEnsureThatWarningsListIsInitializedOnCreationWithNormalConstructor() throws Exception {
        InstanceMetadata cut = new InstanceMetadata(null, null, null, null, null);

        assertThat(cut.getWarnings(), notNullValue());
    }

    @Test
    public void shouldEnsureThatWarningsListIsInitializedOnCreationWithErrorConstructor() throws Exception {
        InstanceMetadata cut = new InstanceMetadata(null, null);

        assertThat(cut.getWarnings(), notNullValue());
    }

    @Test
    public void shouldEnsureThatWarningsListIsNotNulledByCallingSetWarnings() throws Exception {
        InstanceMetadata cut = new InstanceMetadata();

        cut.setWarnings(null);

        assertThat(cut.getWarnings(), notNullValue());
    }

}
