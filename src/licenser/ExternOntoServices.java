package licenser;

//APACHE HTTP 4.0
import org.apache.http.client.HttpClient;
import org.apache.http.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
/**
 *
 * @author vroddon
 */
public class ExternOntoServices {

    /**
     * This method makes a test query to the Ooops service.
     * @see http://www.oeg-upm.net/oops
     */
    public static void testOOPS() {
        try {
            String uri = "http://oops-ws.oeg-upm.net/rest";
            String request = getRequest("http://www.cc.uah.es/ie/ont/learning-resources.owl");
            HttpPost post = new HttpPost(uri);
            HttpEntity entity = new ByteArrayEntity(request.getBytes("UTF-8"));
            post.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);
            String result = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Error llamando a Maria");
            } else {
                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to create a XML request to OOPS
     * @param ontologia URI of the ontology to be scanned
     */
    private static String getRequest(String ontologia) {
        String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><OOPSRequest><OntologyUrl>";
        s += ontologia;
        s += "</OntologyUrl><OntologyContent></OntologyContent><Pitfalls>10</Pitfalls>"
                + "<OutputFormat></OutputFormat></OOPSRequest>";
        return s;
    }

    
    public static int getYahooTemperature(String city) {
        Client client = Client.create();
        String rest = "http://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20weather.bylocation%20WHERE%20location%3D'";
        rest += city;
        rest += "'%20AND%20unit%3D%22c%22&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        WebResource webResource = client.resource(rest);
        String s = webResource.get(String.class);
        int i = s.indexOf("<yweather:wind chill=");
        int j = s.indexOf("\"", i + 22);
        s = s.substring(i + 22, j);
        s = s.trim();
        int resultado = 0;
        try{
        resultado=Integer.parseInt(s);}catch(Exception e){}
        return resultado;
    }    
}
