package oeg.odrlapi.main;

import java.net.URL;
import java.util.Scanner;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import oeg.odrlapi.validator.ODRLValidator;
import oeg.odrlapi.validator.Preprocessing;

/**
 * This is the main class intended for testing purposes.
 * It contains three methods:
 * - superTest validates all the known test cases
 * - normalize Normalizes one policy
 * - validate Validates one policy
 * Control panel: https://console.cloud.google.com/home/dashboard?project=odrlapi
 * @author vroddon
 */
public class Main {

    public static void main(String[] args) {
        normalize(75);
//        validate(74);
//      superTest();
    }
    
    /**
     * Normalizes one test case
     */
    public static void normalize(int i)
    {
        try{
            String testfile = String.format("http://odrlapi.appspot.com/samples/sample%03d",i);
            String rdf = new Scanner(new URL(testfile).openStream(), "UTF-8").useDelimiter("\\A").next();
            String rdf2 = Preprocessing.preprocess(rdf);
            System.out.println(rdf2);
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    /**
     * Validates one test case
     */
    public static void validate(int i)
    {
        try{
            String testfile = String.format("http://odrlapi.appspot.com/samples/sample%03d",i);
            String rdf = new Scanner(new URL(testfile).openStream(), "UTF-8").useDelimiter("\\A").next();
            ODRLValidator validator = new ODRLValidator();
            ValidatorResponse resp = validator.validate(rdf);
            System.out.println(resp);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Iterates through all the samples showing the expected/real results
     */
    public static void superTest()
    {
        int errors = 0;
        System.out.println("--- TRU ALG");
        for(int i=0;i<73;i++)
        {
            Boolean truth = null;
            try{
                String testfile = String.format("http://odrlapi.appspot.com/samples/sample%03d",i);
                String rdf = new Scanner(new URL(testfile).openStream(), "UTF-8").useDelimiter("\\A").next();
                if (rdf.contains("#valid"))
                    truth=true;
                else if (rdf.contains("#invalid"))
                    truth=false;
                ODRLValidator validator = new ODRLValidator();
                ValidatorResponse resp = validator.validate(rdf);
                System.out.println(String.format("%03d %s %s", i, truth,resp.valid));
                if (truth!=resp.valid)
                {
                    errors++;
                    System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX");
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("Errors: " + errors);
    }
}
