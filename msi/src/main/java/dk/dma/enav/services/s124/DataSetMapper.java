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
package dk.dma.enav.services.s124;

import _int.iho.s124.gml.cs0._0.DatasetType;
import _int.iho.s124.gml.cs0._0.IMemberType;
import _int.iho.s124.gml.cs0._0.LocationNameType;
import _int.iho.s124.gml.cs0._0.NWPreambleType;
import _int.iho.s124.gml.cs0._0.PreambleType;
import _int.iho.s124.gml.cs0._0.TitleType;
import dk.dma.enav.services.s124.S124View.S124ViewBuilder;
import net.opengis.gml._3.AbstractFeatureMemberType;
import net.opengis.gml._3.BoundingShapeType;

import java.util.Optional;

public class DataSetMapper {
    public S124View toViewType(DatasetType dataset) {
        S124View.S124ViewBuilder builder = new S124ViewBuilder(dataset.getId());

        if (hasBoundedBy(dataset)) {
            BoundingShapeType boundedBy = dataset.getBoundedBy();
        }

        Optional<IMemberType> nwPreamble = getNwPreambleIMemeber(dataset);
        if (nwPreamble.isPresent()) {
            NWPreambleType preamble = (NWPreambleType) nwPreamble.get().getInformationType().getValue();
            builder
                    .id(getId(preamble))
                    .title(getTitle(preamble).getText())
                    .areaHeading(createAreaHeading(preamble))
                    .mainType("NW");

        }


        return builder.build();
    }

    private TitleType getTitle(PreambleType preamble) {
        Optional<TitleType> engTitle = preamble.getTitle().stream().filter(t -> t.getLanguage() == null || t.getLanguage().equals("eng")).findFirst();
        return engTitle.orElseGet(() -> preamble.getTitle().get(0));
    }

    private String getId(PreambleType preamble) {
        return preamble.getMessageSeriesIdentifier().getWarningIdentifier();
    }

    private String createAreaHeading(NWPreambleType preamble) {
        StringBuilder builder = new StringBuilder();
        preamble.getGeneralArea().forEach(ga -> {
            Optional<LocationNameType> eng = ga.getLocationName().stream().filter(loc -> loc.getLanguage() != null && loc.getLanguage().equals("eng")).findFirst();
            builder.append(eng.orElseGet(this::emptyLocationName).getText()).append(" - ");
        });

        preamble.getLocality().forEach(loc -> {
            Optional<LocationNameType> eng = loc.getLocationName().stream().filter(locName -> locName.getLanguage() != null && locName.getLanguage().equals("eng")).findFirst();
            builder.append(eng.orElseGet(this::emptyLocationName).getText()).append(" - ");
        });
        return builder.delete(builder.lastIndexOf(" - "), builder.length()).toString();
    }

    private LocationNameType emptyLocationName() {
        LocationNameType res = new LocationNameType();
        res.setText("");
        return res;
    }

    private boolean hasBoundedBy(DatasetType dataset) {
        return dataset.getBoundedBy() != null;
    }

    private Optional<IMemberType> getNwPreambleIMemeber(DatasetType dataset) {
        Optional<AbstractFeatureMemberType> preamble = dataset.getImemberOrMember().stream().filter(this::isNwPreamble).findFirst();
        return preamble.map(abstractFeatureMemberType -> (IMemberType) abstractFeatureMemberType);
    }

    private boolean isNwPreamble(AbstractFeatureMemberType preambleCandidate) {
        return preambleCandidate instanceof IMemberType
                && ((IMemberType)preambleCandidate).getInformationType().getDeclaredType().equals(NWPreambleType.class);
    }
}
