package oeg.jodrlapi.examples;

import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.odrlmodel.*;

import java.util.Arrays;
import org.apache.jena.riot.Lang;

/**
 * This class shows a different examples for the creation of other ODRL policies.
 * @author Victor
 */
public class Example3 {
    
    /**
     * @param args No arguments are needed
     */
    public static void main(String[] args) {
        policy3();
        policy4();
        policy5();
    }
    
    /**
     * This method shows a very simple example creating a simple ODRL request policy with different permisisons. 
     */
    public static void policy3()
    {
        
        //We create a policy. It can be anonymous, or it can receive a name (URI)
        Policy policy = new Policy("http://example.com/policy9001");
        policy.setTypeofpolicy(Policy.POLICY_AGREEMENT); 

        Duty payment = new Duty("http://example.com/requirements");
        payment.setActions(Arrays.asList(new Action("http://www.w3.org/ns/odrl/2/pay")));
        payment.setTarget("http://example.com/ublAUD0.50");

        Permission permission = new Permission();
        permission.setTarget("http://example.com/music4545");
        permission.setAssigner(new Party("http://example.com/sony10"));
        permission.setAssignee(new Party("http://example.com/billie888"));
        permission.setActions(Arrays.asList(new Action("http://www.w3.org/ns/odrl/2/play")));
        permission.setDuty(payment);
        policy.addRule(permission);

        Permission permission2 = new Permission();
        permission2.setTarget("http://example.com/music:4545");
        permission2.setAssigner(new Party("http://example.com/sony10"));
        permission2.setAssignee(new Party("http://example.com/billie888"));
        permission2.setActions(Arrays.asList(new Action("http://www.w3.org/ns/odrl/2/copy")));
        permission2.setDuty(payment);

        Constraint c = new Constraint("");
        c.setOperator("http://www.w3.org/ns/odrl/2/lteq");
        c.setLeftOperand("http://www.w3.org/ns/odrl/2/count");
        c.setRightOperand("1");
        permission2.setConstraint(Arrays.asList(c));
        policy.addRule(permission2);
        
        //We serialize the policy
        String rdf=ODRLRDF.getRDF(policy,Lang.TTL);
        System.out.println(rdf);
    }
    
    /**
     * This method shows a very simple example creating a simple ODRL request policy. 
     */
    public static void policy4()
    {

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
    /**
     *  This method shows a very simple example creating a simple ODRL expression with a time constraint.
     */
    public static void policy5()
    {
        
        //We create a policy. It can be anonymous, or it can receive a name (URI)
        Policy policy = new Policy("http://example.com/policy0811");
        policy.setTypeofpolicy(Policy.POLICY_TICKET); 


        Permission permission = new Permission();
        permission.setTarget("http://example.com/game4589");
        permission.setActions(Arrays.asList(new Action("http://www.w3.org/ns/odrl/2/play")));

        Constraint c = new Constraint("");
        c.setOperator("http://www.w3.org/ns/odrl/2/lteq");
        c.setLeftOperand("http://www.w3.org/ns/odrl/2/dateTime");
        c.setRightOperand("2010-12-31");
        permission.setConstraint(Arrays.asList(c));
        policy.addRule(permission);
        
        
        //We serialize the policy
        String rdf=ODRLRDF.getRDF(policy,Lang.TTL);
        System.out.println(rdf);
    }
    
}
