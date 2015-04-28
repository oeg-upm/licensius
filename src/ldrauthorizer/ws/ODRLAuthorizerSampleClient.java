package ldrauthorizer.ws;

//JAVA
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletResponse;

//LDRAUTHORIZER
import ldconditional.MainApp;

//LOG4J
import ldconditional.LDRConfig;
import org.apache.log4j.Logger;

public class ODRLAuthorizerSampleClient {

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception {
        MainApp.initLogger();

        Demo1();
        
        Demo2();
    }

    public static void Demo1() {
        String recursoOK = "http://linguistic.linkeddata.es/data/terminesp/lexicon/lexiconES";
        String recursoNOTOK = "http://linguistic.linkeddata.es/data/terminesp/lexicon/lexiconEN";


        ODRLAuthorizerSampleClient http = new ODRLAuthorizerSampleClient();
        try {
            String r1 = URLEncoder.encode(recursoOK, "UTF-8");
            String r2 = URLEncoder.encode(recursoNOTOK, "UTF-8");
            int PORT = Integer.parseInt(LDRConfig.getPort());
            
            String consulta1 = "http://salonica.dia.fi.upm.es:"+PORT+"/ODRLAuthorize/testAuthorize?resource=" + r1;
            String resp1 = http.sendGet(consulta1);
            System.out.println(resp1);
            String consulta2 = "http://salonica.dia.fi.upm.es:"+PORT+"/ODRLAuthorize/testAuthorize?resource=" + r2;
            String resp2 = http.sendGet(consulta2);
            System.out.println(resp2);
        } catch (Exception e) {
            Logger.getLogger("ldr").warn("Warning " + e.getMessage());
        }
    }

    public static void Demo2() {
        ODRLAuthorizerSampleClient http = new ODRLAuthorizerSampleClient();
        try {
            int PORT = Integer.parseInt(LDRConfig.getPort());;
            String usuario = "http://mayor2.dia.fi.upm.es/oeg-upm/index.php/es/phdstudents/212-daniel-vila-suero";
            String recursoNOTOK = "http://linguistic.linkeddata.es/data/terminesp/lexicon/lexiconEN";
            String resource_encoded = URLEncoder.encode(recursoNOTOK, "UTF-8");
            String usuario_encoded = URLEncoder.encode(usuario, "UTF-8");
            String consulta1 = "http://salonica.dia.fi.upm.es:"+PORT+"/ODRLAuthorize/testAuthorize?resource=" + resource_encoded;
            String resp1 = http.sendGet(consulta1);
            System.out.println(resp1);
            String consulta2 = "http://salonica.dia.fi.upm.es:"+PORT+"/ODRLAuthorize/testGetTicket?resource=" + resource_encoded +"&userid=" + usuario_encoded;
            String resp2 = http.sendGetTicket(consulta2);
            System.out.println(resp2);
            String consulta3 = "http://salonica.dia.fi.upm.es:"+PORT+"/ODRLAuthorize/testAuthorize?resource=" + resource_encoded +"&userid=" + usuario_encoded;
            http.sendPostWithTicket(consulta3, resp2);
            
        } catch (Exception e) {
            Logger.getLogger("ldr").warn("Warning " + e.getMessage());
        }
    }

    // HTTP GET request
    private String sendGet(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        Logger.getLogger("ldr").debug("\nSending 'GET' request to URL : " + url);
        Logger.getLogger("ldr").debug("Response Code : " + responseCode);
        if (responseCode != HttpServletResponse.SC_OK) {
            return "NOT AUTHORIZED";
        } else {
            return "AUTHORIZED";
        }
        /*
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        
        String respuesta = "";
        while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
        respuesta += inputLine;
        }
        in.close();
        
        //print result
        Logger.getLogger("ldr").debug(response.toString());
        return response.toString();*/

    }
    private String sendGetTicket(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        Logger.getLogger("ldr").debug("\nSending 'GET' request to URL : " + url);
        Logger.getLogger("ldr").debug("Response Code : " + responseCode);
        if (responseCode!=HttpServletResponse.SC_OK)
            return "Error";
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        String respuesta = "";
        while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
        respuesta += inputLine;
        }
        in.close();
        
        //print result
        Logger.getLogger("ldr").debug(response.toString());
        return response.toString();

    }

    // HTTP POST request
    private void sendPostWithTicket(String url, String ticket) throws Exception {
        URL obj = new URL(url);
//        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        
        String urlParameters = ticket;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        Logger.getLogger("ldr").debug("\nSending 'POST' request to URL : " + url);
        Logger.getLogger("ldr").debug("Post parameters : " + urlParameters);
        Logger.getLogger("ldr").debug("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        Logger.getLogger("ldr").debug(response.toString());

    }
    private void sendPost(String url) throws Exception {
        URL obj = new URL(url);
//        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        String urlParameters = "Hola Como estas";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        Logger.getLogger("ldr").debug("\nSending 'POST' request to URL : " + url);
        Logger.getLogger("ldr").debug("Post parameters : " + urlParameters);
        Logger.getLogger("ldr").debug("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        Logger.getLogger("ldr").debug(response.toString());

    }    
}