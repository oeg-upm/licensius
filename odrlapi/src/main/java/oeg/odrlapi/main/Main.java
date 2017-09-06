package oeg.odrlapi.main;

import java.net.URL;
import java.util.Scanner;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import oeg.odrlapi.validator.ODRLValidator;

/**
 * This is the main class intended for testing purposes.
 * https://console.cloud.google.com/home/dashboard?project=odrlapi
 * @author vroddon
 */
public class Main {

    public static void main(String[] args) {
        try{
            //6, 42
            String rdf = new Scanner(new URL("http://odrlapi.appspot.com/samples/sample054").openStream(), "UTF-8").useDelimiter("\\A").next();

/*            String odrl = new Scanner(new URL("http://w3c.github.io/poe/vocab/ODRL22.ttl").openStream(), "UTF-8").useDelimiter("\\A").next();
            Model modrl = ODRLValidator.getModel(odrl);         
            System.out.println(modrl.size());
*/            
//            InputStream is = Main.class.getResourceAsStream("/samples/sample000");
//            String rdf = IOUtils.toString(is, "UTF-8"); 
            ODRLValidator validator = new ODRLValidator();
            ValidatorResponse resp = validator.validate(rdf);
            System.out.println(resp);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
