
package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.List;
import oeg.odrlapi.rdf.RDFUtils;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;

/**
 * Warning for same class Offer/Agreement/Set
 * @author vroddon
 */
public class Validation03 implements Validation {

    @Override
    public ValidatorResponse validate(String turtle) {
        Model model = ODRLValidator.getModel(turtle);
        if (model==null)
            return new ValidatorResponse(false, 415,"not valid. The input could not be parsed as RDF Turtle, RDF/XML or NTRIPLES..." );
        
        List<String> lista = Preprocessing.getPoliticas(model);
        for(String politica : lista)
        {
            List<String> clases = RDFUtils.getObjectsGivenSP(model, politica, RDF.type.getURI());
            int cuenta=0;
            for(String clase : clases)
            {
                if (clase.equals(ODRL.RAGREEMENT.getURI()))
                    cuenta++;
                if (clase.equals(ODRL.ROFFER.getURI()))
                    cuenta++;
                if (clase.equals(ODRL.RSET.getURI()))
                    cuenta++;
            }
            if (cuenta>1)
                return new ValidatorResponse(true, 200,"Warning: agreements, offers and sets are incompatibles" );
        }
        
        if (lista.isEmpty())
            return new ValidatorResponse(false, 415,"not valid. There is no policy, aggrement, offer or set" );
        
        return new ValidatorResponse(true, 200, "ok");    }
    
}
