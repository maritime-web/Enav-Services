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
package dk.dma.embryo.vessel.component;

import dk.dma.enav.model.voyage.RouteLeg;
import dk.dma.enav.model.voyage.Waypoint;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;


public class RouteLegDecoratorTest {
    @Test
    public void shouldCalculateLegLengthToBeTenNauticalMiles() throws Exception {
        RouteLegDecorator cut = new RouteLegDecorator(new RouteLeg(10.0, RouteLeg.Heading.RL, 0.0, 0.0), new WaypointDecorator(new Waypoint("from", 64.110604, -52.4, 0.0, 0.0)));
        cut.setTo(new WaypointDecorator(new Waypoint("to", 64.2772, -52.40, 0.0, 0.0)));

        assertThat(cut.calcRange(), is(closeTo(10D, 0.01)));
    }

    @Test
    public void shouldCalculateDurationToSailTheLegToBeOneHour() throws Exception {
        RouteLegDecorator cut = new RouteLegDecorator(new RouteLeg(10.0, RouteLeg.Heading.RL, 0.0, 0.0), new WaypointDecorator(new Waypoint("from", 64.110604, -52.4, 0.0, 0.0)));
        cut.setTo(new WaypointDecorator(new Waypoint("to", 64.2772, -52.40, 0.0, 0.0)));

        assertThat((double)cut.calcTtg(), is(closeTo(3600000D, 1000D)));
    }

}
