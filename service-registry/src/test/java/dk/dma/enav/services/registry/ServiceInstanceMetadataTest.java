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

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by Steen on 24-05-2016.
 *
 */
public class ServiceInstanceMetadataTest {

    @Test
    public void shouldEnsureThatWarningsListIsInitializedOnCreationWithEmptyConstructor() throws Exception {
        ServiceInstanceMetadata cut = new ServiceInstanceMetadata();

        assertThat(cut.getWarnings(), notNullValue());
    }

    @Test
    public void shouldEnsureThatWarningsListIsInitializedOnCreationWithNormalConstructor() throws Exception {
        ServiceInstanceMetadata cut = new ServiceInstanceMetadata(null, null, null, null, null);

        assertThat(cut.getWarnings(), notNullValue());
    }

    @Test
    public void shouldEnsureThatWarningsListIsInitializedOnCreationWithErrorConstructor() throws Exception {
        ServiceInstanceMetadata cut = new ServiceInstanceMetadata(null, null);

        assertThat(cut.getWarnings(), notNullValue());
    }

    @Test
    public void shouldEnsureThatWarningsListIsNotNulledByCallingSetWarnings() throws Exception {
        ServiceInstanceMetadata cut = new ServiceInstanceMetadata();

        cut.setWarnings(null);

        assertThat(cut.getWarnings(), notNullValue());
    }

}
