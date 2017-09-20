package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.List;
import oeg.odrlapi.rdf.RDFSugar;
import oeg.odrlapi.rdf.RDFUtils;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.apache.jena.rdf.model.Model;

/**
 * Determines if there is at least one policy.
 * Result: Warning. There is no policy, aggrement, offer or set to evaluate.
 * @author vroddon
 */
public class Validation01 implements Validation {

    @Override
    public ValidatorResponse validate(String turtle) {
        Model model = RDFSugar.getModel(turtle);
        if (model==null)
            return new ValidatorResponse(false, 415,"not valid. The input could not be parsed as RDF Turtle, JSON-LD,  RDF/XML or NTRIPLES." );
        List<String> lista = new ArrayList();
        
        lista.addAll(RDFUtils.getSubjectOfType(model, ODRL.RPOLICY));
        lista.addAll(RDFUtils.getSubjectOfType(model, ODRL.RAGREEMENT));
        lista.addAll(RDFUtils.getSubjectOfType(model, ODRL.ROFFER));
        lista.addAll(RDFUtils.getSubjectOfType(model, ODRL.RSET));
        
        if (lista.isEmpty())
            return new ValidatorResponse(true, 200,"warning. There is no policy, aggrement, offer or set to evaluate." );
        
        return new ValidatorResponse(true, 200, "ok");
    }
    
}
