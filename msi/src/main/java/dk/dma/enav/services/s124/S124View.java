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

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
public class S124View {

    private final String dataSetId;
    private String id;
    private String title;
    private String areaHeading;
    private String mainType;

    public S124View(String dataSetId) {
        this.dataSetId = dataSetId;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class S124ViewBuilder {

        private final String dataSetId;
        private String id;
        private String title;
        private String areaHeading;
        private String mainType;

        public S124ViewBuilder(String dataSetId) {
            this.dataSetId = dataSetId;
        }

        public S124View build() {
            S124View s124View = new S124View(this.dataSetId);
            s124View.setId(id);
            s124View.setMainType(mainType);
            s124View.setTitle(title);
            s124View.setAreaHeading(areaHeading);
            return s124View;
        }

    }
}
