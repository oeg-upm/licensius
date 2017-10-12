package oeg.odrlapi.validator;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import oeg.odrlapi.rdf.RDFSugar;
import oeg.odrlapi.rdf.TestSPARQL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.update.UpdateAction;

/**
 * Transformation N7.
 * Inferences derived from odrl:implies[edit] 
 * This transformation transforms every Permission or Duty including the action α by
 * adding the property-value odrl:action β for every α where α odrl:implies β .
 * If an Action α implies another Action β (α odrl:implies β), a Prohibition of
 * β conflicts a Permission of α, but not necessarily vice versa (i.e., a
 * Prohibition of α is not in conflict with a Permission of β). * It is
 * implemented as a SPARQL Update query
 *
 * @author vroddon
 */
public class TransformationN7 implements Transformation {

    @Override
    public Model transform(Model model) throws Exception {
        try{
            InputStream stream = TestSPARQL.class.getResourceAsStream("/transformation7.sparql");
            String sparqlstr = new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
            UpdateAction.parseExecute(sparqlstr, model );
            return model;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }   
    // Test method
    public static void main(String args[])
    {
        
    }
    
    
    
}
