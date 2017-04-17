/*
Copyright (c) 2015 OEG

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package oeg.licensius.util;

//import org.apache.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Model;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class with the method that adds the only required prefixes
 * @author Victor
 */
public class RDFPrefixes {

    private static final Map<String, String> map = new HashMap();

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
    
    /**
     * Reverse query: given a prefix, obtain the URL
     * @param prefix Prefix
     */
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
     * Adds only the prefix that are required in a Jena model
     * Prefixes that are added must be in the intialization list (modify th list if new prefixes are needed)
     * @param modelo Jena model
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

    /**
     * Expands a prefixed resource. 
     * @param Prefixed resource. For example: dct:license
     * @return Full URI. For example: http://purl.org/dc/terms/license
     */
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


}
