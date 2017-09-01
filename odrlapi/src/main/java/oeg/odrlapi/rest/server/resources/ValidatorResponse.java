package oeg.odrlapi.rest.server.resources;

import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;

 
/**
 * This is the response of the validator.
 * @param valid Valid or not
 * @param status HTTP status
 * @param text Text describing the error
 * @author vroddon
 */
public class ValidatorResponse {

    public Boolean valid;
    public String text;
    public int status = 200;

    public ValidatorResponse()
    {
        
    }
    
    public ValidatorResponse(Boolean _valid, int _status, String _text)
    {
        text = _text;
        status = _status;
        valid = _valid;
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String toString() {
        String s = "error";
        ObjectMapper mapperObj = new ObjectMapper();
        try {
            s = mapperObj.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

}
