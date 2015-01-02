package licenser;

//JAVA
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//LOG4J
import org.apache.log4j.Logger;

//JENA
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;

//JSON
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


import vroddon.sw.Modelo;

/**
 * Clase para obtener automáticamente datos del CKAN.
 * - Utiliza la API del CKAN (que puede dejar de funcionar en cualquier momento).
 * La API del CKAN está descrita aquí: http://docs.ckan.org/en/ckan-1.7/using-data-api.html
 *
 * OEG - Diciembre 2012
 * @author Victor
 */
public class CKANDatasets {

    
    
    
    /**
     * Obtiene una lista de datasets leyéndolos del archivo "datasets.txt" en este mismo paquete
     * 
     * @return Lista de strings identificando 
     */
    public static List<String> getPreloadedDatasets(String slocalfile) {
        List<String> ls = new ArrayList();
        try {
            InputStream is = EndPointExplorer.class.getResourceAsStream(slocalfile);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                ls.add(strLine);
            }
            is.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        Logger.getLogger("licenser").info("Precargados " + ls.size() + " datasets");
        return ls;
    }

    public static void main(String[] args) {
        List<String> ls = getAllVOIDURI();

        for (String svoid : ls) {
            String modelo = Modelo.loadXMLRDF("cachevoidckan", svoid);
            boolean ok = true;
            if (!modelo.isEmpty()) {
                try {
                    ok = Modelo.parseFromString(modelo, true);
                    if (!ok)
                        ok = Modelo.parseFromString(modelo, false);
                    if (ok)
                    {
                    boolean b1=isTherePredicate("http://purl.org/dc/terms/rights");
                    boolean b2=isTherePredicate("http://purl.org/dc/terms/license");
                    boolean b3=isTherePredicate("http://creativecommons.org/ns#license");
                    boolean b4=isTherePredicate("http://www.w3.org/1999/xhtml/vocab/‎license");
                    boolean b5=isTherePredicate("http://purl.org/dc/elements/1.1/license");
                    boolean b6=isTherePredicate("http://purl.org/dc/elements/1.1/rights");                        

                        System.out.println("Parsed\t" + svoid +"\t" +b1+"\t" +b2+"\t" +b3+"\t" +b4+"\t" +b5+"\t" +b6);
                    }
            
                } catch (Exception e) {
                    ok = false;
                }
            }
            else 
                ok=false;
            if (!ok)
                System.out.println("Not parsed\t" + svoid);

        }


        //    CKANExplorer.getMiniJSONRapido("http://datahub.io/api/3/search/resource?format=meta%2Fvoid&limit=250&all_fields=1", "void.json");
        //    CKANExplorer.parseLocalJSON("void.json");
        //http://datahub.io/api/3/search/resource?res_format=meta%2Fvoid
    }

    public static List<String> getAllVOIDURI() {
        List<String> uris = new ArrayList();
        //     CKANExplorer.getMiniJSONRapido("http://datahub.io/api/3/search/resource?format=meta%2Fvoid&limit=250&all_fields=1", "void.json");
        try {
            BufferedReader br = new BufferedReader(new FileReader("void.json"));
            String output = br.readLine();
            if (output == null) {
                return uris;
            }
            JSONObject obj = (JSONObject) JSONValue.parse(output);
            JSONArray array = (JSONArray) obj.get("results");
//            String count = (String) obj.get("count");
//            JSONArray array = (JSONArray) obj;
            int tam = array.size();
            for (int i = 0; i < tam; i++) {
                JSONObject jobj = (JSONObject) array.get(i);
                String id = (String) jobj.get("id");
                id = (id == null ? "null" : id);
                String url = (String) jobj.get("url");
                url = (url == null ? "null" : url);
                uris.add(url);
   //             System.out.println(id + "\t" + url + "\t");
            }
        } catch (Exception e) {
            System.err.println("HA HABIDO UN ERROR");
        }
        return uris;
    }
    
    static boolean isTherePredicate(String s)
    {
        Property ptipo = Modelo.model.getProperty(s);
        ResIterator ri=Modelo.model.listSubjectsWithProperty(ptipo);
        if (ri.hasNext())
            return true;
        return false;
    }
    ///xxxxxxxxxx pero puedo precargar datasets e ids.
    ///http://datahub.io/api/rest/package/eurostat-rdf b93f71f1-909c-473c-8e5a-ccb512957b86
    ///http://datahub.io/api/rest/package/eurostat-rdf obtiene un id que es el mismo que aparece en el json del recurso como "package_id"
}
