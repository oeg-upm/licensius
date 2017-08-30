package examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import odrlmodel.Action;
import odrlmodel.ODRLRDF;
import odrlmodel.Party;
import odrlmodel.Permission;
import odrlmodel.Policy;
import odrlmodel.Prohibition;
import org.apache.jena.riot.Lang;


/**
 * This class shows a very simple example creating a simple ODRL expression
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
        Policy policy = new Policy("http://example.com/policy:4311");
        policy.setType(Policy.POLICY_REQUEST);
        //We create a permission to read and write
        Permission permission = new Permission();
        permission.setTarget("http://example.com/news:0099");
        permission.setActions(Arrays.asList(new Action("http://www.w3.org/ns/odrl/2/display")));
        permission.setAssignee(new Party("http://example.com/guest:0589"));
        policy.addRule(permission);

        
        //We serialize the policy
        String rdf=ODRLRDF.getRDF(policy,Lang.TTL);
        System.out.println(rdf);
        
    }
    
    
}


