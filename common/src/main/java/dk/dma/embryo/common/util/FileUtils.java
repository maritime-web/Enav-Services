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
package dk.dma.embryo.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Simple wrapper which checks the boolean response for some of the file operations (delete, mkdirs)
 * @author Klaus Groenbaek
 *         Created 08/03/17.
 */
@Slf4j
public final class FileUtils {

    public static void createDirectories(File file) {
        if (!file.mkdirs()) {
            log.warn("Unable to create directory {}", file);
        }
    }

    public static void createDirectoriesIfNeeded(File file) {
        if (!file.exists()) {
            createDirectories(file);
        }
    }

    private FileUtils() {
    }


}
