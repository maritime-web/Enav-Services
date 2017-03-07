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
package dk.dma.embryo.user.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Steen on 05-04-2016.
 *
 */
public class KnownRolesTest {
    @Test
    public void shouldKnowSailor() throws Exception {
        assertThat(KnownRoles.isKnown("Sailor"), is(true));
    }

    @Test
    public void shouldKnowShore() throws Exception {
        assertThat(KnownRoles.isKnown("Shore"), is(true));
    }

    @Test
    public void shouldKnowReporting() throws Exception {
        assertThat(KnownRoles.isKnown("Reporting"), is(true));
    }

    @Test
    public void shouldKnowAdministration() throws Exception {
        assertThat(KnownRoles.isKnown("Administration"), is(true));
    }

    @Test
    public void shouldNotKnowCaptain() throws Exception {
        assertThat(KnownRoles.isKnown("Captain"), is(false));
    }
}
