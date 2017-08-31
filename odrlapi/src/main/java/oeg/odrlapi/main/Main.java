package oeg.odrlapi.main;

import java.io.IOException;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import oeg.odrlapi.validator.ODRLValidator;

/**
 * This is the main class intended for testing purposes.
 * https://console.cloud.google.com/home/dashboard?project=odrlapi
 * @author vroddon
 */
public class Main {

    public static void main(String[] args) {
        String rdf = "as<vvdssd  sdl"; 
        ODRLValidator validator = new ODRLValidator();
        ValidatorResponse resp = validator.validate(rdf);
        System.out.println(resp);
    }
}
