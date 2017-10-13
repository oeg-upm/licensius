package oeg.odrlapi.validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import oeg.odrlapi.rdf.RDFSugar;
import oeg.odrlapi.rdf.RDFUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Transformation N10.
 * No transitivity is applied. No sorting is made.
 * @author Victor
 */
public class TransformationN10 implements Transformation {
    @Override
    public Model transform(Model model) throws Exception {
        
        List<Resource> viejas = new ArrayList();
        
        StmtIterator it = model.listStatements(null, ODRL.PISREPLACEDBY, (RDFNode)null);
        while (it.hasNext()) {
            Statement stmt = it.nextStatement();
            RDFNode nodop1 = stmt.getSubject();
            RDFNode nodop2 = stmt.getObject();
            
            StmtIterator it2 = model.listStatements(null, ODRL.POBLIGATION, (RDFNode)null);
            while(it2.hasNext())
            {
                Statement stmt2 = it2.nextStatement();
                viejas.add(stmt2.getObject().asResource());
            }
            it2 = model.listStatements(null, ODRL.PPERMISSION, (RDFNode)null);
            while(it2.hasNext())
            {
                Statement stmt2 = it2.nextStatement();
                viejas.add(stmt2.getObject().asResource());
            }
            
            it2 = model.listStatements(null, ODRL.PPROHIBITION, (RDFNode)null);
            while(it2.hasNext())
            {
                Statement stmt2 = it2.nextStatement();
                viejas.add(stmt2.getObject().asResource());
            }
            
        }

        for(Resource vieja : viejas)
        {
            System.out.println(vieja.toString());
            Model racimo = RDFUtils.getRacimo(model, vieja, new ArrayList<Resource>());
            model.remove(racimo);
        }
        
        
        
        return null;
    }
    // Test method
    public static void main(String args[])
    {
        try{
            int i = 82;
            String testfile = String.format("http://odrlapi.appspot.com/samples/sample%03d",i);
            String rdf = new Scanner(new URL(testfile).openStream(), "UTF-8").useDelimiter("\\A").next();
            rdf = "@prefix dc: <http://purl.org/dc/terms/> .\n"+rdf;
            System.out.println(rdf);
             
            Model model = RDFSugar.getModel(rdf);
            TransformationN10 t = new TransformationN10();
            model = t.transform(model);
            
            System.out.println("===================================================");
            
            String rdf2 = RDFUtils.getString(model);
            System.out.println(rdf2);
        }catch(Exception e){
            e.printStackTrace();
        }                
    }        
}
