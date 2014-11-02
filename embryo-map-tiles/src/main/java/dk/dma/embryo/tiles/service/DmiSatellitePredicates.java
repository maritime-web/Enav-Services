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
package dk.dma.embryo.tiles.service;

import com.google.common.base.Predicate;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.Set;

/**
 * @author Jesper Tejlgaard
 */
public class DmiSatellitePredicates {

    public static Predicate<FTPFile> downloaded(String fileNamePrefix, Set<String> existingFileNames) {
        return new DownloadedPredicate(fileNamePrefix, existingFileNames);
    }

    public static Predicate<FTPFile> fullyDownloaded(String localChartDirectory, String... requiredFileTypes) {
        return new FullyDownloadedPredicate(localChartDirectory, requiredFileTypes);
    }


    private static class DownloadedPredicate implements Predicate<FTPFile> {
        private final Set<String> existingFiles;
        private final String fileNamePrefix;

        public DownloadedPredicate(String fileNamePrefix, Set<String> existingFileNames) {
            this.existingFiles = existingFileNames;
            this.fileNamePrefix = fileNamePrefix;
        }

        @Override
        public boolean apply(FTPFile file) {
            return existingFiles.contains(fileNamePrefix + file.getName());
        }
    }

    private static class FullyDownloadedPredicate implements Predicate<FTPFile> {
        private final String localDmiDirectory;
        private final String[] requiredFilesInIceObservation;

        public FullyDownloadedPredicate(String localDmiDirectory, String... requiredFileTypes) {
            this.localDmiDirectory = localDmiDirectory;
            this.requiredFilesInIceObservation = requiredFileTypes;
        }

        @Override
        public boolean apply(FTPFile file) {
            for (String suffix : requiredFilesInIceObservation) {
                String fileName = localDmiDirectory + "/" + file.getName() + suffix;
                if (!new File(fileName).exists()) {
                    return false;
                }
            }

            return true;
        }
    }
}