package oeg.rdflicense;

import oeg.rdflicense.RDFUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NsIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PrefixMapping;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//LOG4J
import org.apache.log4j.Logger;
import org.openjena.atlas.lib.StrUtils;

/**
 * Helper class with some useful methods to manipulate RDF
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class RDFUtils {

    private static final Logger logger = Logger.getLogger(RDFUtils.class.getName());

    String property = "";
    String value = "";
    public static Property TITLE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/title");
    public static Property COMMENT = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#comment");
    public static Property RIGHTS = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/rights");
    public static Property RLICENSE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/license");
    public static Property LABEL = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#label");
    public static Property SEEALSO = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");
    public static Resource RDATASET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/dcat#Dataset");
    public static Resource RLINKSET = ModelFactory.createDefaultModel().createResource("http://rdfs.org/ns/void#Linkset");

    /**
     * Gets the first object for a given subject and property
     */
    public static String getFirstValue(Model model, String uri, String propiedad) {
        Resource res = model.getResource(uri);
        if (res == null) {
            return "";
        }
        Property prop = ModelFactory.createDefaultModel().createProperty(propiedad);
        StmtIterator it = res.listProperties(prop);
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            return nodo.toString();
        }
        return "";
    }

    /**
     * Gets the first label it finds for a given resource
     */
    public static String getLabel(Model model, String uri) {
        Resource res = model.getResource(uri);
        if (res == null) {
            return "";
        }
        StmtIterator it = res.listProperties(LABEL);
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            return nodo.toString();
        }
        return "";
    }

    /**
     * Parses the ontology from a text
     * @param txt Text with an ontology
     */
    public static Model parseFromText(String txt) {
        Model model = ModelFactory.createDefaultModel();
        InputStream is = new ByteArrayInputStream(txt.getBytes());
        logger.info("Size: " + txt.length());
        String head = "";
        int index = txt.indexOf("\n");
        if (index!=-1)
            head=txt.substring(0,index);
        try {
            model.read(is, null, "TURTLE");
            logger.debug("Read as TURTLE: "+ head);
            return model;
        } catch (Exception e) {
            logger.warn("Failed as TURTLE " + head +" -- " + e.getMessage());
            try {
                is.close();
                is = new ByteArrayInputStream(txt.getBytes());
                model.read(is, null, "RDF/XML");
                logger.debug("Read as RDF/XML");
                return model;
            } catch (Exception e2) {
                logger.warn("Failed as RDF/XML: " + head+  " -- " + e2.getMessage());
            }
            return null;
        }
    }

    /**
     * Returns a string with common prefixes in turtle 
     */
    public static String getLicensePrefixes() {
        String s = "@prefix cc: <http://creativecommons.org/ns#> . \n";
        s += "@prefix dct: <http://purl.org/dc/terms/> . \n";
        s += "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n";
        s += "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n";
        s += "@prefix dcat:  <http://www.w3.org/ns/dcat#> .\n";
        s += "@prefix xml: <http://www.w3.org/XML/1998/namespace> .\n";
        s += "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n";
        s += "@prefix foaf: <http://xmlns.com/foaf/0.1/> .\n";
        s += "@prefix l4lod: <http://ns.inria.fr/l4lod/> .\n";
        s += "@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n";
        s += "@prefix odrl: <http://www.w3.org/ns/odrl/2/> .\n";
        s += "@prefix ldr: <http://purl.org/NET/ldr/ns#> .\n";
        s += "@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\n";
        s += "@prefix provo: <http://purl.org/net/provenance/ns#> .\n";
        s += "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n";
        s += "@prefix ms: <http://purl.org/NET/ms-rights#> .\n";
        s += "@prefix : <http://purl.org/NET/rdflicense/> .\n";
        return s;
    }
    
    public static Model cleanUnusedPrefixes(Model model) {
        NsIterator it = model.listNameSpaces();
        List<String> uris = new ArrayList();
        List<String> toremove = new ArrayList();
        while(it.hasNext())
        {
            String s = it.next();
            uris.add(s);
        }
        Map<String,String> mapa = model.getNsPrefixMap();
        Iterator i = mapa.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry)i.next();
            if (!uris.contains(e.getValue()))
            {
                String aq=(String)e.getKey();
                toremove.add(aq);
            }
        }
        for(String s : toremove)
            model.removeNsPrefix(s);
        return model;
    }
    
    public static String getLastBitFromUrl(final String url){
        return url.replaceFirst(".*/([^/?]+).*", "$1"); 
    }        
    
    
    /**
     * Downloads a file making its best:
     * - following redirects
     * - implementing the best content negotiation
     * curl -I -L -H "Accept: application/rdf+xml" http://datos.bne.es/resource/XX947766
     */
    public static String browseSemanticWeb(String url) {
        String document = "";
        String acceptHeaderValue = StrUtils.strjoin(",","application/rdf+xml","application/turtle;q=0.9","application/x-turtle;q=0.9","text/n3;q=0.8","text/turtle;q=0.8","text/rdf+n3;q=0.7","application/xml;q=0.5","text/xml;q=0.5","text/plain;q=0.4","*/*;q=0.2");
        boolean redirect = false;
        try {
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestProperty("Accept",acceptHeaderValue);                
            conn.setReadTimeout(5000);
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    redirect = true;
                }
            }
            
            if (redirect) {
		String newUrl = conn.getHeaderField("Location");
		String cookies = conn.getHeaderField("Set-Cookie");  
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestProperty("Cookie", cookies);                
            }
	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	String inputLine;
	StringBuffer html = new StringBuffer();
	while ((inputLine = in.readLine()) != null) {html.append(inputLine);html.append("\n");}
	in.close();            
            
        document = html.toString();
            
        } catch (Exception e) {
            
        }
        return document;
    }
    
}
