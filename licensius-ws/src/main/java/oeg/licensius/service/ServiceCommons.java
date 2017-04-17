package oeg.licensius.service;

//import com.google.appengine.labs.repackaged.org.json.JSONObject;
import java.io.IOException;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

/**
 *
 * @author Victor
 */
public class ServiceCommons {

    /**
     * Determines whether a HTTP Servlet Request is demanding Turtle
     *
     * @param request HTTP request
     * @retur true if demanding turtle
     */
    public static boolean isJSON(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            String hname = (String) enume.nextElement();
            Enumeration<String> enum2 = request.getHeaders(hname);
            while (enum2.hasMoreElements()) {
                String valor = enum2.nextElement();
                if (hname.equals("Accept")) {
                    if (valor.contains("application/json")) {
                        return true;
                    }
                }
            }
        }
        return false;

    }

    /**
     * *
     * Obtains the body from a HTTP request. Period. No more, no less.
     */
    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
        body = stringBuilder.toString();
        return body;
    }

    public static boolean isURI(String uri) {
        final URL url;
        try {
            url = new URL(uri);
            return true;
        } catch (Exception e1) {
            return false;
        }
    }

    /**
     * Retrieves a txt document from a URL
     */
    public static String downloadFromURL(String url) {
        try {
            URL oracle = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            String inputLine;
            String txt = "";
            while ((inputLine = in.readLine()) != null) {
                txt += inputLine + "\n";
            }
            in.close();
            return txt;
        } catch (Exception ex) {
            return "";
        }
    }



}
