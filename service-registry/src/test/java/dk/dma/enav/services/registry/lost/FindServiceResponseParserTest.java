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

import dk.dma.enav.services.registry.ErrorDescription;
import dk.dma.enav.services.registry.Errors;
import dk.dma.enav.services.registry.ServiceInstanceMetadata;
import ietf.lost1.BasicException;
import ietf.lost1.DisplayName;
import ietf.lost1.ExceptionContainer;
import ietf.lost1.FindServiceResponse;
import ietf.lost1.LocationInformation;
import ietf.lost1.Mapping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Element;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Steen on 03-05-2016.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class FindServiceResponseParserTest {
    @Mock
    private LostUnmarshalAdapter lostUnmarshalAdapter;
    @Mock
    private DomUtil domUtil;
    @Mock
    private ErrorConverter errorConverter;

    @InjectMocks
    private FindServiceResponseParser cut;

    @Test
    public void shouldCreateEmptyResponseIfNoValidMappingIsFound() throws Exception {
        ServiceInstanceMetadata expectedResult = new ServiceInstanceMetadata();
        String response = "<response></response>";
        when(lostUnmarshalAdapter.unmarshal(response, FindServiceResponse.class)).thenReturn(createFindServiceResponseWithNoValidMapping());

        ServiceInstanceMetadata result = cut.parseFindServiceResponse(response);

        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test
    public void shouldIncludewarningsInResponseIfNoValidMappingIsFound() throws Exception {
        String message = "message";
        ExceptionContainer warning = createWarning("id", message);
        ServiceInstanceMetadata expectedResult = new ServiceInstanceMetadata();
        expectedResult.setWarnings(singletonList(new Errors(warning.getSource(), singletonList(new ErrorDescription(message, null)))));
        String response = "<response></response>";
        FindServiceResponse serviceResponseWithNoValidMapping = createFindServiceResponseWithNoValidMapping(warning);
        when(lostUnmarshalAdapter.unmarshal(response, FindServiceResponse.class)).thenReturn(serviceResponseWithNoValidMapping);
        when(errorConverter.convert(serviceResponseWithNoValidMapping.getWarnings())).thenReturn(expectedResult.getWarnings());

        ServiceInstanceMetadata result = cut.parseFindServiceResponse(response);

        assertThat(result, equalTo(expectedResult));
    }

    @Test
    public void shouldOnlyIncludeEnglishDisplayName() throws Exception {
        String response = "<response></response>";
        FindServiceResponse serviceResponseWithValidMapping = findServiceResponseWithValidMapping();
        serviceResponseWithValidMapping.getMapping().get(0).getDisplayName().add(createDisplayName("fr", "service utile"));
        serviceResponseWithValidMapping.getMapping().get(0).getDisplayName().add(createDisplayName("en", "useful service"));
        when(lostUnmarshalAdapter.unmarshal(response, FindServiceResponse.class)).thenReturn(serviceResponseWithValidMapping);

        ServiceInstanceMetadata result = cut.parseFindServiceResponse(response);

        assertThat(result.getName(), is("useful service"));
    }

    @Test
    public void shouldIncludeUrl() throws Exception {
        String response = "<response></response>";
        FindServiceResponse serviceResponseWithValidMapping = findServiceResponseWithValidMapping();
        when(lostUnmarshalAdapter.unmarshal(response, FindServiceResponse.class)).thenReturn(serviceResponseWithValidMapping);

        ServiceInstanceMetadata result = cut.parseFindServiceResponse(response);

        assertThat(result.getUrl(), not(isEmptyOrNullString()));
    }

    @Test
    public void shouldIncludeServiceId() throws Exception {
        String response = "<response></response>";
        FindServiceResponse serviceResponseWithValidMapping = findServiceResponseWithValidMapping();
        when(lostUnmarshalAdapter.unmarshal(response, FindServiceResponse.class)).thenReturn(serviceResponseWithValidMapping);

        ServiceInstanceMetadata result = cut.parseFindServiceResponse(response);

        assertThat(result.getServiceId(), not(isEmptyOrNullString()));
    }

    @Test
    public void shouldIncludeInstanceId() throws Exception {
        String response = "<response></response>";
        FindServiceResponse serviceResponseWithValidMapping = findServiceResponseWithValidMapping();
        when(lostUnmarshalAdapter.unmarshal(response, FindServiceResponse.class)).thenReturn(serviceResponseWithValidMapping);

        ServiceInstanceMetadata result = cut.parseFindServiceResponse(response);

        assertThat(result.getInstanceId(), not(isEmptyOrNullString()));
    }

    @Test
    public void shouldIncludeBoundary() throws Exception {
        String response = "<response></response>";
        FindServiceResponse serviceResponseWithValidMapping = findServiceResponseWithValidMapping();
        when(lostUnmarshalAdapter.unmarshal(response, FindServiceResponse.class)).thenReturn(serviceResponseWithValidMapping);
        when(domUtil.toString(any(Element.class))).thenReturn("<exiting><boundary></boundary></exiting>");

        ServiceInstanceMetadata result = cut.parseFindServiceResponse(response);

        assertThat(result.getBoundary(), not(isEmptyOrNullString()));
    }

    private DisplayName createDisplayName(String lang, String value) {
        DisplayName res = new DisplayName();
        res.setLang(lang);
        res.setValue(value);
        return res;
    }

    private FindServiceResponse findServiceResponseWithValidMapping() {
        FindServiceResponse res = new FindServiceResponse();
        Mapping m = new Mapping();
        m.getUri().add("ftp://useful.service.com");
        m.setService("urn:bla:bla:bla");
        LocationInformation loc = new LocationInformation();
        loc.getExtensionPoint().add(mock(Element.class));
        m.getServiceBoundary().add(loc);
        res.getMapping().add(m);

        return res;
    }

    private FindServiceResponse createFindServiceResponseWithNoValidMapping() {
        return new FindServiceResponse();
    }

    private FindServiceResponse createFindServiceResponseWithNoValidMapping(ExceptionContainer warning) {
        FindServiceResponse res = createFindServiceResponseWithNoValidMapping();
        res.getWarnings().add(warning);
        return res;
    }

    private ExceptionContainer createWarning(String id, String message) {
        ExceptionContainer res = new ExceptionContainer();
        res.setSource(id);
        BasicException w = new BasicException();
        w.setMessage(message);
        res.getBadRequestOrInternalErrorOrServiceSubstitution().add(w);
        return res;
    }

}
