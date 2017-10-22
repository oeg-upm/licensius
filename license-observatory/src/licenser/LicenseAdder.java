package licenser;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

//JAVA
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 *
 * @author Victor
 */
public class LicenseAdder {

    private static Model model = null;

    /**
     * Adds a license to the given dataset
     * @param input URI or filename of a RDF file
     * @param output filename of the output RDF file
     * @return true in case of success
     */
    public static boolean addLicense(String input, String output, String licencia) {
        model = ModelFactory.createDefaultModel();
        try {
            InputStream fin = FileManager.get().open(input);
            if (fin == null) {
                return false;
            }
            FileOutputStream fout = new FileOutputStream(output);
            if (fout == null) {
                return false;
            }

            //WE READ RDF/XML
            model.read(fin, null);

            addLicense(input, licencia);

            model.write(fout);
            fout.close();
            fin.close();
            model.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void addLicense(String input, String licencia) {
        if (licencia.isEmpty())
            licencia="http://creativecommons.org/licenses/by/3.0/";

        Resource subject = model.createResource(input);
        Property property = model.createProperty("http://purl.org/dc/terms/license");
        Object o = model.createLiteral(licencia);
        Statement st = model.createLiteralStatement(subject, property, o);
        model.add(st);
        
    
    }

    private static void test() {
        StmtIterator iter = model.listStatements();

// print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();  // get next statement
            Resource subject = stmt.getSubject();     // get the subject
            Property predicate = stmt.getPredicate();   // get the predicate
            RDFNode object = stmt.getObject();      // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"" );
            }
            System.out.print("\n");


        }
    }

    }
