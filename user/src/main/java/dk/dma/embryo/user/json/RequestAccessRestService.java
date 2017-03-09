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
package dk.dma.embryo.user.json;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import dk.dma.embryo.common.configuration.PropertyFileService;
import dk.dma.embryo.common.mail.MailSender;
import dk.dma.embryo.user.mail.RequestAccessMail;
import lombok.Getter;
import lombok.Setter;

@Path("/request-access")
public class RequestAccessRestService {
    @Inject
    MailSender mailSender;
    
    @Inject
    PropertyFileService propertyFileService;
    
    @POST
    @Path("/save")
    @Consumes("application/json")
    public void save(SignupRequest request) {
        mailSender.sendEmail(new RequestAccessMail(request, propertyFileService));
    }

    @Getter
    @Setter
    public static class SignupRequest {
        private String preferredLogin;
        private String contactPerson;
        private String emailAddress;
        private Long mmsiNumber;
    }
}
