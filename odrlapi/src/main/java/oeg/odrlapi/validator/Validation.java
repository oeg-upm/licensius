
package oeg.odrlapi.validator;

import oeg.odrlapi.rest.server.resources.ValidatorResponse;

/**
 *
 * @author vroddon
 */
public interface Validation {
    
    public abstract ValidatorResponse validate(String turtle);
}
