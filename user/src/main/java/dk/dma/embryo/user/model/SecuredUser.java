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

import dk.dma.embryo.common.persistence.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NamedQueries({
    @NamedQuery(name = "SecuredUser:findByUserName", query = "SELECT u FROM SecuredUser u WHERE u.userName=:userName"),
    // LEFT JOIN FETCH u.roles [identification variable] is not supported by JPA but by Hibernate
    @NamedQuery(name = "SecuredUser:getByPrimaryKeyReturnAll", query = "SELECT u FROM SecuredUser u LEFT JOIN FETCH u.role WHERE u.id=:id"),
    @NamedQuery(name = "SecuredUser:list", query = "SELECT u FROM SecuredUser u LEFT JOIN FETCH u.role AS r LEFT JOIN FETCH r.vessel"),
    @NamedQuery(name="SecuredUser:findByEmail", query = "SELECT u FROM SecuredUser u WHERE u.email=:email"),
    @NamedQuery(name="SecuredUser:findByMaritimeCloudId", query = "SELECT u FROM SecuredUser u WHERE u.maritimeCloudId=:maritimeCloudId"),
    @NamedQuery(name="SecuredUser:findByUuid", query = "SELECT u FROM SecuredUser u WHERE u.forgotUuid=:uuid")})
@ToString
@Setter
@Getter
public class SecuredUser extends BaseEntity<Long> {

    private static final long serialVersionUID = -8480232439011093135L;

    // //////////////////////////////////////////////////////////////////////
    // Entity fields (also see super class)
    // //////////////////////////////////////////////////////////////////////
    @Setter(AccessLevel.NONE)
    private String maritimeCloudId;

    private String hashedPassword;

    private byte[] salt;

    @Column(unique=true)
    private String userName;

    private String email;

    private String forgotUuid;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Setter(AccessLevel.NONE)
    private DateTime created;

    @OneToOne(cascade=CascadeType.REMOVE)
    private Role role;

    @OneToMany(orphanRemoval = true ,cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name="SecuredUser_id", nullable = false)
    private List<AreasOfInterest> areasOfInterest = new ArrayList<>();

    private String aisFilterName;

    public SecuredUser() {}

    /*
     * Used for unit test
     */
    public SecuredUser(Long id) {
        this.id=id;
    }

    public SecuredUser(String userName, String hashedPassword, byte[] salt) {
        setUserName(userName);
        setHashedPassword(hashedPassword);
        setSalt(salt);
        created = DateTime.now(DateTimeZone.UTC);
    }

    public SecuredUser(String userName, String hashedPassword, byte[] salt, String email, String aisFilterName) {
        this(userName, hashedPassword, salt);
        setEmail(email);
        setAisFilterName(aisFilterName);
    }

    public SecuredUser(User user) {
        this.maritimeCloudId = user.getMaritimeCloudId();
        setUserName(user.getLogin());
        setEmail(user.getEmail());
        setAisFilterName(user.getAisFilterName());
        created = DateTime.now(DateTimeZone.UTC);
    }

    // //////////////////////////////////////////////////////////////////////
    // business logic
    // //////////////////////////////////////////////////////////////////////
    public void addSelectionGroup(AreasOfInterest group) {

        if (this.areasOfInterest == null) {
            this.areasOfInterest = new ArrayList<>();
        }

        this.areasOfInterest.add(group);
    }

    public User toJsonModel() {
        User user = new User();
        user.setAisFilterName(getAisFilterName());
        user.setLogin(getUserName());
        user.setEmail(getEmail());
        Role role = getRole();
        user.setRole(role == null ? null : role.getLogicalName());
        if (role instanceof SailorRole) {
            SailorRole sailor = (SailorRole) role;
            user.setShipMmsi(sailor.getVessel().getMmsi());
        }
        return user;
    }

    public static List<User> toJsonModel(List<SecuredUser> users) {
        return users.stream().map(user -> user.toJsonModel()).collect(Collectors.toList());
    }

    public boolean hasActiveAreasOfInterest() {
        if (this.areasOfInterest != null && !this.areasOfInterest.isEmpty()) {
            for (AreasOfInterest group : this.areasOfInterest) {
                if(group.getActive()) {
                    return true;
                }
            }
        }

        return false;
    }
}
