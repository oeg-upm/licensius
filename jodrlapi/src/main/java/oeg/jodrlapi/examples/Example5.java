package oeg.jodrlapi.examples;

import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.odrlmodel.*;
import java.util.Arrays;
import org.apache.jena.riot.Lang;

/**
 * This class shows a very simple example creating a simple ODRL expression
 * @see http://www.w3.org/ns/odrl/2/#sec-example-5
 * @author Victor
 */
public class Example5 {
    
    /**
     * @param args No arguments are needed
     */
    public static void main(String[] args) {
        
        //We create a policy. It can be anonymous, or it can receive a name (URI)
        Policy policy = new Policy("http://example.com/policy:0811");
        policy.setTypeofpolicy(Policy.POLICY_TICKET); 


        Permission permission = new Permission();
        permission.setTarget("http://example.com/game:4589");
        permission.setActions(Arrays.asList(new Action("http://www.w3.org/ns/odrl/2/play")));

        Constraint c = new Constraint("");
        c.setOperator("http://www.w3.org/ns/odrl/2/lteq");
        c.setRightOperand("http://www.w3.org/ns/odrl/2/dateTime");
        c.setValue("2010-12-31");
        permission.setConstraint(Arrays.asList(c));
        policy.addRule(permission);
        
        
        //We serialize the policy
        String rdf=ODRLRDF.getRDF(policy,Lang.TTL);
        System.out.println(rdf);

    }
}
