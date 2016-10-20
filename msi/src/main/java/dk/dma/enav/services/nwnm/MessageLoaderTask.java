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
package dk.dma.enav.services.nwnm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.niord.model.message.MessageVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Task that fetches messages from a given Maritime Cloud NW-NM service instance
 */
final class MessageLoaderTask implements Callable<List<MessageVo>> {

    Logger logger = LoggerFactory.getLogger(MessageLoaderTask.class);
    String url;

    /** Constructor */
    MessageLoaderTask(String url)  {
        this.url = Objects.requireNonNull(url);
    }


    /** Creates a new connection to the given URL **/
    private HttpURLConnection newHttpUrlConnection(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection)(new URL(url).openConnection());
        con.setRequestProperty("Accept", "application/json;charset=UTF-8");
        con.setConnectTimeout(5000); //  5 seconds
        con.setReadTimeout(10000);   // 10 seconds
        return con;
    }


    /** Fetch the messages from the given service instance. */
    @Override
    public List<MessageVo> call() throws Exception {
        long t0 = System.currentTimeMillis();
        try {
            HttpURLConnection con = newHttpUrlConnection(url);

            int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER) {

                // get redirect url from "location" header field
                String redirectUrl = con.getHeaderField("Location");

                // open the new connection again
                con = newHttpUrlConnection(redirectUrl);
            }

            try (InputStream is = con.getInputStream()) {

                String json = IOUtils.toString(is);

                ObjectMapper mapper = new ObjectMapper();
                List<MessageVo> messages = mapper.readValue(json, new TypeReference<List<MessageVo>>(){});

                logger.info(String.format(
                        "Loaded %d NW-NM messages in %s ms",
                        messages.size(),
                        System.currentTimeMillis() - t0));

                return messages;
            }

        } catch (Exception e) {
            logger.error("Failed loading NW-NM messages from url " + url +" : " + e.getMessage());
            throw new WebApplicationException("Failed loading NW-NM messages: " + e.getMessage(), 500);
        }
    }
}
