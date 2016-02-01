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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import dk.dma.embryo.vessel.model.Vessel;

@Entity
public class VesselOwnerRole extends Role {
    private static final long serialVersionUID = 1L;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Vessel> vessels = new HashSet<Vessel>();

    public VesselOwnerRole() {
        super("vesselOwner");
    }

    public Set<Vessel> getVessels() {
        return vessels;
    }

    public void setVessels(Set<Vessel> vessels) {
        this.vessels = vessels;
    }

}
