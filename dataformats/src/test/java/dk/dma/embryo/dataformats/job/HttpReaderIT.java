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
package dk.dma.embryo.dataformats.job;

import java.io.File;
import java.io.IOException;

import dk.dma.embryo.common.util.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Integration Test reading folder and files from Russian Wether Service (AARI)
 * 
 * Named HttpReaderIT instead of HttepReaderTest to prevent maven-surefire-plugin from executing it automatically
 *
 * @author Jesper Tejlgaard
 */
@Ignore("Not included in automatic run, because depending on russian weather site")
public class HttpReaderIT {

    @Test
    public void testReadContent() throws IOException {
        HttpReader reader = new HttpReader("http", "wdc.aari.ru", 10000);

        System.out.println(reader.readContent("datasets/d0004/chu/sigrid/2014/"));
    }

    @Test
    public void testGetFile() throws IOException {
        HttpReader reader = new HttpReader("http", "wdc.aari.ru", 10000);

        File dir = new File(System.getProperty("user.home") + "/arcticweb/tmp");
        FileUtils.createDirectories(dir);
        
        File file = new File(dir.getAbsoluteFile(), "" + Math.random());
        
        reader.getFile("datasets/d0004/chu/sigrid/2014", "aari_chu_20140107_pl_a.dbf", file);
    }
}
