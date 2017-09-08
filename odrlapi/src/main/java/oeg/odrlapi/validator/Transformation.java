package oeg.odrlapi.validator;

import org.apache.jena.rdf.model.Model;

/**
 * Transformation made on a ODRL Policy.
 * @author vroddon
 */
public interface Transformation {
    
    public Model transform(Model model) throws Exception;
    
}
