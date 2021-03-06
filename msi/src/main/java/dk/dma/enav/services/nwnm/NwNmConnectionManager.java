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

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;


/**
 * If the Niord system is using SSL certificates from a Certificate Authority unknown to Java, attempts to
 * fetch NW-NM messages will yield an SSL exception.
 * Example: COMODO is sadly used by niord.dma.dk, however, older JDKs will not have the CA certs in their
 * trust store, jre/lib/security/cacerts.
 *
 * This utility class can produce a {@code ClientConnectionManager} that basically ignores the SSL chain.
 * Important: After creating a new ClientConnectionManager, you must eventually call {@code shutdown()} to free resources.
 */
public class NwNmConnectionManager {

    private final ClientConnectionManager connectionManager;

    /**
     * Instantiates a {@code NwNmConnectionManager} that basically ignores the SSL chain.
     * Important: After creating a new ClientConnectionManager, you must eventually call {@code shutdown()} to free resources.
     */
    public NwNmConnectionManager() throws KeyManagementException, NoSuchAlgorithmException {

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
                null,
                new TrustManager[] { new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                } },
                new java.security.SecureRandom());

        SSLSocketFactory sslSocketFactory = new SSLSocketFactory(
                sslContext,
                SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        Scheme httpsScheme = new Scheme("https", 443, sslSocketFactory);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(httpsScheme);

        Scheme httpScheme = new Scheme("http", 80, new PlainSocketFactory());
        schemeRegistry.register(httpScheme);

        connectionManager = new BasicClientConnectionManager(schemeRegistry);
    }


    /**
     * Should be called when using the NW-NM connection manager has completed
     */
    public void shutdown() {
        if (connectionManager != null) {
            connectionManager.shutdown();
        }
    }


    /**
     * Executes the given URL as a GET request, and returns resulting JSON
     * @param url the URL to execute
     * @return the resulting JSON
     */
    public String getJson(String url) throws IOException {
        HttpGet httpGet = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager);
            httpClient.setRedirectStrategy(new LaxRedirectStrategy());
            httpGet = new HttpGet(url);
            httpGet.addHeader("Accept", "application/json;charset=UTF-8");
            HttpParams httpParams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(
                    httpParams, 10 * 1000); // http.connection.timeout
            HttpConnectionParams.setSoTimeout(
                    httpParams, 10 * 1000); // http.socket.timeout

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return httpClient.execute(httpGet, responseHandler);

        } finally {
            if (httpGet != null) {
                try {
                    httpGet.releaseConnection();
                } catch (Exception ignored) {
                }
            }
        }
    }

}
