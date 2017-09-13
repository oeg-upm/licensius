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
//        test(27);
      superTest();
    }
    
    public static void test(int i)
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
    
    
    public static void superTest()
    {
        int errors = 0;
        System.out.println("--- TRU ALG");
        for(int i=0;i<72;i++)
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
