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
package dk.dma.embryo.metoc.json;

import dk.dma.embryo.metoc.json.client.DmiSejlRuteService;
import dk.dma.embryo.metoc.json.client.DmiSejlRuteService.MetocForecast;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
@EqualsAndHashCode
public class Metoc {

    private String created;
    private List<Forecast> forecasts;

    // //////////////////////////////////////////////////////////////////////
    // Utility methods
    // //////////////////////////////////////////////////////////////////////
    public static Metoc from(MetocForecast metocForecast) {
        Metoc result = new Metoc();

        try {
            List<Forecast> forecasts = new ArrayList<>(metocForecast.getForecasts().length);
            for (DmiSejlRuteService.Forecast dmiForecast : metocForecast.getForecasts()) {
                forecasts.add(Forecast.from(dmiForecast));
            }
            result.setCreated(metocForecast.getCreated());
            result.setForecasts(forecasts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public static Metoc from(MetocForecast metocForecast, Predicate<DmiSejlRuteService.Forecast> predicate) {
        Metoc result = new Metoc();

        try {
            List<Forecast> forecasts = new ArrayList<>(metocForecast.getForecasts().length);
            for (DmiSejlRuteService.Forecast dmiForecast : metocForecast.getForecasts()) {
                if (predicate.test(dmiForecast)) {
                    forecasts.add(Forecast.from(dmiForecast));
                }
            }
            result.setCreated(metocForecast.getCreated());
            result.setForecasts(forecasts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    // //////////////////////////////////////////////////////////////////////
    // Property methods
    // //////////////////////////////////////////////////////////////////////
    public String getCreated() {
        return created;
    }
    public void setCreated(String created) {
        this.created = created;
    }

    public List<Forecast> getForecasts() {
        return forecasts;
    }
    public void setForecasts(List<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    // //////////////////////////////////////////////////////////////////////
    // Inner classes
    // //////////////////////////////////////////////////////////////////////
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Forecast {
        private double lat;
        private double lon;
        private Date time;
        private Double windDir;
        private Double windSpeed;
        private Double curDir;
        private Double curSpeed;
        private Double waveDir;
        private Double waveHeight;
        private Double wavePeriod;
        private Double seaLevel;

        public static Forecast from(DmiSejlRuteService.Forecast dmiForecast) throws ParseException {
            Forecast forecast = new Forecast();
            forecast.setLat(dmiForecast.getLat());
            forecast.setLon(dmiForecast.getLon());
            forecast.setTime(DmiSejlRuteService.DATE_FORMAT.parse(dmiForecast.getTime()));
            forecast.setWindDir(dmiForecast.getWindDir() == null ? null : ceil(dmiForecast.getWindDir().getForecast(), 0));
            forecast.setWindSpeed(dmiForecast.getWindSpeed() == null ? null : ceil(dmiForecast.getWindSpeed().getForecast(), 1));
            forecast.setCurDir(dmiForecast.getCurrentDir() == null ? null : ceil(dmiForecast.getCurrentDir().getForecast(), 0));
            forecast.setCurSpeed(dmiForecast.getCurrentSpeed() == null ? null : ceil(dmiForecast.getCurrentSpeed().getForecast(), 1));
            forecast.setWaveDir(dmiForecast.getWaveDir() == null ? null : ceil(dmiForecast.getWaveDir().getForecast(), 0));
            forecast.setWaveHeight(dmiForecast.getWaveHeight() == null ? null : ceil(dmiForecast.getWaveHeight().getForecast(), 1));
            forecast.setWavePeriod(dmiForecast.getWavePeriod() == null ? null : ceil(dmiForecast.getWavePeriod().getForecast(), 2));
            forecast.setSeaLevel(dmiForecast.getSealevel() == null ? null : ceil(dmiForecast.getSealevel().getForecast(), 1));
            return forecast;
        }

        /**
         * tries to round a double to a give number of decimals
         * todo this does not always work, because rounding is always involved when one of the participants is a double, BigDecimal should be used instead
         */
        private static double ceil(double number, int decimals) {
            double d = Math.pow(10.0, decimals);
            return Math.ceil(number * d) / d;
        }
    }
}
