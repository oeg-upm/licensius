package oeg.licensius.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * @author victor
 */
public class TestClient {

//    private static final String SERVICE_URL = "http://www.licensius.appspot.com/api/test";
    private static final String SERVICE_URL = "http://localhost:8080/licensius-ws/api/test";
//    private static final String SERVICE_URL2 = "http://www.licensius.appspot.com/api/license/getLicense";
    private static final String SERVICE_URL2 = "http://localhost:8080/licensius-ws/api/license/getLicense";

    
    public static void main(String args[]) {
        String res = invokeGET(SERVICE_URL);
        System.out.println(res);
        String body = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF xmlns=\"http://www.semanticweb.org/leopo/ontologies/2022/0/AirTraffic_Infrastructure#\"\n"
                + "     xml:base=\"http://www.semanticweb.org/leopo/ontologies/2022/0/AirTraffic_Infrastructure\"\n"
                + "     xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"
                + "     xmlns:ns=\"http://creativecommons.org/ns#\"\n"
                + "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "     xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"\n"
                + "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "     xmlns:AirTrafficInfrastructure=\"http://www.semanticweb.org/leopo/ontologies/2022/0/AirTrafficInfrastructure#\">\n"
                + "    <owl:Ontology rdf:about=\"http://www.semanticweb.org/leopo/ontologies/2022/0/AirTrafficInfrastructure\">\n"
                + "        <ns:license rdf:resource=\"https://spdx.org/licenses/MIT.html\"/>\n"
                + "        <dc:creator>Leopoldo Santos</dc:creator>\n"
                + "        <AirTrafficInfrastructure:content_license>The MIT License (MIT)\n"
                + "        </AirTrafficInfrastructure:content_license>\n"
                + "    </owl:Ontology>\n"
                + "</rdf:RDF>";
        res = invokePOST(SERVICE_URL2, body);
        System.out.println(res);
    }

    public static String invokePOST(String surl, String body) {
        try {
            URL url = new URL(surl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes());
            os.flush();
            os.close();
            
            conn.connect();
            int responsecode = conn.getResponseCode();
            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else {
                String inline = "";
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }
                scanner.close();
                return inline;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";

        }
    }

    public static String invokeGET(String surl) {
        try {
            URL url = new URL(surl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else {

                String inline = "";
                Scanner scanner = new Scanner(url.openStream());
                //Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }
                //Close the scanner
                scanner.close();
                return inline;
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            return "";

        }
    }

}
