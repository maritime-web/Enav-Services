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
package dk.dma.enav.services.vtsreport.mail;


import dk.dma.embryo.common.configuration.PropertyFileService;
import dk.dma.embryo.common.mail.Mail;
import dk.dma.embryo.common.util.DateTimeConverter;

/**
 * @author Jesper Tejlgaard
 */
public class VtsReportMail extends Mail<VtsReportMail> {

    private String userEmail;
    private String recipient;

    public VtsReportMail(String preparedHeader,String preparedBody, String userEmail, String recipient, PropertyFileService propertyFileService) {
        super(preparedHeader,preparedBody,propertyFileService);
        this.userEmail = userEmail;
        this.recipient = recipient;
//        this.preparedHeader = preparedHeader;
//        this.preparedBody = preparedBody;
    }

    public VtsReportMail build() {
        DateTimeConverter reportTsConverter = DateTimeConverter.getDateTimeConverter("MM");

        setTo(recipient + ";"); //expects semicolon for split
        setFrom(propertyFileService.getProperty("embryo.notification.mail.from")); //global from sender
        setCc(userEmail); //sends copy to user

        return this;
    }
}
