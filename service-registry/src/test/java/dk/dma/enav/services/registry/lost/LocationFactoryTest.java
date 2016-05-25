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

import ietf.lost1.Location;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

/**
 * Created by Steen on 25-05-2016.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LocationFactoryTest {
    @Mock(answer = Answers.RETURNS_MOCKS)
    private Geodetic2DFactory geodetic2DFactory;
    @InjectMocks
    private LocationFactory cut;

    @Test
    public void shouldUseGeodetic2DAsLocationProfile() throws Exception {
        Location location = cut.createLocation(11.0001234, 22.0005678);

        assertThat(location, hasProperty("profile", equalTo("geodetic-2d")));
    }
}
