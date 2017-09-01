package oeg.odrlapi.validator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.ArrayList;
import java.util.List;
import oeg.odrlapi.rdf.RDFUtils;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;

/**
 * Determines if there is at least one policy
 * @author vroddon
 */
public class Validation01 implements Validation {

    @Override
    public ValidatorResponse validate(String turtle) {
        Model model = ODRLValidator.getModel(turtle);
        if (model==null)
            return new ValidatorResponse(false, 415,"The input could not be parsed as RDF Turtle, RDF/XML or NTRIPLES..." );
        
        List<String> lista = new ArrayList();
        
        lista.addAll(RDFUtils.getObjectsOfType(model, ODRL.RPOLICY));
        lista.addAll(RDFUtils.getObjectsOfType(model, ODRL.RAGREEMENT));
        lista.addAll(RDFUtils.getObjectsOfType(model, ODRL.ROFFER));
        lista.addAll(RDFUtils.getObjectsOfType(model, ODRL.RSET));
        
        if (lista.isEmpty())
            return new ValidatorResponse(false, 415,"There is no policy, aggrement, offer or set" );
        
        return new ValidatorResponse(true, 200, "ok");
    }
    
}
