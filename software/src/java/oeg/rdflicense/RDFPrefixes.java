package oeg.rdflicense;

import com.hp.hpl.jena.rdf.model.Model;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class with the method that adds the only required prefixes
 * @author Victor
 */
public class RDFPrefixes {

    private static Map<String, String> map = new HashMap(); 

    private static void init() {
        map.put("http://www.w3.org/ns/odrl/2/", "odrl");
        map.put("http://purl.org/dc/terms/", "dct");
        map.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
        map.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
        map.put("http://semanticweb.cs.vu.nl/2009/11/sem/", "sem");
        map.put("http://purl.org/dc/terms/", "dct");
        map.put("http://www.w3.org/2004/02/skos/core#", "skos");
        map.put("http://www.geonames.org/ontology#", "gn");
        map.put("http://xmlns.com/foaf/0.1/", "foaf");
        map.put("http://tbx2rdf.lider-project.eu/tbx#", "tbx");
        map.put("http://www.w3.org/ns/lemon/ontolex#", "ontolex");
        map.put("http://tbx2rdf.lider-project.eu/converter/resource/iate/", "iate");
        map.put("http://lemon-model.net/lemon#", "lemon");
        map.put("http://creativecommons.org/ns#", "cc");
        map.put("http://www.w3.org/ns/dcat#", "dcat");
        map.put("http://rdfs.org/ns/void#", "void");
    }
    
    public static String reverse(String prefix)
    {
        if (map.isEmpty()) {
            init();
        }        
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            String prefijo = (String)e.getValue();
            if (!prefijo.equals(prefix))
                continue;
            String nombre = (String)e.getKey();
            return nombre;
        }
        return "";
    }
    

    /**
     * Adds only the prefix that are required in a model
     */
    public static Model addPrefixesIfNeeded(Model modelo) {
        if (map.isEmpty()) {
            init();
        }
        StringWriter sw = new StringWriter();
        
        modelo.write(sw, "TTL");

        String ttl = sw.toString();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String key = (String) e.getKey();
            if (ttl.contains(key))
            {
                modelo.setNsPrefix((String)e.getValue(), key);
            }
        }
        return modelo;
    }
    
    public static String getSampleTTL()
    {
        String ttl ="<http://purl.org/NET/vroddon> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> .";
        return ttl;
    }

    public static String extend(String param) {
        if (!param.contains(":"))
            return param;
        String left=param.substring(0,param.indexOf(":"));
        String right=param.substring(param.indexOf(":")+1, param.length());
        String prefex = RDFPrefixes.reverse(left);
        if (prefex.isEmpty())
            return param;
        return prefex+right;
    }

    public static String encode(String base, String name) {
        if (name.contains("%"))
            return base+name;
        try {
            String codificado = URLEncoder.encode(name, "UTF-8");
            codificado=codificado.replace("+", "%20");
            codificado=codificado.replace("(", "%28");
            codificado=codificado.replace(")", "%29");
            String sres = base + codificado;
            return sres;
        } catch (Exception e) {
            return ""; 
        }
    }

    public static String getLastPart(String uri) {
        String s="";
        int index=uri.lastIndexOf("/");
        if (index==-1)
            return uri;
        s=uri.substring(index+1, uri.length());
        s=s.replace("%20", " ");
        s=s.replace("+", " ");
        return s;
    }
        public static void main(String[] args) {
            System.out.println(getLastPart("http://tbx2rdf.lider-project.eu/data/iate/IATE-74645"));
        }

}
