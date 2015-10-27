package oeg.licensius.test;

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

/**
 * Class to demo and test the Licensius getLicense service
 * @author Victor Rodriguez
 */
public class LicensiusClientTest {

    /**
     * @param args the command line arguments
     * http://www.geonames.org/ontology/ontology_v3.1.rdf
     * http://purl.org/net/p-plan
     * http://purl.org/goodrelations/v1.owl
     * http://datos.bne.es/resource/XX947766
     * http://purl.org/wf4ever/roevo
     */
    public static void main(String[] args) {
        String s ="";
        testRaw();
    }

    /**
     * 
     */
    public static void testLocalPost() {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("C:\\Desktop\\victor.owl"));
            String s = new String(encoded, StandardCharsets.UTF_8);  //StandardCharsets.UTF_8
            System.out.println(s);
            String license = getLicenseLocalPost(s);
            System.out.println(license);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test() {
        String uri = "http://linguistic.linkeddata.es/def/translation/lemonTranslation.owl";
        String license = getLicense(uri);
        System.out.println(license);
        String title = getLicenseTitle(uri);
        System.out.println(title);
    }

    public static void testRaw() {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("C:\\Desktop\\prueba.owl"));
            String s = new String(encoded, StandardCharsets.UTF_8);  //
            String license = getLicenseRaw(s);
            System.out.println(license);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testLocal() {
        String license = getLicenseLocal("http://purl.org/net/p-plan");
        System.err.println(license);
    }

    /**
     * Invokes the getLicense method of the Licensius service, so that
     * the license in a RDF resource is found.
     * @param txt Raw text of the RDF document to scan
     */
    public static String getLicenseRaw(String txt) {
        String output = "";
        try {
//            String uri = "http://138.100.15.150:8765/getLicenseRaw";
            String uri = "http://licensius.appspot.com/getLicenseRaw";
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            OutputStream out = conn.getOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");
            writer.write(txt);
            writer.close();
            out.close();
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()),"UTF-8"));
            String linea = "";
            while ((linea = br.readLine()) != null) {
                output += linea +"\n";
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return output;
    }

    /**
     * Invokes the getLicense method of the Licensius service, so that
     * the license in a RDF resource is found.
     * @param uriToScan URI to scan, for example http://purl.org/goodrelations/v1.owl
     */
    public static String getLicenseLocalPost(String txt) {
        String output = "";
        try {
            String uri = "http://localhost:8083/getLicenseRaw";
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            OutputStream out = conn.getOutputStream();
            Writer writer = new OutputStreamWriter(out);
//            writer.write("content=");
//            String codificada = URLEncoder.encode(txt, "UTF-8");
///   System.out.println(codificada);
            writer.write(txt);
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String linea = "";
            while ((linea = br.readLine()) != null) {
                output += linea;
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Invokes the getLicense method of the Licensius service, so that
     * the license in a RDF resource is found.
     * @param uriToScan URI to scan, for example http://purl.org/goodrelations/v1.owl
     */
    public static String getLicense(String uriToScan) {
        String output = "unknown";
        try {
            String uri = "http://licensius.appspot.com/getLicense?content=";
//            String uri = "http://localhost:8083/getLicense?content=";
            String encodedData = URLEncoder.encode(uriToScan);
            uri += encodedData;
            System.out.println(uri);
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String linea = "";
            while ((linea = br.readLine()) != null) {
                output = linea;
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Invokes the getLicense method of the Licensius service, so that
     * the license in a RDF resource is found.
     * @param uriToScan URI to scan, for example http://purl.org/goodrelations/v1.owl
     */
    public static String getLicenseLocal(String uriToScan) {
        String output = "unknown";
        try {
            String uri = "http://localhost:8083/getLicense?content=";
            String encodedData = URLEncoder.encode(uriToScan);
            uri += encodedData;
            System.out.println(uri);
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
                output = linea;
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Invokes the getLicense method of the Licensius service, so that
     * the license in a RDF resource is found.
     * @param uriToScan URI to scan, for example http://purl.org/goodrelations/v1.owl
     */
    public static String getLicenseTitle(String uriToScan) {
        String output = "unknown";
        try {
            String uri = "http://licensius.appspot.com/getLicenseTitle?content=";
            String encodedData = URLEncoder.encode(uriToScan);
            uri += encodedData;
            System.out.println(uri);
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
                output = linea;
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
    
    
    /**
     * This is the sample client code to guess a license from an indicative text
     * @param text Free text, like "CC0, CCZero, CC ZERO" etc.
     * @return License found (either the URL or a structured answer if JSON is requested). Empty string if no matching license was found
     */
    public static String guessLicense(String text) {
        String output = "unknown";
        try {
            String uri = "http://licensius.appspot.com/licenseguess?txt=";
            String encodedData = URLEncoder.encode(text);
            uri += encodedData;
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");            
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
            br.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }    
}
