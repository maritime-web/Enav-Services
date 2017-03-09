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
package dk.dma.embryo.vessel.model;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import dk.dma.embryo.common.persistence.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NamedQueries({@NamedQuery(name = "Berth:findByQuery", query = "SELECT b FROM Berth b WHERE b.name LIKE :query OR b.alias like :query"),
        @NamedQuery(name = "Berth:lookup", query = "SELECT b FROM Berth b WHERE UPPER(b.name) = :name OR UPPER(b.alias) = :name")})
@NoArgsConstructor
@Setter
@Getter
public class Berth extends BaseEntity<Long> {

    private static final long serialVersionUID = -7720878907095105915L;

    // //////////////////////////////////////////////////////////////////////
    // Entity fields (also see super class)
    // //////////////////////////////////////////////////////////////////////
    @NotNull
    private String name;

    private String alias;

    private Position position;

    // //////////////////////////////////////////////////////////////////////
    // Constructors
    // //////////////////////////////////////////////////////////////////////

    public Berth(String name, String latitude, String longitude) {
        this.name = name;
        this.position = new Position(latitude, longitude);
    }

    public Berth(String name, String alias, String latitude, String longitude) {
        this.name = name;
        this.alias = alias;
        this.position = new Position(latitude, longitude);
    }
}
