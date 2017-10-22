package licenser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.jena.atlas.lib.StrUtils;

public class URLUtils {

    public static void main(String[] args) {

        System.out.println(URLUtils.checkIfURLExists("http://sparql.reegle.info/"));//http://www.google.com/
    }
    /*
    String URIdataset = "http://www.w3.org/ns/dcat#DataSet";
    String URIdcrights = "http://purl.org/dc/terms/rights";
    String URIdclicense ="http://purl.org/dc/terms/license";
    String URIcclicense = "http://creativecommons.org/ns#license";
    String URIxhtmllicense = "www.w3.org/1999/xhtml/vocab/‎license";
     */

    
    
    /**
     * Downloads a file making its best:
     * - following redirects
     * - implementing the best content negotiation
     */
    public static String browseSemanticWeb(String url) {
        String document = "";
        String acceptHeaderValue = StrUtils.strjoin(",","application/rdf+xml","application/turtle;q=0.9","application/x-turtle;q=0.9","text/n3;q=0.8","text/turtle;q=0.8","text/rdf+n3;q=0.7","application/xml;q=0.5","text/xml;q=0.5","text/plain;q=0.4","*/*;q=0.2");
        boolean redirect = false;
        try {
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestProperty("Accept", "application/rdf+xml");                
            conn.setReadTimeout(5000);
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    redirect = true;
                }
            }
            
            if (redirect) {
		String newUrl = conn.getHeaderField("Location");
		String cookies = conn.getHeaderField("Set-Cookie");  
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestProperty("Cookie", cookies);                
            }
	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	String inputLine;
	StringBuffer html = new StringBuffer();
	while ((inputLine = in.readLine()) != null) {html.append(inputLine);}
	in.close();            
            
        document = html.toString();
            
        } catch (Exception e) {
            
        }
        return document;
    }

    public static boolean checkIfURLExists(String targetUrl) {
        HttpURLConnection httpUrlConn;
        try {
            httpUrlConn = (HttpURLConnection) new URL(targetUrl).openConnection();

            // A HEAD request is just like a GET request, except that it asks
            // the server to return the response headers only, and not the
            // actual resource (i.e. no message body).
            // This is useful to check characteristics of a resource without
            // actually downloading it,thus saving bandwidth. Use HEAD when
            // you don't actually need a file's contents.
            httpUrlConn.setRequestMethod("HEAD");


            // Set timeouts in milliseconds
            httpUrlConn.setConnectTimeout(30000);
            httpUrlConn.setReadTimeout(30000);

            // Print HTTP status code/message for your information.
            System.out.println("Response Code: "
                    + httpUrlConn.getResponseCode());
            System.out.println("Response Message: "
                    + httpUrlConn.getResponseMessage());

            return (httpUrlConn.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}