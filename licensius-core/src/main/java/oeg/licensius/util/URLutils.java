/*
Copyright (c) 2015 OEG

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package oeg.licensius.util;

import com.google.apphosting.utils.security.urlfetch.URLFetchServiceStreamHandler;
import java.net.*;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.apache.jena.atlas.lib.StrUtils;

/**
 * This class includes some useful functions to manipulate URLs
 * @author Victor
 */
public class URLutils {

    private static final Logger logger = Logger.getLogger(URLutils.class.getName());

    /**
     * Returns the last chunk in a URL
     */
    public static String getLastBitFromUrl(final String url) {
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }

    /**
     * Loads a file from a URI, downloading it. 
     */
    public static String getFile(String uri) {
        String txt = "";
        try {
            URL oracle = new URL(uri);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                txt+=inputLine+" \n";
            }
            in.close();
        } catch (Exception e) {
        }
        return txt;
    }
    
    /**
     * Downloads a file making its best efforts to retrieve RDF.
     * In particular:
     * - it adds to the HTTP header the need for RDF (content negotiation).
     * - it follows redirects.
     * - pending: to scan for RDFa.
     * curl -I -L -H "Accept: application/rdf+xml" http://datos.bne.es/resource/XX947766
     */
    public static String browseSemanticWeb(String url) {
        String document = "";
        String acceptHeaderValue = StrUtils.strjoin(",","application/rdf+xml","application/turtle;q=0.9","application/x-turtle;q=0.9","text/n3;q=0.8","text/turtle;q=0.8","text/rdf+n3;q=0.7","application/xml;q=0.5","text/xml;q=0.5","text/plain;q=0.4","*/*;q=0.2");
        boolean redirect = false;
        try {
            URL obj = new URL(url);
            URLConnection oc = obj.openConnection();
            HttpURLConnection conn = null;
            if (url.startsWith("https"))
            {
                logger.info("Protocol is HTTPS");
                try
                {
                    logger.info("Changing to HttpsURLConnection");
                    conn = (HttpsURLConnection)oc;
                }catch(Exception e)
                {
                    logger.info("Changing to HttpURLConnection because of Google URLFetchServiceStreamHandler");
                    conn = (HttpURLConnection)oc;
                }
            }
            else
            {
                logger.info("Protocol is HTTP");

                try
                {
                    logger.info("Changing to HttpURLConnection");
                    conn = (HttpURLConnection)oc;
                }catch(Exception e)
                {
                    if (oc.getClass().equals(URLFetchServiceStreamHandler.class))
                    {
                        logger.info("Changing to HttpURLConnection because of Google URLFetchServiceStreamHandler");
                        conn = (HttpURLConnection)oc;
                    }
                }
                
            }
            
            conn.setRequestProperty("Accept",acceptHeaderValue);                
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
                logger.info("We are being redirected");
		String newUrl = conn.getHeaderField("Location");
		String cookies = conn.getHeaderField("Set-Cookie");  
                conn.disconnect();
                if (newUrl.startsWith("https"))
                {
                    logger.info("Protocol is now HTTPS");
                    if (oc.getClass().equals(HttpsURLConnection.class))
                        conn = (HttpsURLConnection)oc;
                    if (oc.getClass().equals(URLFetchServiceStreamHandler.class))
                        conn = (HttpURLConnection)oc;
                }
                else
                {
                    logger.info("Protocol is now HTTP");
                    conn = (HttpURLConnection) new URL(newUrl).openConnection();
                    if (oc.getClass().equals(URLFetchServiceStreamHandler.class))
                        conn = (HttpURLConnection)oc;
                }
                if (cookies!=null) //What for?
                {
        //            conn.setRequestProperty("Cookie", cookies);                
                }
            }
	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	String inputLine;
	StringBuffer html = new StringBuffer();
	while ((inputLine = in.readLine()) != null) {html.append(inputLine);html.append("\n");}
	in.close();            
            
        document = html.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Fatal error browsing the semantic web " + e.getMessage());
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
    
    /**
     * Useful method to get a map of property-values from a URI
     */
    public static Map<String, String> getQueryMap(String query)  
    {  
        String[] params = query.split("&");  
        Map<String, String> map = new HashMap<String, String>();  
        for (String param : params)  
        {  
            String name = param.split("=")[0];  
            String value = param.split("=")[1];  
            map.put(name, value);  
        }  
        return map;  
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
    public static String downloadFromURL(String url)
    {
        try {
            URL oracle = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            String inputLine;
            String txt ="";
            while ((inputLine = in.readLine()) != null)
                txt += inputLine+"\n";
            in.close();    
            return txt;
        } catch (Exception ex) {
            return "";
        }
    }    
}
