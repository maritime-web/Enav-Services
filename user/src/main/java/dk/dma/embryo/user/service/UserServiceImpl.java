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

import dk.dma.embryo.user.model.AdministratorRole;
import dk.dma.embryo.user.model.AreasOfInterest;
import dk.dma.embryo.user.model.KnownRoles;
import dk.dma.embryo.user.model.ReportingAuthorityRole;
import dk.dma.embryo.user.model.Role;
import dk.dma.embryo.user.model.SailorRole;
import dk.dma.embryo.user.model.SecuredUser;
import dk.dma.embryo.user.model.ShoreRole;
import dk.dma.embryo.user.model.User;
import dk.dma.embryo.user.model.UsernamePassword;
import dk.dma.embryo.user.persistence.RealmDao;
import dk.dma.embryo.user.security.SecurityUtil;
import dk.dma.embryo.user.security.SecurityUtil.HashedPassword;
import dk.dma.embryo.vessel.model.Vessel;
import dk.dma.embryo.vessel.persistence.VesselDao;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.FinderException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Stateless
@Slf4j
public class UserServiceImpl implements UserService {

    @Inject
    private RealmDao realmDao;

    @Inject
    private VesselDao vesselDao;

    private Role createRole(String role, Long mmsi) {
        switch (role) {
        case "Sailor":
            Vessel vessel = getVessel(mmsi);

            SailorRole sailor = new SailorRole();
            sailor.setVessel(vessel);
            realmDao.saveEntity(sailor);
            return sailor;
        case "Shore":
            ShoreRole shore = new ShoreRole();
            realmDao.saveEntity(shore);
            return shore;
        case "Reporting":
            ReportingAuthorityRole authority = new ReportingAuthorityRole();
            realmDao.saveEntity(authority);
            return authority;
        case "Administration":
            AdministratorRole administrator = new AdministratorRole();
            realmDao.saveEntity(administrator);
            return administrator;
        default:
            throw new IllegalArgumentException("Unknown role \""+role+"\"");
        }
    }

    private Vessel getVessel(Long mmsi) {
        Vessel vessel = vesselDao.getVessel(mmsi);
        if (vessel == null) {
            vessel = new Vessel();
            vessel.setMmsi(mmsi);
            vesselDao.saveEntity(vessel);
        }
        return vessel;
    }

    @Override
    public void create(String login, String password, Long mmsi, String email, String role, String aisFilterName) {
        SecuredUser su = SecurityUtil.createUser(login, password, email, aisFilterName);
        su.setRole(createRole(role, mmsi));
        realmDao.saveEntity(su);
    }

    @Override
    public void createFrom(User user) {
        SecuredUser newUser = new SecuredUser(user);
        newUser.setRole(createRole(user.getRole(), user.getShipMmsi()));
        realmDao.saveEntity(newUser);
    }

    public void edit(String login, Long mmsi, String email, String role, String aisFilterName) {

        SecuredUser user = realmDao.findByUsername(login);

        if (email != null) {
            user.setEmail(email);
        }

        user.setAisFilterName(aisFilterName);

        if (role != null) {
            if (user.getRole().getClass() == SailorRole.class) {
                Role oldRole = user.getRole();
                Vessel oldVessel = ((SailorRole) oldRole).getVessel();
                user.setRole(null);
                realmDao.saveEntity(user);
                realmDao.remove(oldRole);
                vesselDao.remove(oldVessel);
            } else if (user.getRole() != null) {
                Role oldRole = user.getRole();
                user.setRole(null);
                realmDao.saveEntity(user);
                realmDao.remove(oldRole);
            }
            user.setRole(createRole(role, mmsi));
        }

        realmDao.saveEntity(user);
    }

    @Override
    public void mergeWith(User user) {
        edit(user.getLogin(), user.getShipMmsi(), user.getEmail(), user.getRole(), user.getAisFilterName());
    }

    @Override
    public void delete(String login) {
        SecuredUser user = realmDao.findByUsername(login);
        if (user.getRole().getClass() == SailorRole.class) {
            Vessel v = ((SailorRole)user.getRole()).getVessel();
            vesselDao.remove(v);
        }
        realmDao.remove(user);
    }

    @Override
    public List<SecuredUser> list() {
        return realmDao.list();
    }

    @Override
    public void createPasswordUuid(SecuredUser user) {
        UUID uuid = UUID.randomUUID();
        user.setForgotUuid(uuid.toString());
        realmDao.saveEntity(user);
    }

    @Override
    public void changePassword(String uuid, String password) throws FinderException {
        SecuredUser user = realmDao.findByUuid(uuid);
        if(user == null) {
            throw new FinderException("No user for given UUID.");
        }
        HashedPassword hashedPassword = SecurityUtil.hashPassword(password);

        user.setHashedPassword(hashedPassword.getPassword());
        user.setSalt(hashedPassword.getSalt());
        user.setForgotUuid(null);

        realmDao.saveEntity(user);
    }

    @Override
    public void changePassword(UsernamePassword newPassword) throws FinderException {
        SecuredUser user = realmDao.findByUsername(newPassword.getUsername());
        if (user == null) {
            throw new FinderException("No user for given username '" + newPassword.getUsername() + "'");
        }
        HashedPassword hashedPassword = SecurityUtil.hashPassword(newPassword.getPassword());

        user.setHashedPassword(hashedPassword.getPassword());
        user.setSalt(hashedPassword.getSalt());

        realmDao.saveEntity(user);
    }

    @Override
    public void updateAreasOfInterest(List<AreasOfInterest> areasOfInterests, String userName) throws FinderException {
        SecuredUser securedUserCurrent = this.realmDao.findByUsername(userName);
        if(securedUserCurrent == null) {
            throw new FinderException("No user for given userName.");
        }

        // Important to clear() and NOT nullify <- hibernate do not understand it correctly!
        securedUserCurrent.getAreasOfInterest().clear();
        SecuredUser userReadyForUpdate = this.realmDao.saveEntityWithFlush(securedUserCurrent);

        for (AreasOfInterest areasOfInt : areasOfInterests) {
            userReadyForUpdate.addSelectionGroup(areasOfInt);
        }

        SecuredUser savedUser = this.realmDao.saveEntityWithFlush(userReadyForUpdate);
    }

    public List<Object[]> rolesCount(){
        return this.realmDao.rolesCount();
    }

}
