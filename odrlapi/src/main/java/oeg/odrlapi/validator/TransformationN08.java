
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
 * Normalization step 8.
 * This transformation changes every Rule including the Asset β with the property odrl:target by adding the property-value odrl:target α for every α where α odrl:partOf β .
 * @author Victor
 */
public class TransformationN08 implements Transformation {
    @Override
    public Model transform(Model model) throws Exception {
        try{
            InputStream stream = TestSPARQL.class.getResourceAsStream("/sparql/transformation8.sparql");
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
            int i = 80;
            String testfile = String.format("http://odrlapi.appspot.com/samples/sample%03d",i);
            String rdf = new Scanner(new URL(testfile).openStream(), "UTF-8").useDelimiter("\\A").next();
            System.out.println(rdf);
            
            Model model = RDFSugar.getModel(rdf);
            TransformationN08 t = new TransformationN08();
            model = t.transform(model);
            
            System.out.println("===================================================");
            
            String rdf2 = RDFUtils.getString(model);
            System.out.println(rdf2);
        }catch(Exception e){
            e.printStackTrace();
        }                
    }    
}
