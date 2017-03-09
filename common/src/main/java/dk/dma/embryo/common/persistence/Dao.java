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
package dk.dma.embryo.common.persistence;

import java.util.List;

public interface Dao {

    /**
     * Remove entity
     *
     * @param bean a managed entity
     */
    void remove(IEntity<?> bean);

    /**
     * Save (insert or update) the entity bean
     * 
     * @param bean a new or existing entity
     * @return entity the entity after it has been persisted or merged
     */
    <E extends IEntity<?>> E saveEntity(E bean);
    <E extends IEntity<?>> E saveEntityWithFlush(E bean);
    
    
    /**
     * General purpose method to retrieve all instance of a entityType
     * @param entityType the class
     * @return a list of entities of the given type
     */
    <E extends IEntity<?>> List<E> getAll(Class<E> entityType);


    <E extends IEntity<?>> Long count(Class<E> entityType);

}
