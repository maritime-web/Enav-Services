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
package dk.dma.embryo.user.model;

import dk.dma.embryo.vessel.model.Vessel;
import org.joda.time.DateTime;
import org.junit.Test;
import org.keycloak.common.util.Time;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.representations.AccessToken;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Arrays;
import java.util.List;

import static dk.dma.embryo.user.json.UserRestService.User;

/**
 * Created by Jesper Tejlgaard on 6/11/15.
 */
public class SecuredUserTest {

    @Test
    public void testName() throws Exception {
//          String token = "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiI4Y2ExMDk4NC0wMjVjLTQ2Y2UtODFmMC1hMDYyMzNiOGQ5MjQiLCJleHAiOjE0NTY3MzcyNjAsIm5iZiI6MCwiaWF0IjoxNDU2NzM2NjYwLCJpc3MiOiJodHRwOi8vMTkyLjE2OC45OS4xMDA6ODA4MC9hdXRoL3JlYWxtcy9kZW1vIiwiYXVkIjoiYmFsdGljLXdlYiIsInN1YiI6ImY4Yjk4ZTJiLTQwMWEtNDRhYS04Y2RlLTliOTk1ZTE1YzQxMyIsInR5cCI6IkJlYXJlciIsImF6cCI6ImJhbHRpYy13ZWIiLCJub25jZSI6ImFjODc4NTQ0LWFjZDItNDViNC04ODVkLTU2ZjM5MTlhY2JmMSIsInNlc3Npb25fc3RhdGUiOiJmODkxYjg5MC1hY2ZiLTQxNGMtODQ1Zi1iZTJjMTZjMTZhMjEiLCJjbGllbnRfc2Vzc2lvbiI6IjZkZjU4MWVlLTBlNTUtNDljZS1hZWNhLTAxYWM4YjJkMDEwZSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJlbmF2LXNlcnZpY2VzIjp7InJvbGVzIjpbInVzZXIiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJ2aWV3LXByb2ZpbGUiXX19LCJuYW1lIjoidXNlcl9maXJzdCB1c2VyX2xhc3QiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyIiwiZ2l2ZW5fbmFtZSI6InVzZXJfZmlyc3QiLCJmYW1pbHlfbmFtZSI6InVzZXJfbGFzdCIsImVtYWlsIjoidXNlckBkbWEuZGsifQ.hE6bPq6Z5sP-AhbckFB046PHRCIEyB4MkM-FgvxtZ82Dghivb0IYYHOc_JCoLhrqJM7CQYcXfGut-gWw5jBPB6ceen0MEkjr1p_nR0Pwenn3Erb4eeXCMu_iymMo12cUOuAvMOje5oO2Q0eAxHOsG4dLn5DOkdROk5TQX4RfZpNd_Gtlr-oecYisTF5NrDNgoP46eYPe3VaGtD4SNdSZskXDJ32ekGHGY9_iJX2vNR76tJJddl45ls6PPkfyBDu-k0pJJR-iEZjfC28T1VTKkitkxnknNlOfmDmDtBMrL2J9plyqkfDTiPajfO-B6t8uCpv44w65H83P1pLRYMwHXQ";
//          String token = "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiI3MzE5MjY1OS03MWIyLTQ5OWUtOGQ0MS1hNDYzZTQ2N2RkNTgiLCJleHAiOjE0NTY3NTMzNDksIm5iZiI6MCwiaWF0IjoxNDU2NzUzMDQ5LCJpc3MiOiJodHRwOi8vMTkyLjE2OC45OS4xMDA6ODA4MC9hdXRoL3JlYWxtcy9kZW1vMiIsImF1ZCI6ImJhbHRpYy13ZWIiLCJzdWIiOiIyYjZmZDI5Ny04ZGMzLTQ1ZjctYmNkMC0wMDYzNzJiOWI5MTAiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJiYWx0aWMtd2ViIiwibm9uY2UiOiJjMmI5MGU5OS03NjZjLTRmN2MtODRmYS02ZDAwZDhlYmNhYjMiLCJzZXNzaW9uX3N0YXRlIjoiOWFkZjYxODAtMTQ0Zi00OGZiLTg4ZTEtYjljNzkxZDM0MzJhIiwiY2xpZW50X3Nlc3Npb24iOiIzZmZhNjM1YS1mNmY4LTQzOGEtYTUxZi01YzU0Yjg2ZGFhMWUiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgwIl0sInJlc291cmNlX2FjY2VzcyI6eyJlbmF2LXNlcnZpY2VzIjp7InJvbGVzIjpbInVzZXIiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJ2aWV3LXByb2ZpbGUiXX19LCJuYW1lIjoidXNlcjIgdXNlcjIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyMiIsImdpdmVuX25hbWUiOiJ1c2VyMiIsImZhbWlseV9uYW1lIjoidXNlcjIifQ.ag0ZuZvk_cTT04Cx3GOt6rOf81GRxzyM9qH43QPA7LfkwaE4DGb6-1qrbW_CizekHwoISIY4sX7ITcgoHjzwXhKzdKK90mYPkg81LtI3IN1z1QCJTKIjZFbT3BYOKFEobXJaz0n-I1GxskGme8zWb6svtZDDESUUfBJ7PRbDKzOownSFW3KB0Laerh2YnFeTPKL6OBMrFzxtF9EFb8T5cD4yuqt5OuiG5Jay8pKct0KWBqYiazyCwRi-ugfatq6dS3DNsUuLs69UGoDZjmUHWvyDdi9EI4lEwphVh9uASJ-MnA514OcOytmFQT22PZv-dvvuPnffoQXqzM2ZdeClIA";
//          String token = "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiI1OTY3YjkxOS0xNDA0LTRkZWYtYjg0OS1jYjc4MjY1MWM2OGEiLCJleHAiOjE0NTY3NTM3NTYsIm5iZiI6MCwiaWF0IjoxNDU2NzUzNDU2LCJpc3MiOiJodHRwOi8vMTkyLjE2OC45OS4xMDA6ODA4MC9hdXRoL3JlYWxtcy9kZW1vMiIsImF1ZCI6ImJhbHRpYy13ZWIiLCJzdWIiOiIyYjZmZDI5Ny04ZGMzLTQ1ZjctYmNkMC0wMDYzNzJiOWI5MTAiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJiYWx0aWMtd2ViIiwibm9uY2UiOiI0MmUwYTEwMC1jMTIzLTQ3N2YtOGUyNy05ZDU4ZTAxMGVjNjIiLCJzZXNzaW9uX3N0YXRlIjoiOWFkZjYxODAtMTQ0Zi00OGZiLTg4ZTEtYjljNzkxZDM0MzJhIiwiY2xpZW50X3Nlc3Npb24iOiI1ZWZjNmVhYy05ZGZlLTRjNjYtOTJjNi03YTkzODY5ZjcwNDMiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgwIl0sInJlc291cmNlX2FjY2VzcyI6eyJlbmF2LXNlcnZpY2VzIjp7InJvbGVzIjpbInVzZXIiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJ2aWV3LXByb2ZpbGUiXX19LCJuYW1lIjoidXNlcjIgdXNlcjIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyMiIsImdpdmVuX25hbWUiOiJ1c2VyMiIsImZhbWlseV9uYW1lIjoidXNlcjIifQ.gq5lzkgSlEVMk0HnaR392ZDmRMEGOb5UaWMninFu7F-D2LeMUpAhfsu5o9iuBK1mtWbqxmlhD2RQgBE6my0dcTnSdj8ndaY0eByejDfY3C5jVHj4OAPb2s8_K8L09holo8fSmhQgajaFOTXzJHiVbklUhLbgDoPJOj-JE_HZOuqFnXUonyqHRrDGGHOo_QUT3Z03DNzQbOAt709wftwytiFiYlN4w7n-MHgKaU4ex7wBeKWrEwMc5m-bWEs3ic1b3pI_xXAahhTBAv4KdDfZRaasWq2VTaPwG9OQJXgUbLcZM-eNqhm_RKSHIeOP1cRIJUWPbgvua0PXPyKqcIhDPA";
//          String token = "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJkZjYxMWYwYi0xZWJjLTRlYzUtYWUwZC1lNDJiNzk0ZDQ0NDUiLCJleHAiOjE0NTY3NTYzMTIsIm5iZiI6MCwiaWF0IjoxNDU2NzU2MDEyLCJpc3MiOiJodHRwOi8vMTkyLjE2OC45OS4xMDA6ODA4MC9hdXRoL3JlYWxtcy9kZW1vMiIsImF1ZCI6ImJhbHRpYy13ZWIiLCJzdWIiOiIyYjZmZDI5Ny04ZGMzLTQ1ZjctYmNkMC0wMDYzNzJiOWI5MTAiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJiYWx0aWMtd2ViIiwibm9uY2UiOiJkMjRhYjVmMS0zMGNhLTRjOWQtYjliOS1hZDgxNGJmMDYxMmYiLCJzZXNzaW9uX3N0YXRlIjoiOWFkZjYxODAtMTQ0Zi00OGZiLTg4ZTEtYjljNzkxZDM0MzJhIiwiY2xpZW50X3Nlc3Npb24iOiJlMTQ2YTg1MS03ZTlhLTQ1MWItYTc2OS1hZWRiNGNlYzgzNmEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgwIl0sInJlc291cmNlX2FjY2VzcyI6eyJlbmF2LXNlcnZpY2VzIjp7InJvbGVzIjpbInVzZXIiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJ2aWV3LXByb2ZpbGUiXX19LCJuYW1lIjoidXNlcjIgdXNlcjIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyMiIsImdpdmVuX25hbWUiOiJ1c2VyMiIsImZhbWlseV9uYW1lIjoidXNlcjIifQ.lgiUjYOjNOQUpUxtJodjzmZhDEQhVTXkrm0e7bsAhy-KQT2IEswL95kjNunhOeO9USSsV9sV0-FEqAcD9KuS39HULo2Xa9FwB_mxIH_ixK87e90B8-5Ui6r98wBiW0Qdc8eNX8X03FTJSAPpoPwoT-xRRgA5hkoqKA3Fn1J9_1mvVbg2oiPGmXCu1zlEzVoUfLNb3HHMeQza7s5a1IH_UrpmFaftchUaIOG4jv4o8AZFaZUZDM45d9_8fFHXaK9wc_n94ximfAoT8xH-wm213XYKA6cBDN1zvefh1MMEMYLC-3yOSz246n_9sFq_jtHIultfFmQbieqT5eku5iY1-Q";
//          String token = "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJlYWZhMmVjYi0zNTIxLTRkM2YtOGI5Yy1jM2NjNmYxM2NiM2IiLCJleHAiOjE0NTY3NTgwMTEsIm5iZiI6MCwiaWF0IjoxNDU2NzU3NzExLCJpc3MiOiJodHRwOi8vMTkyLjE2OC45OS4xMDA6ODA4MC9hdXRoL3JlYWxtcy9kZW1vMiIsImF1ZCI6ImJhbHRpYy13ZWIiLCJzdWIiOiIyYjZmZDI5Ny04ZGMzLTQ1ZjctYmNkMC0wMDYzNzJiOWI5MTAiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJiYWx0aWMtd2ViIiwibm9uY2UiOiIzNDUzNTQwZC04MTU1LTQ1ZDYtOGE1ZS1lNDNjNWUzYTRhNmMiLCJzZXNzaW9uX3N0YXRlIjoiOWFkZjYxODAtMTQ0Zi00OGZiLTg4ZTEtYjljNzkxZDM0MzJhIiwiY2xpZW50X3Nlc3Npb24iOiJlZmZkZmNiNy00NTAxLTRiZmQtODc2Ny03YTYzNzBmNmVjMDAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgwIl0sInJlc291cmNlX2FjY2VzcyI6eyJlbmF2LXNlcnZpY2VzIjp7InJvbGVzIjpbInVzZXIiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJ2aWV3LXByb2ZpbGUiXX19LCJuYW1lIjoidXNlcjIgdXNlcjIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyMiIsImdpdmVuX25hbWUiOiJ1c2VyMiIsImZhbWlseV9uYW1lIjoidXNlcjIifQ.AVJIySVk9uXgO423clS_OSzTgJGZ-xycMYHRttKO_HcCJc4iBhrrPia8zKrBZ8DSB4j8MLX6HbXDflnbvEE-Q2-LR6gPSe8nT2nmO94g5h5m1DflUPOBNhIBtwKdXr7colTC3-_k3y9h3Qy3wWphHsi1H7Xm5H-S3er0H43Ps_I2OXJK8hb-L7lpNmKhdytxv7T6tJSIEQ1FK-svuNGqO2tHaeAGO98esN8GgLDjUKkrxJ56S4gkpywTtqA1d4S0XCuyonPX0aqeTcBmntXIlVOr75f-iUo9ocMS-D0Eem69lhxDMoElIy6D6rt4yVf6KUaPSHb1aFKCDkKYXVqrIA";
            String token = "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJlYWZhMmVjYi0zNTIxLTRkM2YtOGI5Yy1jM2NjNmYxM2NiM2IiLCJleHAiOjE0NTY3NTgwMTEsIm5iZiI6MCwiaWF0IjoxNDU2NzU3NzExLCJpc3MiOiJodHRwOi8vMTkyLjE2OC45OS4xMDA6ODA4MC9hdXRoL3JlYWxtcy9kZW1vMiIsImF1ZCI6ImJhbHRpYy13ZWIiLCJzdWIiOiIyYjZmZDI5Ny04ZGMzLTQ1ZjctYmNkMC0wMDYzNzJiOWI5MTAiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJiYWx0aWMtd2ViIiwibm9uY2UiOiIzNDUzNTQwZC04MTU1LTQ1ZDYtOGE1ZS1lNDNjNWUzYTRhNmMiLCJzZXNzaW9uX3N0YXRlIjoiOWFkZjYxODAtMTQ0Zi00OGZiLTg4ZTEtYjljNzkxZDM0MzJhIiwiY2xpZW50X3Nlc3Npb24iOiJlZmZkZmNiNy00NTAxLTRiZmQtODc2Ny03YTYzNzBmNmVjMDAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgwIl0sInJlc291cmNlX2FjY2VzcyI6eyJlbmF2LXNlcnZpY2VzIjp7InJvbGVzIjpbInVzZXIiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJ2aWV3LXByb2ZpbGUiXX19LCJuYW1lIjoidXNlcjIgdXNlcjIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyMiIsImdpdmVuX25hbWUiOiJ1c2VyMiIsImZhbWlseV9uYW1lIjoidXNlcjIifQ.AVJIySVk9uXgO423clS_OSzTgJGZ-xycMYHRttKO_HcCJc4iBhrrPia8zKrBZ8DSB4j8MLX6HbXDflnbvEE-Q2-LR6gPSe8nT2nmO94g5h5m1DflUPOBNhIBtwKdXr7colTC3-_k3y9h3Qy3wWphHsi1H7Xm5H-S3er0H43Ps_I2OXJK8hb-L7lpNmKhdytxv7T6tJSIEQ1FK-svuNGqO2tHaeAGO98esN8GgLDjUKkrxJ56S4gkpywTtqA1d4S0XCuyonPX0aqeTcBmntXIlVOr75f-iUo9ocMS-D0Eem69lhxDMoElIy6D6rt4yVf6KUaPSHb1aFKCDkKYXVqrIA";
        JWSInput j = new JWSInput(token);
        AccessToken accessToken = j.readJsonContent(AccessToken.class);
        System.out.println(new String(j.getContent(), "UTF-8"));
//        System.out.println(new String(j.getSignature(), "UTF-8"));
        System.out.println(accessToken.isActive());
        System.out.println(accessToken.getExpiration() + "   " + DateTime.now().withMillis(accessToken.getExpiration()*1000));
        int currentTime = Time.currentTime();
        System.out.println(currentTime + "   " + DateTime.now().withMillis(currentTime*1000));
// 1456756945   1970-01-09T20:57:11.656+01:00

    }

    @Test
    public void testToJsonModel() {
        SecuredUser user1 = new SecuredUser();
        user1.setAisFilterName("FooFilter");
        user1.setUserName("JohnDoe");
        user1.setEmail("john@doe.com");
        SailorRole sailor = new SailorRole();
        Vessel vessel = new Vessel(123456789L);
        sailor.setVessel(vessel);
        user1.setRole(sailor);

        SecuredUser user2 = new SecuredUser();
        user2.setAisFilterName("BarFilter");
        user2.setUserName("Ella");
        user2.setEmail("ella@doe.com");
        ShoreRole shore = new ShoreRole();
        user2.setRole(shore);

        List<SecuredUser> users = Arrays.asList(user1, user2);

        User exp1 = new User();
        exp1.setAisFilterName("FooFilter");
        exp1.setEmail("john@doe.com");
        exp1.setLogin("JohnDoe");
        exp1.setRole("Sailor");
        exp1.setShipMmsi(123456789L);

        User exp2 = new User();
        exp2.setAisFilterName("BarFilter");
        exp2.setEmail("ella@doe.com");
        exp2.setLogin("Ella");
        exp2.setRole("Shore");

        List<User> expected = Arrays.asList(exp1, exp2);

        ReflectionAssert.assertReflectionEquals(expected, SecuredUser.toJsonModel(users));
    }

}
