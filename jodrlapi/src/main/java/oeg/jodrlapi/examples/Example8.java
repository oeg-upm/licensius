package oeg.jodrlapi.examples;

import java.util.List;
import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.helpers.ValidatorClient;
import oeg.jodrlapi.odrlmodel.Policy;
import org.apache.jena.riot.Lang;

/**
 * Shows how to load an external policy.
 * @author vroddon
 */
public class Example8 {
    
    public static void main(String[] args) {
        List<Policy> policies = ODRLRDF.load("http://rdflicense.appspot.com/rdflicense/cc-by4.0");
        Policy policy = policies.get(0);
        String rdf=ODRLRDF.getRDF(policy,Lang.TTL);
        System.out.println(rdf);
        ValidatorClient.validate(rdf);
    }
    
    
}
