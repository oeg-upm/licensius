package oeg.jodrlapi.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implements a client for the validator here http://odrlapi.appspot.com/validator
 * @author vroddon
 */
public class ValidatorClient {
    public static String validate(String input) {
        String res ="";
        try {

            URL url = new URL("http://odrlapi.appspot.com/validator");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "text/turtle");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            //conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes("UTF-8"));
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                return "Failed : HTTP error code : " + conn.getResponseCode();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                res = output;
                System.out.println(output);
            }
            conn.disconnect();
            return res;
        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();

        }
        return res;
    }    
}
