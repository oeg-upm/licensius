package oeg.odrlapi.validator;

import oeg.odrlapi.rest.server.resources.ValidatorResponse;

/**
 * Superclass of all the validations
 * @author vroddon
 */
public interface Validation {
    
    public abstract ValidatorResponse validate(String turtle);
}
