package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.List;
import oeg.odrlapi.rdf.RDFSugar;
import oeg.odrlapi.rdf.RDFUtils;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Permissions cannot have more than one assigner or assignee.
 * @todo REPLACE THIS BY A SHACL SHAPE
 * @author vroddon
 */
public class Validation04 implements Validation {

    @Override
    public ValidatorResponse validate(String turtle) {
        Model model = RDFSugar.getModel(turtle);
        if (model==null)
            return new ValidatorResponse(false, 415,"not valid. The input could not be parsed as RDF Turtle, JSON-LD,  RDF/XML or NTRIPLES..." );
        ValidatorResponse vr = new ValidatorResponse(true, 200,"valid");
        
        List<Resource> lista = new ArrayList();
        lista.addAll(RDFUtils.getRSubjectOfType(model, ODRL.RPERMISSION));
        for(Resource p : lista)
        {
            StmtIterator si = p.listProperties(ODRL.PASSIGNER);
            int cuenta = 0;
            while (si.hasNext()) {
                Statement res = si.next();
                cuenta++;
                if (cuenta>=2)
                {
                    vr.setText("warning. There must be at most 1 assigner. Under CWA, this is not valid");
                }
            }
        }
        
        lista = new ArrayList();
        lista.addAll(RDFUtils.getRSubjectOfType(model, ODRL.RPERMISSION));
        for(Resource p : lista)
        {
            StmtIterator si = p.listProperties(ODRL.PASSIGNEE);
            int cuenta = 0;
            while (si.hasNext()) {
                Statement res = si.next();
                cuenta++;
                if (cuenta>=2)
                {
                    vr.setText("warning. There must be at most 1 assignee. Under CWA, this is not valid");
                }
            }
        }
       
        
        
        return vr;
        
    }
    
}
