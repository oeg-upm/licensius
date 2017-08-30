package examples;

import java.util.List;
import odrlmodel.ODRLRDF;
import odrlmodel.Policy;
import org.apache.jena.riot.Lang;

/**
 * This example shows how to read a file with a Policy and load it into memory.
 * Then, it is serialized again.
 * @author Victor
 */
public class Example1modified {
    
    /**
     * @param args No arguments are needed
     */
    public static void main(String[] args) {
        List<Policy> policies = ODRLRDF.load("./doc/samplepolicies/policy1.ttl");
        for(Policy policy : policies)
        {
            String s2=ODRLRDF.getRDF(policy, Lang.TTL);
            System.out.println(s2);
        }
    }
    
}
