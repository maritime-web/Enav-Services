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
package dk.dma.embryo.user.persistence;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import dk.dma.embryo.common.persistence.DaoImpl;
import dk.dma.embryo.user.model.SailorRole;
import dk.dma.embryo.user.model.SecuredUser;

@Stateless
public class RealmDaoImpl extends DaoImpl implements RealmDao {

    @SuppressWarnings("unused")
    public RealmDaoImpl() {
        super();
    }

    public RealmDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public SecuredUser findByUsername(String username) {
        TypedQuery<SecuredUser> query = em.createNamedQuery("SecuredUser:findByUserName", SecuredUser.class);
        query.setParameter("userName", username);

        return getSingleOrNull(query.getResultList());
    }

    @Override
    public SecuredUser getByPrimaryKeyReturnAll(Long key) {
        TypedQuery<SecuredUser> query = em.createNamedQuery("SecuredUser:getByPrimaryKeyReturnAll", SecuredUser.class);
        query.setParameter("id", key);
        return getSingleOrNull(query.getResultList());
    }

    @Override
    public SailorRole getSailor(Long userId) {
        TypedQuery<SailorRole> query = em.createNamedQuery("Sailor:withVessel", SailorRole.class);
        query.setParameter("id", userId);
        return query.getSingleResult();
    }

    @Override
    public List<SecuredUser> list(){
        TypedQuery<SecuredUser> query = em.createNamedQuery("SecuredUser:list", SecuredUser.class);
        return query.getResultList();
    }
    
    @Override
    public SecuredUser findByEmail(String email) {
        TypedQuery<SecuredUser> query = em.createNamedQuery("SecuredUser:findByEmail", SecuredUser.class);
        query.setParameter("email", email);
        return getSingleOrNull(query.getResultList());
    }
    
    @Override
    public SecuredUser findByUuid(String uuid) {
        TypedQuery<SecuredUser> query = em.createNamedQuery("SecuredUser:findByUuid", SecuredUser.class);
        query.setParameter("uuid", uuid);
        return getSingleOrNull(query.getResultList());
    }

    @Override
    public SecuredUser findByMaritimeCloudId(String maritimeCloudId) {
        TypedQuery<SecuredUser> query = em.createNamedQuery("SecuredUser:findByMaritimeCloudId", SecuredUser.class);
        query.setParameter("maritimeCloudId", maritimeCloudId);
        return getSingleOrNull(query.getResultList());
    }


    @Override
    public List<Object[]> rolesCount(){
        Query query = em.createQuery("SELECT r.logicalName AS name, COUNT(su) AS total FROM SecuredUser su JOIN su.role r GROUP BY r.logicalName ORDER BY r.logicalName ASC");
        //Query query = em.createNativeQuery("SELECT logicalName, count(*) FROM SecuredUser INNER JOIN Role r ON Role_id = r.id GROUP BY roleName");

        //noinspection unchecked
        return query.getResultList();
    }

}
