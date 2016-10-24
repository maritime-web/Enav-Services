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
package dk.dma.enav.services.registry.mc;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class InstanceDetailsTest {

    @Test
    public void shouldReturnDefaultCoverageIfNoneIsSet() throws Exception {
        InstanceDetails cut = new InstanceDetails();

        assertThat(cut.getCoverage(), is(equalTo(InstanceDetails.DEFAULT_COVERAGE)));
    }

    @Test
    public void shouldReturnDefaultCoverageIfNullIsSet() throws Exception {
        InstanceDetails cut = new InstanceDetails();

        cut.withCoverage(null);

        assertThat(cut.getCoverage(), is(equalTo(InstanceDetails.DEFAULT_COVERAGE)));
    }
}
