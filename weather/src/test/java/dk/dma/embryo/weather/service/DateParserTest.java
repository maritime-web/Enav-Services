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
package dk.dma.embryo.weather.service;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class DateParserTest {
    private String defaultToText = "Forecast, valid to Tuesday the 8. July, 18 UTC.";
    @Test
    public void shouldParseFromWhenDateAndTimeIsInFromText() throws Exception {
        DateTime expectedFrom = new DateTime(2014, 7, 7, 17, 45, DateTimeZone.UTC);
        String fromText = "Monday the 7. July 2014, 17.45 UTC. ";

        DateParser parser = new DateParser(fromText, defaultToText);

        assertThat(parser.getFrom(), equalTo(expectedFrom));
    }

    @Test
    public void shouldParseFromWhithDeviatingTimeFormat() throws Exception {
        DateTime expectedFrom = new DateTime(2014, 8, 3, 12, 20, DateTimeZone.UTC);
        String fromText = "Sunday the 3. August 2014, 12:20 UTC..";

        DateParser parser = new DateParser(fromText, defaultToText);

        assertThat(parser.getFrom(), equalTo(expectedFrom));
    }

    @Test
    public void shouldParseFromWhenDateIsInFromTextAndTimeIsInToText() throws Exception {
        DateTime expectedFrom = new DateTime(2017, 8, 7, 11, 35, DateTimeZone.UTC);
        String fromText = "Monday the 7. August 2017.";
        String toText = "Forecast, valid to the 8. August 12 UTC. Issued at 11.35 UTC.";

        DateParser parser = new DateParser(fromText, toText);

        assertThat(parser.getFrom(), equalTo(expectedFrom));
    }

    @Test
    public void shouldParseFromWhenhoursIsSingleDigit() throws Exception {
        DateTime expectedFrom = new DateTime(2017, 8, 11, 6, 45, DateTimeZone.UTC);
        String fromText = "Friday the 11. August 2017.";
        String toText = "Forecast, valid to the 12. August 06 UTC.. Issued at 6.45 UTC..\n";

        DateParser parser = new DateParser(fromText, toText);

        assertThat(parser.getFrom(), equalTo(expectedFrom));
    }

    @Test
    public void shouldParseToWhithExtraDotsAfterUTC() throws Exception {
        DateTime expectedTo = new DateTime(2017, 8, 9, 0, 0, DateTimeZone.UTC);
        String fromText = "Monday the 7. August 2017.";
        String toText = "Forecast, valid to the 9. August 00 UTC.. Issued at 21.45 UTC..";

        DateParser parser = new DateParser(fromText, toText);

        assertThat(parser.getTo(), equalTo(expectedTo));
    }

    @Test
    public void shouldParseToWhithTimeAndUtc() throws Exception {
        DateTime expectedTo = new DateTime(2014, 12, 10, 6, 0, DateTimeZone.UTC);
        String fromText = "Monday the 7. July 2014, 17.45 UTC.";
        String toText = "Forecast, valid to Wednesday the 10. December, 06utc.";

        DateParser parser = new DateParser(fromText, toText);

        assertThat(parser.getTo(), equalTo(expectedTo));
    }

    @Test
    public void shouldHandleYearChange() throws Exception {
        DateTime expectedTo = new DateTime(2014, 1, 1, 6, 0, DateTimeZone.UTC);
        String fromText = "Tuesday the 31. December 2013, 07.00 UTC. ";
        String toText = "Forecast, valid to Wednesday the 1. January, 06 UTC.";

        DateParser parser = new DateParser(fromText, toText);

        assertThat(parser.getTo(), equalTo(expectedTo));
    }
}
