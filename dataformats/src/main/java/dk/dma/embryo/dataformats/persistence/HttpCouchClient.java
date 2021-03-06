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

package dk.dma.embryo.dataformats.persistence;

import dk.dma.embryo.common.EmbryonicException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 *
 */
@Slf4j
public class HttpCouchClient {

    private final CouchDbConfig config;
    private final Executor executor;

    public HttpCouchClient(Executor executor, CouchDbConfig config) {
        this.executor = executor;
        this.config = config;

    }

    public String get(String id) {
        String res;
        String forecastDbUrl = null;
        try {
            forecastDbUrl = config.getForecastDbUrl() + (config.getForecastDbUrl().endsWith("/") ? "" : "/") + id;
            res = execute(Request.Get(forecastDbUrl)).returnContent().asString();

        } catch (HttpResponseException e) {
            if (e.getStatusCode() == 404) {
                return null;
            }
            throw new EmbryonicException("Got : \"" + e.getStatusCode() + "\" when requsting the document defined by: \"" + forecastDbUrl + "\"", e);
        } catch (IOException e) {
            throw new EmbryonicException("Got error requsting: \"" + forecastDbUrl + "\"", e);
        }

        return res;
    }

    public String getByView(String viewQuery) {
        String res;
        String viewUrl = null;
        try {
            String forecastDbUrl = config.getForecastDbUrl() + (config.getForecastDbUrl().endsWith("/") ? "" : "/");
            String designDocumentId = config.getDesignDocumentId() + (config.getDesignDocumentId().endsWith("/") ? "" : "/");
            viewUrl = forecastDbUrl + designDocumentId + "_view" + viewQuery;

            res = execute(Request.Get(viewUrl)).returnContent().asString();

        } catch (HttpResponseException e) {
            if (e.getStatusCode() == 404) {
                return "{\"rows\" : [{\"id\" : \"NOT FOUND RESULT\", \"value\" : {\"no_value\" : \"NO VALUE\"}}]}";
            }
            throw new EmbryonicException("Got : \"" + e.getStatusCode() + "\" when requsting the view defined by: \"" + viewUrl + "\"", e);
        } catch (IOException e) {
            throw new EmbryonicException("Got error requsting: \"" + viewUrl + "\"", e);
        }

        return res;
    }

    public void upsert(String id, String json) {
        String upsertUrl = null;
        try {
            String forecastDbUrl = config.getForecastDbUrl() + (config.getForecastDbUrl().endsWith("/") ? "" : "/");
            String designDocumentId = config.getDesignDocumentId() + (config.getDesignDocumentId().endsWith("/") ? "" : "/");
            upsertUrl = forecastDbUrl + designDocumentId + "_update/upsert/" + id;
            HttpResponse httpResponse = execute(Request.Put(upsertUrl).bodyString(json, ContentType.APPLICATION_JSON))
                    .returnResponse();

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != 201) {
                String responseFromCouch = parseResponse(httpResponse);
                throw new EmbryonicException("Unable to upsert document with id \"" + id + "\" using : " + upsertUrl + "\nResponse was:\n" + responseFromCouch);
            }
        } catch (IOException e) {
            throw new EmbryonicException("Got error requsting: \"" + upsertUrl + "\"", e);
        }
    }

    private String parseResponse(HttpResponse httpResponse) {
        StringBuilder res = new StringBuilder();
        res.append(httpResponse.getStatusLine()).append("\n---\n")
                .append(Arrays.deepToString(httpResponse.getAllHeaders())).append("\n---\n");
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            try {
                ByteArrayOutputStream entityStream = new ByteArrayOutputStream();
                entity.writeTo(entityStream);
                res.append(entityStream.toString("UTF-8"));
            } catch (IOException e) {
                //Ignore
            }
        }
        return res.toString();
    }

    private Response execute(Request request) {
        try {
            return executor.execute(request
                    .connectTimeout(30000)
                    .socketTimeout(30000));

        } catch (IOException e) {
            throw new EmbryonicException("Got error requsting: \"" + request + "\"", e);
        }
    }

    void initialize() {
        ensureDB();
        addDesignDocument();
    }

    private void ensureDB() {
        try {
            HttpResponse httpResponse = execute(Request.Put(config.getForecastDbUrl())).returnResponse();
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (!(statusCode == 412 || statusCode < 300)) {
                throw new EmbryonicException("Unable to create CouchDb from url: " + config.getForecastDbUrl() + " Status code: " + statusCode);
            }
        } catch (IOException e) {
            throw new EmbryonicException("Unable to create CouchDb from url: " + config.getForecastDbUrl(), e);
        }
    }

    private void addDesignDocument() {
        String designDocument = readFromClassPath(config.getDesignDocumentResourceUrl());
        String designDocumentId = config.getDesignDocumentId();
        String designUrl = config.getForecastDbUrl() + (config.getForecastDbUrl().endsWith("/") ? designDocumentId : "/" + designDocumentId);
        log.info("Upserting design document with id \"" + designDocumentId + "\"");
        try {
            HttpResponse httpResponse = execute(Request.Put(designUrl).bodyString(designDocument, ContentType.APPLICATION_JSON))
                    .returnResponse();

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (designdocumentAlreadyExists(statusCode)) {
                updateDesignDocument(designUrl, designDocument);
            } else if (statusCode >= 400) {
                throw new EmbryonicException("Unable to add or update design document. Got status code: " + statusCode);
            }
        } catch (IOException e) {
            throw new EmbryonicException("Unable to add or update design document", e);
        }
    }

    private void updateDesignDocument(String designUrl, String designDocument) throws IOException {
        HttpResponse httpResponse;
        int statusCode;
        httpResponse = execute(Request.Head(designUrl)).returnResponse();
        String revId = httpResponse.getFirstHeader("ETag").getValue();

        StringBuilder sb = new StringBuilder(designDocument);
        sb.insert(sb.indexOf("{") + 1, "\"_rev\": " + revId + ", ");
        log.info("Updating existing design document\n" + sb);
        httpResponse = execute(Request.Put(designUrl).bodyString(sb.toString(), ContentType.APPLICATION_JSON))
                .returnResponse();

        statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode != 201) {
            throw new EmbryonicException("Unable to add or update design document. Got status code: " + statusCode);
        }
    }

    private boolean designdocumentAlreadyExists(int statusCode) {
        return statusCode == 409;
    }

    private String readFromClassPath(String designDocumentResourceUrl) {
        InputStream resourceAsStream = getClass().getResourceAsStream(designDocumentResourceUrl);
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"));
            br.lines().forEach(v -> sb.append(v).append("\n"));
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
    }

}
