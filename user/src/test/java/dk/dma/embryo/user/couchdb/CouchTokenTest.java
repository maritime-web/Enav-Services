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
package dk.dma.embryo.user.couchdb;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jesper Tejlgaard on 6/21/16.
 */
public class CouchTokenTest {

    @Test
    public void testGenerate(){
        CouchToken couchToken = new CouchToken("hep hey. I am the secret");

        String result = couchToken.generate("JohnDoe");

        Assert.assertNotEquals("JohnDoe", result);
        Assert.assertNotEquals("hep hey. I am the secret", result);
        // The expected value has been taken from the calculation and is as such a bad expectation value
        // The calculated values does in the application however match the calculated values produced by CouchDB.
        Assert.assertEquals("697f494fa41ad6cbf6f56f90d6614d8645daa95a", result);
    }
}
