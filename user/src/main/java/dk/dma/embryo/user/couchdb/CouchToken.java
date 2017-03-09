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
package dk.dma.embryo.user.couchdb;

import dk.dma.embryo.common.EmbryonicException;
import dk.dma.embryo.common.configuration.Property;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;

/**
 * Created by Jesper Tejlgaard on 6/21/16.
 */
@RequestScoped
public class CouchToken {

    private final String secret;

    public CouchToken(){
        secret = null;
    }

    @Inject
    public CouchToken(@Property(value = "embryo.users.couchdb.secret", defaultValue = "-") String secret){
        this.secret = secret;
    }

    public String generate(String userName){
        if(secret == null || secret.trim().length() == 0 || "-".equals(secret)) {
            return null;
        }
        try{
            return calculateRFC2104HMAC(userName, secret);
        }catch(NoSuchAlgorithmException | InvalidKeyException e){
            throw new EmbryonicException("Error calculating couch token", e);
        }
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    public static String calculateRFC2104HMAC(String data, String key)
            throws NoSuchAlgorithmException, InvalidKeyException
    {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }


}
