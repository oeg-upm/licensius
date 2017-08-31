package oeg.odrlapi.rest.server.resources;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vroddon
 */
public class EvaluatorResponse {

    public Map<String, String> getMapa() {
        return mapa;
    }

    public void setMapa(Map<String, String> mapa) {
        this.mapa = mapa;
    }
    public Map<String, String> mapa = new HashMap();
    
    
   
}
