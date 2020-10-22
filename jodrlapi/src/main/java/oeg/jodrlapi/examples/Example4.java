package oeg.jodrlapi.examples;

import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.odrlmodel.*;

import java.util.Arrays;
import oeg.jodrlapi.odrlmodel.Policy;
import org.apache.jena.riot.Lang;


/**
 * This class shows a very simple example creating a simple ODRL expression. 
 * The policy here is simply a policy request
 * @see http://www.w3.org/ns/odrl/2/#sec-example-4
 * 
 * @author Victor
 */
public class Example4 {
 
    /**
     * @param args No arguments are needed
     */
    public static void main(String[] args) {

        //We create a policy. It can be anonymous, or it can receive a name (URI)
        Policy policy = new Policy("http://example.com/policy4311");
        policy.setTypeofpolicy(Policy.POLICY_REQUEST);
        //We create a permission to read and write
        Permission permission = new Permission();
        permission.setTarget("http://example.com/news0099");
        permission.setActions(Arrays.asList(new Action("display")));
        permission.setAssignee(new Party("http://example.com/guest0589"));
        policy.addRule(permission);

        
        //We serialize the policy
        String rdf=ODRLRDF.getRDF(policy,Lang.TTL);
        System.out.println(rdf);
        
    }
    
    
}


