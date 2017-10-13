package oeg.odrlapi.validator;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import oeg.odrlapi.rdf.RDFSugar;
import oeg.odrlapi.rdf.RDFUtils;
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
public class TransformationN07 implements Transformation {

    @Override
    public Model transform(Model model) throws Exception {
        try{
            InputStream stream = TestSPARQL.class.getResourceAsStream("/sparql/transformation7.sparql");
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

        try{
            int i = 79;
            String testfile = String.format("http://odrlapi.appspot.com/samples/sample%03d",i);
            String rdf = new Scanner(new URL(testfile).openStream(), "UTF-8").useDelimiter("\\A").next();
            System.out.println(rdf);
            
            Model model = RDFSugar.getModel(rdf);
            TransformationN07 t = new TransformationN07();
            model = t.transform(model);
            
            System.out.println("===================================================");
            
            String rdf2 = RDFUtils.getString(model);
            System.out.println(rdf2);
        }catch(Exception e){
            e.printStackTrace();
        }           
    }
    
    
    
}
