package examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import odrlmodel.Action;
import odrlmodel.Constraint;
import odrlmodel.Duty;
import odrlmodel.ODRLRDF;
import odrlmodel.Party;
import odrlmodel.Permission;
import odrlmodel.Policy;
import odrlmodel.Prohibition;
import org.apache.jena.riot.Lang;

/**
 * This class shows a very simple example creating a simple ODRL expression
 * @see http://www.w3.org/ns/odrl/2/#sec-example-3
 * @author Victor
 */
public class Example3 {
    
    /**
     * @param args No arguments are needed
     */
    public static void main(String[] args) {
        
        //We create a policy. It can be anonymous, or it can receive a name (URI)
        Policy policy = new Policy("http://example.com/policy:9001");
        policy.setType(Policy.POLICY_AGREEMENT); 

        Duty payment = new Duty("http://example.com/requirements");
        payment.setActions(Arrays.asList(new Action("http://www.w3.org/ns/odrl/2/pay")));
        payment.setTarget("http://example.com/ubl:AUD0.50");

        Permission permission = new Permission();
        permission.setTarget("http://example.com/music:4545");
        permission.setAssigner(new Party("http://example.com/sony:10"));
        permission.setAssignee(new Party("http://example.com/billie:888"));
        permission.setActions(Arrays.asList(new Action("http://www.w3.org/ns/odrl/2/play")));
        permission.setDuty(payment);
        policy.addRule(permission);

        Permission permission2 = new Permission();
        permission2.setTarget("http://example.com/music:4545");
        permission2.setAssigner(new Party("http://example.com/sony:10"));
        permission2.setAssignee(new Party("http://example.com/billie:888"));
        permission2.setActions(Arrays.asList(new Action("http://www.w3.org/ns/odrl/2/copy")));
        permission2.setDuty(payment);

        Constraint c = new Constraint("");
        c.setOperator("http://www.w3.org/ns/odrl/2/lteq");
        c.setRightOperand("http://www.w3.org/ns/odrl/2/count");
        c.setValue("1");
        permission2.setConstraints(Arrays.asList(c));
        policy.addRule(permission2);
        
        
        
        //We serialize the policy
        String rdf=ODRLRDF.getRDF(policy,Lang.TTL);
        System.out.println(rdf);

    }
}
