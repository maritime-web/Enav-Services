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
package dk.dma.enav.services.registry.lost;

/**
 * Created by Steen on 30-05-2016.
 *
 */
enum ErrorType {
    BAD_REQUEST("badRequest"),
    INTERNAL_ERROR("internalError"),
    SERVICE_SUBSTITUTION("serviceSubstitution"),
    DEFAULT_MAPPING_RETURNED("defaultMappingReturned"),
    FORBIDDEN("forbidden"),
    NOT_FOUND("notFound"),
    LOOP("loop"),
    SERVICE_NOT_IMPLEMENTED("serviceNotImplemented"),
    SERVER_TIMEOUT("serverTimeout"),
    SERVER_ERROR("serverError"),
    LOCATION_INVALID("locationInvalid"),
    LOCATION_PROFILE_UNRECOGNIZED("locationProfileUnrecognized");

    private final String name;

    ErrorType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
