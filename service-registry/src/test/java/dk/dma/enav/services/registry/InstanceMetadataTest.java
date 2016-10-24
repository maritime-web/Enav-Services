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

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dma.enav.services.registry.api.Error;
import dk.dma.enav.services.registry.api.ErrorId;
import dk.dma.enav.services.registry.api.ErrorType;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.TechnicalDesignId;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.notNullValue;

/**
 *
 */
public class InstanceMetadataTest {

    @Test
    public void shouldEnsureThatWarningsListIsInitializedOnCreationWithNormalConstructor() throws Exception {
        InstanceMetadata cut = new InstanceMetadata("some id");

        assertThat(cut.getWarnings(), notNullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldEnsureThatAddedWarningCanNotBeNull() throws Exception {
        InstanceMetadata cut = new InstanceMetadata("Some id");

        cut.addWarning(null);
    }

    @Test
    public void shouldIgnoreDuplicateErrors() throws Exception {
        InstanceMetadata cut = new InstanceMetadata("some id");
        Error e = new Error(ErrorId.MISSING_URL, ErrorType.INVALID_DATA, "message");

        cut.addError(e).addError(e);
        assertThat(cut.getErrors(), iterableWithSize(1));
    }

    @Test
    public void shouldBeAbleToSerializeToJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String url = "http://test.dk";
        String boundary = "POLYGON((-180 -90, 180 -90, 180 90, -180 90, -180 -90))";
        String name = "test";
        String designId = "designtest";
        String version = "1";
        InstanceMetadata inst = new InstanceMetadata("aa")
                .withUrl(url)
                .withBoundary(boundary)
                .withInstanceName(name)
                .withTechnicalDesignId(new TechnicalDesignId(designId, version))
                .addError(new Error(ErrorId.MISSING_URL, ErrorType.INVALID_DATA, ""));
        String json = mapper.writeValueAsString(inst);
        assertThat(json, allOf(containsString(url), containsString(boundary), containsString(name), containsString(designId), containsString(version)));
    }

    @Test
    public void shouldSerializeToJsonWithClassAttributeNames() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        InstanceMetadata inst = new InstanceMetadata("aa");
        String json = mapper.writeValueAsString(inst);
        //noinspection unchecked
        assertThat(json, allOf(containsString("instanceId\":"), containsString("name\":"), containsString("technicalDesignId\":"), containsString("boundary\":"), containsString("url\":"), containsString("errors\":[]"), containsString("warnings\":[]")));
    }
}
