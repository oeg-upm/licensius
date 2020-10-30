package oeg.jodrlapi.examples;

import java.util.List;
import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.helpers.ValidatorClient;
import oeg.jodrlapi.odrlmodel.Policy;
import org.apache.jena.riot.Lang;
import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;

/**
 * Shows how to load an external policy.
 * @author vroddon
 */
public class Example8 {
    
    public static void main(String[] args) {
        
         
        List<Policy> policies = ODRLRDF.load("C:\\Users\\Pablo\\Desktop\\Pret\\pddm\\data\\licenses\\gfdl1.1.ttl"); 
                //http://rdflicense.appspot.com/rdflicense/");
        //BSD4.0.ttl
        System.out.println(policies.size());
        
        
        for(Policy pol:policies){
        
            if(pol.uri == null){
                System.out.println("nulo");
                continue;
            
            }
            try{
            System.out.println(pol.uri);
        String rdf=ODRLRDF.getRDF(pol,Lang.TTL);
        System.out.println(rdf);
        ValidatorClient.validate(rdf);
            }catch(Exception e){}
        
        
            
        }
        
    }
    
    
}
