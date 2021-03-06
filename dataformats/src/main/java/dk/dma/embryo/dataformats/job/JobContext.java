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

import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.net.ftp.FTPClient;

import dk.dma.embryo.common.configuration.PropertyFileService;
import dk.dma.embryo.common.log.EmbryoLogService;
import dk.dma.embryo.dataformats.model.ShapeFileMeasurement;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class JobContext {
    
    public enum Context{

        DMI("dmi"), FCOO("fcoo");
    
        private final String name;
        
        Context(String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
    
    private final Map<String, String> charttypes;
    private final Map<String, String> dirtypes;
    private final List<ShapeFileMeasurement> measurementsFromDatabase;
    private final PropertyFileService propertyFileService;
    private final FTPClient ftpClient;
    private final String ftpBaseDirectory;
    private final String ftpBaseFileName;
    private final Integer ageInDays;
    private final String mailTo;
    private final String tmpDir;
    private final EmbryoLogService embryoLogService;
    private final Context context;

  //Factory method to store object creation logic in single place
    public static JobContext createNewInstance(
            Map<String, String> charttypes, 
            Map<String, String> dirtypes, 
            List<ShapeFileMeasurement> measurementsFromDatabase, 
            PropertyFileService propertyFileService,
            FTPClient ftpClient, 
            String ftpBaseDirectory, 
            String ftpBaseFileName,
            Integer ageInDays, 
            String mailTo, 
            String tmpDir,
            EmbryoLogService embryoLogService,
            Context context) {
        
        return new JobContext(charttypes, dirtypes, measurementsFromDatabase, propertyFileService, ftpClient, ftpBaseDirectory, ftpBaseFileName, ageInDays, mailTo, tmpDir, embryoLogService, context);
    }
    
    protected String getLocalDir(String chartType, String context) {
        String localDir = String.format("embryo.%s.%s.localDirectory", chartType, context);
        return this.propertyFileService.getProperty(localDir, true);
    }

    protected Map<String, String> getRegions(String chartType, String context) {
        return this.propertyFileService.getMapProperty(String.format("embryo.%s.%s.regions", chartType, context));
    }
    
}
