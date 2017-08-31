package oeg.odrlapi.rest.server.resources;

import java.util.HashMap;
import java.util.Map;

/**
 * Evaluator response
 * @author vroddon
 */
public class EvaluatorResponse {

    public Map<String, String> results = new HashMap();
    
    public Map<String, String> getResults() {
        return results;
    }

    public void setResults(Map<String, String> mapa) {
        this.results = mapa;
    }
   
}
