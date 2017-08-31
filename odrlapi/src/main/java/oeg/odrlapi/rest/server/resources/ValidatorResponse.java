package oeg.odrlapi.rest.server.resources;

//import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;


/**
 * This is the response of the validator
 *
 * @author vroddon
 */
public class ValidatorResponse {

    public Boolean valid;
    public String text;
    public int status = 200;

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

   /* public String toString() {
        String s = "error";
        ObjectMapper mapperObj = new ObjectMapper();
        try {
            s = mapperObj.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
*/
}
