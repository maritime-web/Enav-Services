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
package dk.dma.enav.services.registry.lost;

import dk.dma.enav.services.registry.Errors;
import ietf.lost1.BasicException;
import ietf.lost1.ExceptionContainer;
import ietf.lost1.LocationProfileUnrecognized;
import ietf.lost1.ObjectFactory;
import ietf.lost1.Redirect;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

/**
 * Created by Steen on 23-05-2016.
 *
 */
public class ErrorConverterTest {
    private ErrorConverter cut;

    @Before
    public void setUp() throws Exception {
        cut = new ErrorConverter();
    }

    @Test
    public void shouldCreateErrorFromErrorsObject() throws Exception {
        String sourceId = "someSourceId";
        String message = "Bad request";
        JAXBElement errors = createBasicExceptionInContainer(sourceId, message);

        Errors e = cut.convert(errors);

        assertThat(e.getServiceRegistryId(), equalTo(sourceId));
        assertThat(e.getDescriptions(), hasItem(hasProperty("message", equalTo(message))));
    }

    @Test
    public void shouldCreateErrorWithDetailFromErrorsObjectContainingLocationProfileUnrecognized() throws Exception {
        String sourceId = "someSourceId";
        String message = "Unrecognized profile";

        Errors e = cut.convert(wrapInErrorContainer(sourceId, createLocationProfileUnrecognized(message, "wkt", "geojson")));

        assertThat(e.getDescriptions(), hasItem(hasProperty("message", equalTo(message))));
        assertThat(e.getDescriptions(), hasItem(hasProperty("details", equalTo("wkt, geojson"))));
    }

    @Test
    public void shouldCreateErrorWithDetailFromLocationProfileUnrecognizedObject() throws Exception {
        String message = "Unrecognized profile";

        Errors e = cut.convert(createLocationProfileUnrecognized(message, "wkt", "geojson"));

        assertThat(e.getDescriptions(), hasItem(hasProperty("message", equalTo(message))));
        assertThat(e.getDescriptions(), hasItem(hasProperty("details", equalTo("wkt, geojson"))));
    }

    @Test
    public void shouldCreateMultipleErrorsFromMultipleErrorContainers() throws Exception {
        String sourceOne = "SourceOne";
        String sourceTwo = "SourceTwo";
        String messageOne = "Bad request";
        String messageTwo = "Unrecognized profile";

        List<ExceptionContainer> exceptionContainers = new ArrayList<>();
        exceptionContainers.add(wrapInExceptionContainer(sourceOne, createBasicException(messageOne)));
        exceptionContainers.add(wrapInExceptionContainer(sourceTwo, createLocationProfileUnrecognized(messageTwo, "wkt", "geojson")));

        List<Errors> e = cut.convert(exceptionContainers);

        assertThat(e, hasItems(hasProperty("serviceRegistryId", equalTo(sourceOne)), hasProperty("serviceRegistryId", equalTo(sourceTwo))));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenEncounterUnknownElement() throws Exception {
        cut.convert(new Redirect());
    }

    private JAXBElement createBasicExceptionInContainer(String sourceId, String message) {
        return wrapInErrorContainer(sourceId, createBasicException(message));
    }

    private JAXBElement createBasicException(String message) {
        ObjectFactory factory = new ObjectFactory();
        BasicException basicException = new BasicException();
        basicException.setMessage(message);
        return factory.createBadRequest(basicException);
    }

    private LocationProfileUnrecognized createLocationProfileUnrecognized(String message, String... unrecognizedProfile) {
        ObjectFactory factory = new ObjectFactory();
        LocationProfileUnrecognized res = factory.createLocationProfileUnrecognized();
        res.setMessage(message);

        Arrays.spliterator(unrecognizedProfile).forEachRemaining(s -> res.getUnsupportedProfiles().add(s));
        return res;
    }

    private JAXBElement wrapInErrorContainer(String sourceId, Object basicException) {
        ObjectFactory factory = new ObjectFactory();
        ExceptionContainer container = new ExceptionContainer();
        container.setSource(sourceId);
        container.getBadRequestOrInternalErrorOrServiceSubstitution().add(basicException);
        return factory.createErrors(container);
    }

    private ExceptionContainer wrapInExceptionContainer(String sourceId, Object basicException) {
        ExceptionContainer container = new ExceptionContainer();
        container.setSource(sourceId);
        container.getBadRequestOrInternalErrorOrServiceSubstitution().add(basicException);
        return container;
    }
}
