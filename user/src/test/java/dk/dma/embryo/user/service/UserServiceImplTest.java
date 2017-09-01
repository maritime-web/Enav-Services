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
package dk.dma.embryo.user.service;

import dk.dma.embryo.user.model.SailorRole;
import dk.dma.embryo.user.model.SecuredUser;
import dk.dma.embryo.user.persistence.RealmDao;
import dk.dma.embryo.vessel.model.Vessel;
import dk.dma.embryo.vessel.persistence.VesselDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
    @Mock
    private RealmDao realmDao;
    @Mock
    private VesselDao vesselDao;
    @InjectMocks
    private UserServiceImpl cut;

    @Test
    public void shouldDeleteVesselWhenDeletingUser() throws Exception {
        SecuredUser userWithRole = createUserWithSailorRole();
        when(realmDao.findByUsername("login")).thenReturn(userWithRole);

        cut.delete("login");

        verify(vesselDao).remove(any());
    }

    private SecuredUser createUserWithSailorRole() {
        SecuredUser res = new SecuredUser("user", "passwd", new byte[]{}, "user@dma.dk", "aisFilter");
        SailorRole sailorRole = new SailorRole();
        sailorRole.setVessel(new Vessel(123456789L));
        res.setRole(sailorRole);
        return res;
    }
}
