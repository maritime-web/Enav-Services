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

package dk.dma.embryo.common.configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Jesper Tejlgaard on 10/2/14.
 */
@Getter
@Setter
public class Provider {
    private String shortName;
    private String notificationEmail;
    private List<Type> types;


    // //////////////////////////////////////////////////////////////////////
    // Constructors
    // //////////////////////////////////////////////////////////////////////
    public Provider(String shortName, String notificationEmail) {
        this.shortName = shortName;
        this.notificationEmail = notificationEmail;
    }

    public void accept(ProviderVisitor visitor) {
        visitor.visit(this);
        for (Type type : this.getTypes()) {
            type.accept(visitor);
        }
    }
}
