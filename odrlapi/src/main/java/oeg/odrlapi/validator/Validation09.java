package oeg.odrlapi.validator;

import oeg.odrlapi.rdf.RDFSugar;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

/**
 * A remedy must not refer to a Duty that includes a consequence Duty.
 * @author vroddon
 */
public class Validation09 implements Validation {

    @Override
    public ValidatorResponse validate(String turtle) {
        Model model = RDFSugar.getModel(turtle);
        NodeIterator it = model.listObjectsOfProperty(ODRL.RREMEDY);
        while(it.hasNext())
        {
            RDFNode nodo = it.next();
            if (!nodo.isResource())
                continue;
            Resource r = nodo.asResource();
            NodeIterator ni = model.listObjectsOfProperty(r,ODRL.RCONSEQUENCE);
            if (ni.hasNext())
                return new ValidatorResponse(false,415, "A remedy must not refer to a Duty that includes a consequence Duty.");
        }
        return new ValidatorResponse(true, 200, "valid");
    }
    
}
