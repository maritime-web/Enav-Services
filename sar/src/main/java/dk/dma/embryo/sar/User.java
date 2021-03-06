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
package dk.dma.embryo.sar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.n1global.acc.json.CouchDbDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple Class name (i.e. 'User') is added as @type property on JSON document.
 * This class is used in JavaScript
 */
@Setter
@Getter
public class User extends CouchDbDocument {

    private String userName;
    private String name;
    private String mmsi;

    // //////////////////////////////////////////////////////////////////////
    // Constructors
    // //////////////////////////////////////////////////////////////////////
    public User() {
    }

    @JsonCreator
    public User(@JsonProperty("userName") String userName, @JsonProperty("name") String name, @JsonProperty("mmsi") String mmsi) {
        this.name = name;
        this.userName = userName;
        this.mmsi = mmsi;
    }


    public User(String id, String userName, String name, String mmsi) {
        super(id);
        this.name = name;
        this.userName = userName;
        this.mmsi = mmsi;
    }

    // //////////////////////////////////////////////////////////////////////
    // Business Logic
    // //////////////////////////////////////////////////////////////////////
    public static Map<String, User> toMap(List<User> users) {
        return users.stream().filter(d -> d.getClass() == User.class).collect(Collectors.toMap(User::getDocId, user -> user));
    }
}
