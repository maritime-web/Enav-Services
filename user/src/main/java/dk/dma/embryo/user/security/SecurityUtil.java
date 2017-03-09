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
package dk.dma.embryo.user.security;

import dk.dma.embryo.user.model.SecuredUser;
import lombok.Getter;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * Utility class with factory method. 
 * 
 * 
 * @author Jesper Tejlgaard
 * 
 */
public class SecurityUtil{

    private static RandomNumberGenerator RANDOM_NUMBER_GENERATOR = new SecureRandomNumberGenerator();
    public static SecuredUser createUser(String userName, String password, String email, String aisFilterName) {
       
        HashedPassword hashedPassword = hashPassword(password);
        return new SecuredUser(userName, hashedPassword.getPassword(), hashedPassword.getSalt(), email, aisFilterName);
    }
    
    public static HashedPassword hashPassword(String password) {
        ByteSource salt = RANDOM_NUMBER_GENERATOR.nextBytes();

//        HashedCredentialsMatcher matcher = (HashedCredentialsMatcher)jpaRealm.getCredentialsMatcher();
        String algorithmName = "SHA-512";//matcher.getHashAlgorithmName();
        int iterations = 10000;//matcher.getHashIterations();

        //Now hash the plain-text password with the random salt and multiple
        //iterations and then Base64-encode the value (requires less space than Hex):
        String hashedPasswordBase64 = new SimpleHash(algorithmName, password, salt, iterations).toBase64();

        return new HashedPassword(hashedPasswordBase64, salt.getBytes());
     }

    @Getter
    public static class HashedPassword {
        private final String password;
        private final byte[] salt;
        
        public HashedPassword(String password, byte[] salt) {
            this.password = password;
            this.salt = salt;
        }
    }
}
