package oeg.odrlapi.validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        
        List<Resource> oldrules = new ArrayList();    //rules to be deleted
        Map<Resource, Resource> map = new HashMap();
        
        
        StmtIterator it = model.listStatements(null, ODRL.PISREPLACEDBY, (RDFNode)null);
        while (it.hasNext()) {
            Statement stmt = it.nextStatement();
            RDFNode nodop1 = stmt.getSubject();
            RDFNode nodop2 = stmt.getObject();
            Resource oldpolicy = nodop1.asResource();
            Resource newpolicy = nodop2.asResource();
            
            
            map.put(oldpolicy, newpolicy);
            
            StmtIterator it2 = model.listStatements(oldpolicy, ODRL.POBLIGATION, (RDFNode)null);
            while(it2.hasNext())
            {
                Statement stmt2 = it2.nextStatement();
                oldrules.add(stmt2.getObject().asResource());
            }
            it2 = model.listStatements(oldpolicy, ODRL.PPERMISSION, (RDFNode)null);
            while(it2.hasNext())
            {
                Statement stmt2 = it2.nextStatement();
                oldrules.add(stmt2.getObject().asResource());
            }
            
            it2 = model.listStatements(oldpolicy, ODRL.PPROHIBITION, (RDFNode)null);
            while(it2.hasNext())
            {
                Statement stmt2 = it2.nextStatement();
                oldrules.add(stmt2.getObject().asResource());
            }
            

        }

        //Quitamos las oldrules
        for(Resource vieja : oldrules)
        {
            Model racimo = RDFUtils.getRacimo(model, vieja, new ArrayList<Resource>());
            model.remove(racimo);

            List<Statement> aborrar = new ArrayList();
            StmtIterator si = model.listStatements(null, null, vieja);
            while(si.hasNext())
            {
                Statement s = si.nextStatement();
                aborrar.add(s);
            }
            for(Statement ab : aborrar)
            {
                model.remove(ab);
            }
        }

        //Para cada nueva vieja ponemos las reglas nuevas
        it = model.listStatements(null, ODRL.PISREPLACEDBY, (RDFNode)null);
        while (it.hasNext()) {
            Statement stmt = it.nextStatement();
            Resource oldpolicy = stmt.getSubject().asResource();            
            Resource newpolicy = map.get(oldpolicy);
            if (newpolicy==null)
                continue;
            String rdfnuevo = RDFUtils.browseSemanticWeb(newpolicy.toString());
            Model model2 = RDFSugar.getModel(rdfnuevo);
            
            StmtIterator it2 = model2.listStatements(newpolicy, ODRL.PPERMISSION, (RDFNode)null);
            while(it2.hasNext())
            {
                Statement stmt2 = it2.nextStatement();
                Resource obj = stmt2.getObject().asResource();
                Model racimo = RDFUtils.getRacimo(model2, obj, new ArrayList<Resource>());
                model.add(racimo);
                model.add(oldpolicy,ODRL.PPERMISSION,obj);
            }
            
        }
        
        return model;
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
