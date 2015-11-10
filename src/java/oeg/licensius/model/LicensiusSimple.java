
package oeg.licensius.model;

import org.json.simple.JSONObject;

/**
 *
 * @author vrodriguez
 */
public class LicensiusSimple implements LicensiusResponse {

    public String text ="unknown";
    
    @Override
    public String getJSON() {
        return JSONObject.escape(text);
    }
    
}
