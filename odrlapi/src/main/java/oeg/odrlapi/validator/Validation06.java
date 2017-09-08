package oeg.odrlapi.validator;

import java.util.ArrayList;
import oeg.odrlapi.rdf.RDFSugar;
import oeg.odrlapi.rdf.RDFUtils;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import static org.apache.jena.vocabulary.RDF.Statement;

/**
 * Validation of logical constraints.
 * 
 * @author vroddon
 */
public class Validation06 implements Validation {

    @Override
    public ValidatorResponse validate(String turtle) {
        Model model = RDFSugar.getModel(turtle);
        
        
        ResIterator ri = model.listSubjectsWithProperty(RDF.type, ODRL.RLOGICALCONSTRAINT);
        while (ri.hasNext())
        {
            Resource r = ri.next();
            Model racimopadre = RDFUtils.getRacimoExceptProperty(model, r, new ArrayList());
    //        System.out.println("RACIMO--" + RDFUtils.getString(racimopadre) + "--FIN DEL RACIMO");
            StmtIterator si = racimopadre.listStatements(new SimpleSelector(null, RDF.first, (RDFNode)null));
            while (si.hasNext())
            {
                Statement st = si.next();
                RDFNode node = st.getObject();
                if (node.isLiteral())
                   return new ValidatorResponse(false, 415, "not valid. All of the operand values must be unique Constraint instances (URI)."); 
                Resource referenciada = node.asResource();
                NodeIterator nit = model.listObjectsOfProperty(referenciada, RDF.type);
                int cuenta=0;
                while(nit.hasNext())
                {
                    Resource tipo = nit.next().asResource();
                    if (tipo.getURI().equals(ODRL.RCONSTRAINT.getURI()))
                        cuenta++;
                }
                if (cuenta==0)
                   return new ValidatorResponse(false, 415, "not valid. All of the operand values must be unique Constraint instances."); 
                    
            }
        }

        return new ValidatorResponse(true, 200, "valid");
    }
    
    
}
