package main;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vrodriguez
 */
public class LicensesFound implements LicensiusResponse {

    List<String> licenses = new ArrayList();
    List<String> confidences = new ArrayList();
    List<String> evidences = new ArrayList();
    
    public LicensesFound()
    {
        
    }
    
    @Override
    public String getJSON() {

        
    String json ="[";    
    
    int n = licenses.size();
    for(int i =0;i<n;i++)
    {
        String license = licenses.get(i);
        String confidence = confidences.get(i);
        String evidence = evidences.get(i);
        
        String found ="  {\n" +
        "    \"license\": {},\n" +
        "    \"evidence\": \""+evidence+ "\",\n" +
        "    \"confidence\": \""+confidence+"\"\n" +
        "  }\n" +
        "";
        json+=found+",\n";
    }
    json+="]";
    return json;
    }
}
