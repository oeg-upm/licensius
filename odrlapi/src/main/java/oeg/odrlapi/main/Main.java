package oeg.odrlapi.main;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import oeg.odrlapi.validator.ODRLValidator;
import org.apache.commons.io.IOUtils;

/**
 * This is the main class intended for testing purposes.
 * https://console.cloud.google.com/home/dashboard?project=odrlapi
 * @author vroddon
 */
public class Main {

    public static void main(String[] args) {
        try{
            String rdf = new Scanner(new URL("http://odrlapi.appspot.com/samples/sample012").openStream(), "UTF-8").useDelimiter("\\A").next();
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
