package oeg.ldconditional.client;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import ldconditional.Main;
import org.apache.log4j.Logger;

/**
 *
 * @author Victor
 */
public class TestClient {
    
    static String SERVER = "http://localhost";
    private static final Logger logger = Logger.getLogger(TestClient.class);    
    
    public static void main(String[] args) {
        Main.initLogger();

        testGetOffers("geo");
    }

    public static void testGetOffers(String dataset) {
        String uriToScan = "";
        String output = "";
        try {
            String uri = SERVER+"/" +dataset+"/service/getOffers?content=";
            String encodedData = URLEncoder.encode(uriToScan);
            uri += encodedData;
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String linea = "";
            while ((linea = br.readLine()) != null) {
                output += linea;
            }
            conn.disconnect();            
        }catch(Exception e)
        {
            logger.warn(e.getMessage());
        }
        System.out.println(output);
    }
    
    
}
