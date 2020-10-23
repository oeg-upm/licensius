package oeg.jodrlapi.examples;

import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.odrlmodel.*;

import java.util.List;
import org.apache.log4j.BasicConfigurator;

/**
 * We demonstrate how to read a remote license
 * @author Victor
 */
public class Example6 {

    /**
     * Reads remote licenses, like for example: http://rdflicense.appspot.com/rdflicense/cc-by4.0
     * @param args No arguments are needed
     */
    public static void main(String[] args) {
        
       BasicConfigurator.configure();
       
       List<Policy> policies = ODRLRDF.load("http://rdflicense.appspot.com/rdflicense/cc-by4.0");
       for(Policy policy : policies)
       {
           System.out.println(policy.toString());
       }
    }
    
}
