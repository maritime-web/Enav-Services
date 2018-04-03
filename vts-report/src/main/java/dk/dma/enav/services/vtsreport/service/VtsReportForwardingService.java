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

package dk.dma.enav.services.vtsreport.service;

import com.google.common.base.Strings;
import javax.ws.rs.Produces;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;




/**
 * Created by rob on 4/3/18.
 * forwards a POST to another POST, to send report to SMA VTS ADMIN interface (http -> https)
 */


@SuppressWarnings("all")
@Path("/reportforwarding")
public class VtsReportForwardingService {



    @GET
    @Produces("text/plain")
    public String getMessage() {
        return "VTS report forwarding endpoint - expecting POST.";
    }

//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
////    @Consumes(MediaType.TEXT_PLAIN)
//    public Response createTrackInText (String msg) {
//
//
////        return Response.status(201).entity("{\"confirm\":true,\"message\":\""+interceptedReport+"\"}").build();
//        return Response.status(201).entity("{\"confirm\":true}").build();
//
//    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String postMessage(String msg){

        if(hasValue(msg)){
            msg = "{" + msg + "}";
            HttpURLConnection con = null;
//            String url = "http://e2-demoapi.azurewebsites.net";
            String url = "http://sejlrute.dmi.dk/SejlRute/SR?req=%7B%22mssi%22:999999999,%22datatypes%22:%5B%22current%22,%22wave%22,%22wind%22%5D,%22dt%22:15,%22waypoints%22:%5B%7B%22eta%22:%222018-04-04T02:38:10.000%2B0100%22,%22heading%22:%22GC%22,%22lat%22:53.95796988977668,%22lon%22:10.874544382095337%7D,%7B%22eta%22:%222018-04-04T02:48:10.000%2B0100%22,%22heading%22:%22GC%22,%22lat%22:54.05796988977668,%22lon%22:10.974544382095337%7D%5D%7D";
            String urlParameters = "";
            byte[] postData = msg.getBytes(StandardCharsets.UTF_8);
//            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

            try {

                URL myurl = new URL(url);
                con = (HttpURLConnection) myurl.openConnection();

                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Java client");
//                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Content-Type", "application/text");

                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.write(postData);
                }catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                }

                StringBuilder content;

                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()))) {

                    String line;
                    content = new StringBuilder();

                    while ((line = in.readLine()) != null) {
                        content.append(line);
                        content.append(System.lineSeparator());
                    }
                    msg = content.toString();
                }

                System.out.println(content.toString());

            } catch (IOException e) {
                e.printStackTrace();
                msg = "SOMETHING FAILED:" + e;
            } finally {

                if(con != null) {
                    con.disconnect();
                }
            }
            return msg;
        }else{
            return null;
        }
    }


    private boolean hasValue(String aString) {
        return !Strings.isNullOrEmpty(aString) && !aString.equals("0");
    }


//    public String forwardMessage(String msg){
//        HttpURLConnection con;
//
//        return true;
//    }


//    public class JavaPostRequest {


    public void javaPostRequest(String msg){

//        HttpURLConnection con = null;
//
//
//        String url = "http://e2-demoapi.azurewebsites.net";
//            String urlParameters = "";
//            byte[] postData = msg.getBytes(StandardCharsets.UTF_8);
////            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
//
//            try {
//
//                URL myurl = new URL(url);
//                con = (HttpURLConnection) myurl.openConnection();
//
//                con.setDoOutput(true);
//                con.setRequestMethod("POST");
//                con.setRequestProperty("User-Agent", "Java client");
//                con.setRequestProperty("Content-Type", "application/json");
//
//                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
//                    wr.write(postData);
//                }catch (IOException ioe) {
//                    System.out.println(ioe.getMessage());
//                }
//
//                StringBuilder content;
//
//                try (BufferedReader in = new BufferedReader(
//                        new InputStreamReader(con.getInputStream()))) {
//
//                    String line;
//                    content = new StringBuilder();
//
//                    while ((line = in.readLine()) != null) {
//                        content.append(line);
//                        content.append(System.lineSeparator());
//                    }
//                }
//
//                System.out.println(content.toString());
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//
//                if(con != null) {
//                    con.disconnect();
//                }
//            }
        }
//    }


}

