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

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import ietf.lost1.FindServiceResponse;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by Steen on 18-05-2016.
 *
 */
public class LostUnmarshalAdapterTest {
    private LostUnmarshalAdapter cut;

    @Before
    public void setUp() throws Exception {
        cut = new LostUnmarshalAdapter();
    }

    @Test
    public void shouldUnmarshalToFindServiceResponse() throws Exception {
        URL url = Resources.getResource("responseExample.xml");
        String text = Resources.toString(url, Charsets.UTF_8);

        FindServiceResponse findServiceResponse = cut.unmarshal(text, FindServiceResponse.class);
        assertThat(findServiceResponse, is(not(nullValue())));
    }

    @Test(expected = LostErrorResponseException.class)
    public void shouldThrowExceptionWhenXmlStringRepresentsErrorContainer() throws Exception {
        URL url = Resources.getResource("badRequestInContainerError.xml");
        String text = Resources.toString(url, Charsets.UTF_8);

        cut.unmarshal(text, FindServiceResponse.class);
    }

    @Test(expected = LostErrorResponseException.class)
    public void shouldThrowExceptionWhenXmlStringRepresentsLocationprofileUnreqognizedError() throws Exception {
        URL url = Resources.getResource("locationprofileUnreqognizedResponse.xml");
        String text = Resources.toString(url, Charsets.UTF_8);

        cut.unmarshal(text, FindServiceResponse.class);
    }

    @Test(expected = LostErrorResponseException.class)
    public void shouldThrowExceptionWhenXmlStringRepresentsBasicError() throws Exception {
        URL url = Resources.getResource("locationInvalidError.xml");
        String text = Resources.toString(url, Charsets.UTF_8);

        cut.unmarshal(text, FindServiceResponse.class);
    }

    @Test(expected = LostErrorResponseException.class)
    public void shouldThrowExceptionWhenXmlStringRepresentsRedirect() throws Exception {
        URL url = Resources.getResource("redirectResponse.xml");
        String text = Resources.toString(url, Charsets.UTF_8);

        cut.unmarshal(text, FindServiceResponse.class);
    }

}
