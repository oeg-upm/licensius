package oeg.licensius.model;

import org.json.simple.JSONObject;

/**
 * Simple response given by the Licensius server
 * @author vrodriguez
 */
public class LicensiusSimple implements LicensiusResponse {

    public String text ="unknown";
    public String json =""; 
    
    @Override
    public String getJSON() {
        if (!json.isEmpty())
            return json;
        return JSONObject.escape(text);
    }
    
}
