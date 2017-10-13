package oeg.odrlapi.validator;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import oeg.odrlapi.rdf.RDFSugar;
import oeg.odrlapi.rdf.TestSPARQL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.update.UpdateAction;

/**
 * Transformation N6
 * This transformation transforms every Rule including the action β by adding the property-value odrl:action 
 * α for every α where α odrl:includedIn β .
 * 
 * It is implemented as a SPARQL Update query
 * @author vroddon
 */
public class TransformationN06 implements Transformation {

    @Override
    public Model transform(Model model) throws Exception {
        try{
            InputStream stream = TestSPARQL.class.getResourceAsStream("/sparql/transformation6.sparql");
            String sparqlstr = new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
            UpdateAction.parseExecute(sparqlstr, model );
//            model.write( System.out, "TTL" );
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
