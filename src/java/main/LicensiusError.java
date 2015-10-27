package main;

/**
 *
 * @author vrodriguez
 */
public class LicensiusError implements LicensiusResponse {
    
    int code = -1;
    String  message = "unknown";

    public LicensiusError()
    {
        
    }
    public LicensiusError(int _code, String _message)
    {
        code = _code;
        message = _message;
    }
    
    @Override
    public String getJSON() {
            String json = "{\"code\": "+ code +",\n" +
            "  \"message\": \""+ message +"\"\n}";
            return json;
    }
    
}
