package oeg.jodrlapi.odrlmodel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

/**
 *
 * @author vroddon
 */
public class ResourceModel {
    public ResourceModel(Resource r, Model m)
    {
        resource = r;
        model = m;
    }
    public Resource resource = null;
    public Model model = null;
    
}
