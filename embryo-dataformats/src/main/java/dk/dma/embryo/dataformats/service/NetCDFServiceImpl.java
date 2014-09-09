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

package dk.dma.embryo.dataformats.service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import dk.dma.embryo.common.configuration.Property;
import dk.dma.embryo.common.configuration.PropertyFileService;
import dk.dma.embryo.dataformats.netcdf.NetCDFParser;
import dk.dma.embryo.dataformats.netcdf.NetCDFResult;
import dk.dma.embryo.dataformats.netcdf.NetCDFType;

@Singleton
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class NetCDFServiceImpl implements NetCDFService {

    @Inject
    @Property(value = "embryo.netcdf.types")
    private Map<String, String> netcdfTypes;

    @Inject
    @Property(value = "embryo.netcdf.providers")
    private String netcdfProviders;

    @Inject
    private PropertyFileService propertyFileService;

    private Map<NetCDFType, Map<String, NetCDFResult>> entries = new HashMap<>();


    @Lock(LockType.WRITE)
    @Override
    public void parseAllFiles(List<? extends NetCDFType> types) throws IOException {
        for (String netcdfProvider : netcdfProviders.split(";")) {
            for (String netcdfType : netcdfTypes.values()) {
                String folderName = propertyFileService.getProperty("embryo." + netcdfType + "." + netcdfProvider + ".localDirectory", true);
                System.out.println("FOLDERNAME: " + folderName);
                File folder = new File(folderName);
                if (folder.exists()) {
                    NetCDFParser parser = new NetCDFParser();
                    File[] files = folder.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            return pathname.getName().endsWith(".nc");
                        }
                    });
                    for (File file : files) {
                        Map<NetCDFType, NetCDFResult> result = parser.parse(file.getAbsolutePath(), types);
                        for(NetCDFType type : result.keySet()) {
                            Map<String, NetCDFResult> typeMap = entries.get(type);
                            if (typeMap == null) {
                                typeMap = new HashMap<String, NetCDFResult>();
                                entries.put(type, typeMap);
                            }
                            String name = file.getName().substring(0, file.getName().length() - 3) + "_" + type.getCode();
                            typeMap.put(name, result.get(type));
                        }
                    }
                } else {
                    throw new IOException("Folder " + folderName + " does not exist.");
                }
            }
        }
    }

    @Lock(LockType.READ)
    @Override
    public Map<String, NetCDFResult> getEntries(NetCDFType type) {
        return entries.get(type);
    }
}
