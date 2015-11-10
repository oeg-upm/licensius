package oeg.licensius.model;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

/**
 *
 * @author vrodriguez
 */
public class LicensiusFound implements LicensiusResponse {

    List<String> licenses = new ArrayList();
    List<String> confidences = new ArrayList();
    List<String> evidences = new ArrayList();
    
    public LicensiusFound()
    {
        
    }
    
    @Override
    public String getJSON() {

    String json ="[";    
    
    int n = licenses.size();
    for(int i =0;i<n;i++)
    {
        String license = JSONObject.escape(licenses.get(i));
        String confidence = JSONObject.escape(confidences.get(i));
        String evidence = JSONObject.escape(evidences.get(i));
        String found ="  {\n" +
        "    \"license\": \""+license+ "\",\n" +
        "    \"evidence\": \""+evidence+ "\",\n" +
        "    \"confidence\": \""+confidence+"\"\n" +
        "  }\n" +
        "";
        json+=found+",\n";
    }
    json+="]";
    return json;
    }

    public void add(String licencia, String evidencia, String prob) {
        licenses.add(licencia);
        evidences.add(evidencia);
        confidences.add(prob);
    }
}
