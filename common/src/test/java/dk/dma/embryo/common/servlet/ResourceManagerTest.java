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
package dk.dma.embryo.common.servlet;

import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * Created by Jesper Tejlgaard on 4/19/16.
 */
public class ResourceManagerTest {

    @Test(expected = NullPointerException.class)
    public void testGetRequestedResourceNoBasePath(){
        ResourceManager manager = new ResourceManager();
        try{
            manager.getRequestedResource(null, "default-configuration.properties");
            Assert.fail("Expected load to fail");
        } catch(UnsupportedEncodingException e){
            Assert.fail("Expected NullPointerException");
        }
    }

    @Test
    public void testGetRequestedResourceNoFileUrl(){
        ResourceManager manager = new ResourceManager();
        try{
            Resource resource = manager.getRequestedResource(".", null);

            Assert.assertNotNull(resource);
            Assert.assertFalse(resource.exists());
        }catch(UnsupportedEncodingException e){
            Assert.fail("Expected NullPointerException");
        }
    }

    @Test
    public void testGetRequestedResource(){
        ResourceManager manager = new ResourceManager();
        try{
            Resource resource = manager.getRequestedResource(".", "src/test/resources/default-configuration.properties");
            Assert.assertNotNull(resource);
            Assert.assertTrue(resource.exists());
        }catch(UnsupportedEncodingException e){
            Assert.fail("Expected NullPointerException");
        }
    }
}
