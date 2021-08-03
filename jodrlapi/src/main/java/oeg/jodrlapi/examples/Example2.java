package oeg.jodrlapi.examples;

import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.odrlmodel.*;


import java.util.Arrays;
import org.apache.jena.riot.Lang;

/**
 * This class shows a simple example creating a not-so-simple ODRL expression, with a Payment and a count constraint.
 * Shows how to serialize has JSON-LD.
 * @author Victor
 */
public class Example2 {
    
    /**
     * @param args No arguments are needed
     */
    public static void main(String[] args) {
        
        //We create a policy. It can be anonymous, or it can receive a name (URI)
        Policy policy = new Policy("http://example.com/policy0231");
        policy.setTypeofpolicy(Policy.POLICY_OFFER); 

        Duty payment = new Duty("http://example.com/requirements");
        payment.setActions(Arrays.asList(new Action("pay")));
        payment.setTarget("http://example.com/ublAUD0.50");

        Permission permission = new Permission();
        permission.setTarget("http://example.com/music4545");
        permission.setAssigner(new Party("http://example.com/sony10"));
        permission.setActions(Arrays.asList(new Action("play")));
        permission.setDuty(payment);
        policy.addRule(permission);

        Permission permission2 = new Permission();
        permission2.setTarget("http://example.com/music4545");
        permission2.setAssigner(new Party("http://example.com/sony:10"));
        permission2.setActions(Arrays.asList(new Action("copy")));
        permission2.setDuty(payment);
        Constraint c = new Constraint("");
        c.setOperator("http://www.w3.org/ns/odrl/2/lteq");
        c.setLeftOperand("http://www.w3.org/ns/odrl/2/count");
        c.setRightOperand("1");
        permission2.setConstraint(Arrays.asList(c));
        policy.addRule(permission2);

        //We serialize the policy as JSON-LD
        String rdf=ODRLRDF.getRDF(policy,Lang.JSONLD);
        System.out.println(rdf);
        
        //We serialize the policy as an alternative JSON-LD. Based on JACKSON.
        System.out.println("\n"+policy.toJSONLD());

    }
}
