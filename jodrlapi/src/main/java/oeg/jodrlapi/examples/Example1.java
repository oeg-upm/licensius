package oeg.jodrlapi.examples;

import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.odrlmodel.*;

import java.util.Arrays;
import org.apache.jena.riot.Lang;


/**
 * This class shows a very simple example creating a simple ODRL expression: a prohibition and a permission
 * @see http://www.w3.org/ns/odrl/2/#sec-example-1
 * 
 * @author Victor
 */
public class Example1 {
 
    /**
     * @param args No arguments are needed
     */
    public static void main(String[] args) {

        //We create a policy. It can be anonymous, or it can receive a name (URI)
        Policy policy = new Policy("http://example.com/policy0099");
        
        //We create a permission to read and write
        Permission permission = new Permission();
        permission.setTarget("http://example.com/asset9898");
        //Fully qualified actions or short name actions can be given
        permission.setActions(Arrays.asList(new Action("read"), new Action("http://www.w3.org/ns/odrl/2/write")));
        policy.addRule(permission);
        
        //We create the prohibition to distribute
        Prohibition prohibition = new Prohibition();
        prohibition.setTarget("http://example.com/asset9898");
        prohibition.setActions(Arrays.asList(new Action("http://www.w3.org/ns/odrl/2/distribute")));
        policy.addRule(prohibition);
        
        //We serialize the policy
        String rdf=ODRLRDF.getRDF(policy,Lang.TTL);
        System.out.println(rdf);
        
        
        System.out.println("\n"+policy.toJSONLD());
        
    }
    
    
}


