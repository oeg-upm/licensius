package observatorio.rascaweb;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

 
/**
 *
 * @author vroddon
 */
public class RDFParser {

    List<String> lrs = null;
    
    /**
     * Busca archivos RDF usando la API de Google. Pero esta es de pago...
     */
  
    
    public static void main(String[] args) {

        RDFParser parser = new RDFParser();
        parser.init();
//        parser.parse("./data/telegraphisvoid.n3");
    }

    //http://data.archiveshub.ac.uk/def/accessRestrictions
    //http://data.archiveshub.ac.uk/def/useRestrictions
    public void init()
    {
        lrs  = new ArrayList();
        lrs.add("http://creativecommons.org/ns#license");
        lrs.add("http://purl.org/dc/terms/license");
        lrs.add("http://web.resource.org/cc/license");
    }
    
    
    
    public void parse(String inputFileName) {
        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + inputFileName + " not found");
        }

// read the RDF/XML file
        model.read(in, null,"TURTLE");
        StmtIterator iter = model.listStatements();

// print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();  // get next statement
            Resource subject = stmt.getSubject();     // get the subject
            Property predicate = stmt.getPredicate();   // get the predicate
            RDFNode object = stmt.getObject();      // get the object

            String sns=subject.getNameSpace();
        //    System.out.println("NAMESPACE " + sns);
          //  System.out.println(subject.toString() + " " + predicate.toString() + " " + object.toString());
            if (object instanceof Resource) {
       //         System.out.print(object.toString());
            } else {
                // object is a literal
       //         System.out.print(" \"" + object.toString() + "\"");
            }
      //      System.out.println(" .");

            for(String candidate : lrs)
            {
                if (predicate.toString().equals(candidate))
                {
                    System.out.println("GREAT! Detected licensed material using the term: " + candidate );
                    System.out.println("License was: " + object.toString() );
                }
            }
        }
    }
}
