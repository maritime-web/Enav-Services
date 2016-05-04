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
package dk.dma.enav.services.registry;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Steen on 27-04-2016.
 *
 */
public class LostServiceClient {
    private static final String LOST_URL = "http://efficiensea2.labs.frequentis.com:8081/lost.svc";

    @Inject
    private Logger logger;

    public String post(String request) {
        try {
            HttpResponse response = performRequest(request);
            verifyResponseIsOk(request, response);

            return responseAsString(response);
        } catch (IOException e) {
            throw new RuntimeException("", e);
        }
    }

    private HttpResponse performRequest(String request) throws IOException {
        Response response = Executor.newInstance().execute(Request.Post(LOST_URL).bodyString(request, ContentType.APPLICATION_XML));
        return response.returnResponse();
    }

    private void verifyResponseIsOk(String request, HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            logger.warn("Recieved status code {} from request to {} with request entity:\n{}", statusCode, LOST_URL, request);
            if (statusCode == 404) {
                throw new LostResourceNotFoundException();
            }
            throw new RuntimeException("statusCode: " + statusCode);
        }
    }

    private String responseAsString(HttpResponse response) throws IOException {
        ByteArrayOutputStream entityStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(entityStream);
        logger.info("The response:\n{}", entityStream.toString("UTF-8"));
        return entityStream.toString("UTF-8");
    }

}
