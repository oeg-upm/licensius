package examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import odrlmodel.Action;
import odrlmodel.Duty;
import odrlmodel.ODRLRDF;
import odrlmodel.Permission;
import odrlmodel.Policy;
import odrlmodel.Prohibition;
import org.apache.jena.riot.Lang;

/**
 * This class shows a very simple example creating a simple ODRL expression
 * @see http://www.w3.org/ns/odrl/2/#sec-example-2
 * @author Victor
 */
public class Example2 {
    
    /**
     * @param args No arguments are needed
     */
    public static void main(String[] args) {
        
        //We create a policy. It can be anonymous, or it can receive a name (URI)
        Policy policy = new Policy("http://example.com/policy:0231");
        policy.setType(Policy.POLICY_OFFER); 

        Permission permission = new Permission();
        permission.setTarget("http://example.com/music:4545");
        permission.setAssigner("http://example.com/sony:10");
        permission.setActions(Arrays.asList(new Action("http://www.w3.org/ns/odrl/2/play")));
        
        Duty payment = new Duty("http://example.com/requirements");
        payment.setActions(Arrays.asList(new Action("http://www.w3.org/ns/odrl/2/pay")));
        payment.setTarget("http://example.com/ubl:AUD0.50");
        
        permission.setDuty(payment);
            
        policy.addRule(permission);
        
        //We serialize the policy
        String rdf=ODRLRDF.getRDF(policy,Lang.TTL);
        System.out.println(rdf);

    }
}
