package oeg.odrlapi.validator;

import oeg.odrlapi.rdf.RDFSugar;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

/**
 * Searches for AssetCollections with a refinement that do not include a odrl:source property.
 * "Using the refinement property, the uid property must not be used to identify the AssetCollection. 
 * Instead, the source property must be used to reference the AssetCollection."
 * @todo REPLACE THIS BY A SHACL SHAPE
 * @author vroddon
 */
public class Validation08 implements Validation {

    @Override
    public ValidatorResponse validate(String turtle) {
        Model model = RDFSugar.getModel(turtle);
        
        ResIterator ri  = model.listSubjectsWithProperty(RDF.type, ODRL.RASSETCOLLECTION);
        while(ri.hasNext())
        {
            Resource r = ri.next();
            NodeIterator it = model.listObjectsOfProperty(r, ODRL.PREFINEMENT);
            if (it.hasNext()) //El recurso tiene REFINEMENT --> DEBE TENER UN ODRL SOURCE
            {
                NodeIterator it2 = model.listObjectsOfProperty(r, ODRL.PSOURCE);
                if (it2.hasNext())
                {
                    continue;
                }else
                {
                    return new ValidatorResponse(false, 415, "not valid. The AssetCollection must have a odrl:source if using a refine.");
                }
            }
        }
        
        ri  = model.listSubjectsWithProperty(RDF.type, ODRL.RPARTYCOLLECTION);
        while(ri.hasNext())
        {
            Resource r = ri.next();
            NodeIterator it = model.listObjectsOfProperty(r, ODRL.PREFINEMENT);
            if (it.hasNext()) //El recurso tiene REFINEMENT --> DEBE TENER UN ODRL SOURCE
            {
                NodeIterator it2 = model.listObjectsOfProperty(r, ODRL.PSOURCE);
                if (it2.hasNext())
                {
                    continue;
                }else
                {
                    return new ValidatorResponse(false, 415, "not valid. The PartyCollection must have a odrl:source if using a refine.");
                }
            }
        }        
        return new ValidatorResponse(true, 200, "valid");
    }
    
}
