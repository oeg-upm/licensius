package oeg.odrlapi.validator;

import org.apache.jena.rdf.model.Model;

/**
 * Transformation made to a ODRL Policy.
 * The meaning of the resulting policy must be preserved.
 * @author vroddon
 */
public interface Transformation {
    
    public Model transform(Model model) throws Exception;
    
}
