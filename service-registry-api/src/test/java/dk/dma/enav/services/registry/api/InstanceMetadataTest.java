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
package dk.dma.enav.services.registry.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.notNullValue;

/**
 *
 */
public class InstanceMetadataTest {

    @Test
    public void shouldEnsureThatWarningsListIsInitializedOnCreationWithNormalConstructor() throws Exception {
        InstanceMetadata cut = new InstanceMetadata("some id", "0.1", 1);

        assertThat(cut.getWarnings(), notNullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldEnsureThatAddedWarningCanNotBeNull() throws Exception {
        InstanceMetadata cut = new InstanceMetadata("Some id", "0.1", 1);

        cut.addWarning(null);
    }

    @Test
    public void shouldIgnoreDuplicateErrors() throws Exception {
        InstanceMetadata cut = new InstanceMetadata("some id", "0.1", 1);
        Error e = new Error(ErrorId.MISSING_URL, ErrorType.INVALID_DATA, "message");

        cut.addError(e).addError(e);
        assertThat(cut.getErrors(), iterableWithSize(1));
    }

    @Test
    public void shouldReportMissingDataAsErrorsOnValidation() throws Exception {
        InstanceMetadata cut = new InstanceMetadata("some id", "0.1", 1);

        List<Error> validationErrors = cut.validate();

        assertThat(validationErrors, containsInAnyOrder(
                hasProperty("id", equalTo(ErrorId.MISSING_AVAILABILITY)),
                hasProperty("id", equalTo(ErrorId.MISSING_BOUNDARY)),
                hasProperty("id", equalTo(ErrorId.MISSING_DESCRIPTION)),
                hasProperty("id", equalTo(ErrorId.MISSING_DESIGN_ID)),
                hasProperty("id", equalTo(ErrorId.MISSING_INSTANCE_NAME)),
                hasProperty("id", equalTo(ErrorId.MISSING_PRODUCED_BY)),
                hasProperty("id", equalTo(ErrorId.MISSING_PROVIDED_BY)),
                hasProperty("id", equalTo(ErrorId.MISSING_STATUS)),
                hasProperty("id", equalTo(ErrorId.MISSING_URL))
        ));
    }

    @Test
    public void shouldBeAbleToSerializeToJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String url = "http://test.dk";
        String boundary = "POLYGON((-180 -90, 180 -90, 180 90, -180 90, -180 -90))";
        String name = "test";
        String designId = "designtest";
        String version = "1";
        InstanceMetadata inst = new InstanceMetadata("aa", "0.1", 1)
                .setUrl(url)
                .setBoundary(boundary)
                .setName(name)
                .setAvailability(0.1f)
                .setDescription("tada")
                .setStatus("Unknown")
                .setProducedBy(new VendorInfo("mrn:ssss:errr"))
                .setProvidedBy(new VendorInfo("mrn:ssss:xxx").setCommercial(true).setName("MA"))
                .setTechnicalDesignId(new TechnicalDesignId(designId, version))
                .addError(new Error(ErrorId.MISSING_URL, ErrorType.INVALID_DATA, ""));
        String json = mapper.writeValueAsString(inst);
        //noinspection unchecked
        assertThat(json, allOf(
                containsString(url),
                containsString(boundary),
                containsString(name),
                containsString(designId),
                containsString("mrn:ssss:errr"),
                containsString("mrn:ssss:xxx"),
                containsString("MA"),
                containsString(version)));
    }

    @Test
    public void shouldSerializeToJsonWithClassAttributeNames() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        InstanceMetadata inst = new InstanceMetadata("aa", "0.1", 1);
        String json = mapper.writeValueAsString(inst);
        //noinspection unchecked
        assertThat(json, allOf(
                containsString("instanceId\":"),
                containsString("name\":"),
                containsString("technicalDesignId\":"),
                containsString("boundary\":"),
                containsString("url\":"),
                containsString("producedBy\":"),
                containsString("providedBy\":"),
                containsString("errors\":[]"),
                containsString("warnings\":[]")));
    }
}
