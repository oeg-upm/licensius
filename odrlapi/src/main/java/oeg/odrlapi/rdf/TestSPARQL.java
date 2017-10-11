package oeg.odrlapi.rdf;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.update.UpdateAction;

/**
 * Method to test SPARQL queries. 
 * Ideally, transformationS should be able to be expressed as SPARQL queries. 
 * @author vroddon
 */
public class TestSPARQL {
    public static void main(String args[])
    {
        try{
            int i = 78;
            String testfile = String.format("http://odrlapi.appspot.com/samples/sample%03d",i);
            String rdf = new Scanner(new URL(testfile).openStream(), "UTF-8").useDelimiter("\\A").next();
            System.out.println(rdf);
            Model model = RDFSugar.getModel(rdf);
            
            InputStream stream = TestSPARQL.class.getResourceAsStream("/transformations.sparql");
            String sparqlstr = new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
            System.out.println(sparqlstr);

      //      Query query = QueryFactory.create(sparqlstr);
      //      QueryExecution qe = QueryExecutionFactory.create(query, model);
      //      Model resultModel = qe.execConstruct() ;
            
            UpdateAction.parseExecute(sparqlstr, model );
            model.write( System.out, "TTL" );

//            ResultSet results = qe.execSelect();            
//            ResultSetFormatter.out(System.out, results, query);
//            qe.close();            
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
}
