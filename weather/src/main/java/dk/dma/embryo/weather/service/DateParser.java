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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParser {
    private static final Locale DEFAULT_LOCALE = new Locale("en", "UK");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd MMMM YYYY").withZone(DateTimeZone.UTC)
            .withLocale(DEFAULT_LOCALE);
    private Pattern DATE_MONTH_YEAR = Pattern.compile("(?<date>\\d{1,2})\\.\\s(?<month>\\p{Alpha}{4,9})\\s(?<year>\\d{4})");
    private Pattern TIME = Pattern.compile("(?<hours>\\d{2})[.:](?<minutes>\\d{2}) UTC");
    private static final Pattern TO_DATE_MONTH_HOUR = Pattern.compile("(?<date>\\d{1,2})\\.\\s(?<month>\\p{Alpha}{4,9})(,)?\\s(?<hour>\\d{1,2})(\\s)?(UTC|utc)");
    private static final Pattern TO_FROM_TIME = Pattern.compile("Issued at (?<hours>\\d{2})\\.(?<minutes>\\d{2}) UTC");

    private String fromText;
    private String toText;
    private DateTime from;
    private DateTime to;

    DateParser(String fromText, String toText) {
        this.fromText = fromText;
        this.toText = toText;
        parse();
    }

    private void parse() {
        boolean fromHoursFound = false;
        Matcher dateMonthYearMatcher = DATE_MONTH_YEAR.matcher(fromText);
        if (!dateMonthYearMatcher.find()) {
            throw new IllegalArgumentException("Couldn't parse date from '"+fromText+"'");
        }
        String dateMonthYearString = dateMonthYearMatcher.group("date") + " " + dateMonthYearMatcher.group("month") + " " + dateMonthYearMatcher.group("year");
        from = DATE_FORMATTER.parseDateTime(dateMonthYearString);

        Matcher timeMatcher = TIME.matcher(fromText);
        if (timeMatcher.find()) {
            from = from.withHourOfDay(Integer.parseInt(timeMatcher.group("hours")));
            from = from.withMinuteOfHour(Integer.parseInt(timeMatcher.group("minutes")));
            fromHoursFound = true;
        }

        Matcher dateMonthMatcher = TO_DATE_MONTH_HOUR.matcher(toText);
        if (!dateMonthMatcher.find()) {
            throw new IllegalArgumentException("Couldn't parse date from '"+toText+"'");
        }

        String toDateMonthYearString = dateMonthMatcher.group("date") + " " + dateMonthMatcher.group("month") + " " + from.getYear();
        to = DATE_FORMATTER.parseDateTime(toDateMonthYearString);
        to = to.withHourOfDay(Integer.parseInt(dateMonthMatcher.group("hour")));

        Matcher toFromTimeMatcher = TO_FROM_TIME.matcher(toText);
        if (toFromTimeMatcher.find()) {
            from = from.withHourOfDay(Integer.parseInt(toFromTimeMatcher.group("hours")));
            from = from.withMinuteOfHour(Integer.parseInt(toFromTimeMatcher.group("minutes")));
            fromHoursFound = true;
        }

        if (!fromHoursFound) {
            throw new IllegalArgumentException("Couldn't find issued time in either texts {'"+fromText+"', '"+toText+"'}");
        }

        // Year change
        if (from.isAfter(to)) {
            to = to.plusYears(1);
        }
    }

    public DateTime getFrom() {
        return from;
    }

    public DateTime getTo() {
        return to;
    }
}
