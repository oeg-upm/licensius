package oeg.odrlapi.rdf;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.log4j.Logger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import org.apache.jena.atlas.lib.StrUtils;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

/**
 * Helper class with some useful methods to manipulate RDF
 *
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

    public static void print(Model modelo) {
//        RDFDataMgr.write(System.out, modelo, Lang.TURTLE) ;
    }
    
    /**
     * Este algoritmo es probable que haga loops. OJO!
     * In a model, it chooses every statement where the given resource is given is the subject and children
     */
    public static Model getRacimo(Model model, Resource nodo, List<Resource> traversed)
    {
        Model racimo = ModelFactory.createDefaultModel();
        StmtIterator it = model.listStatements(new SimpleSelector(nodo, null, (RDFNode)null));
        while(it.hasNext())
        {
            Statement stm = it.next();
            RDFNode o = stm.getObject();
            Resource p = stm.getPredicate();
            if (o.isLiteral())
            {
                racimo.add(stm);
            }
            if (o.isResource())
            {
                if (traversed.contains(o.asResource()))
                    continue;
                traversed.add(o.asResource());
                racimo.add(stm);
                Model hijos = getRacimo(model,o.asResource(), traversed);
                racimo.add(hijos);
            }
        }
        return racimo;
    }    
    /**
     * Este algoritmo es probable que haga loops. OJO!
     */
    public static Model getRacimoExceptProperty(Model model, Resource nodo, List<Resource> traversed)
    {
        Model racimo = ModelFactory.createDefaultModel();
        Selector selector = new SimpleSelector(nodo, null, (RDFNode)null);
        StmtIterator it = model.listStatements(selector);
        while(it.hasNext())
        {
            Statement stm = it.next();
            RDFNode o = stm.getObject();
            Resource p = stm.getPredicate();
            if (p.toString().equals("http://www.w3.org/ns/odrl/2/inheritFrom"))
                continue;
            if (o.isResource())
            {
                if (traversed.contains(o.asResource()))
                    continue;
                traversed.add(o.asResource());
                racimo.add(stm);
                Model hijos = getRacimoExceptProperty(model,o.asResource(), traversed);
                racimo.add(hijos);
            }
        }
        return racimo;
    }

    /*
    public static boolean save(Model modelo, String filename)
    {
    try {
    BufferedWriter out = new BufferedWriter(new FileWriter(filename));
    //            RDFDataMgr.write(System.out, modelo, Lang.TURTLE) ;
    RDFDataMgr.write(out, modelo, Lang.TURTLE) ;
    out.close();
    } catch (IOException ex) {
    Logger.getLogger("licenser").warn("Error saving file. " + ex.getMessage());
    return false;
    }
    return true;
    }*/
    public static boolean save(String modelo, String filename) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(modelo);
            out.close();
        } catch (IOException ex) {
            //       Logger.getLogger("licenser").warn("Error saving file. " + ex.getMessage());
            return false;
        }
        return true;
    }


    /**
     */
    public static List<String> getSubjectOfType(Model model, Resource propiedad) {
        List<String> policies = new ArrayList();
        ResIterator it = model.listResourcesWithProperty(RDF.type, propiedad);
        while (it.hasNext()) {
            Resource res = it.next();
            Statement s = res.getProperty(RDF.type);
            Resource sujeto = s.getSubject();
            policies.add(sujeto.getURI());
        }
        return policies;
    }
    public static List<Resource> getRSubjectOfType(Model model, Resource propiedad) {
        List<Resource> policies = new ArrayList();
        ResIterator it = model.listResourcesWithProperty(RDF.type, propiedad);
        while (it.hasNext()) {
            Resource res = it.next();
            Statement s = res.getProperty(RDF.type);
            Resource sujeto = s.getSubject();
            policies.add(sujeto);
        }
        return policies;
    }
    

    public static List<String> getObjectsOfType(Model model, String uri) {
        List<String> policies = new ArrayList();

        ResIterator it = model.listSubjectsWithProperty(RDF.type);
        while (it.hasNext()) {
            Resource res = it.next();
            Statement s = res.getProperty(RDF.type);
            String objeto = s.getObject().asResource().getURI();
            if (s.getObject().isURIResource()) {
                String sujeto = s.getSubject().getURI();
                String ouri = s.getObject().asResource().getURI();
                if (uri.equals(ouri)) {
                    policies.add(sujeto);
                }
            }
        }
        return policies;
    }

    /**
     * Gets the first value of a property
     */
    public static String getFirstPropertyValue(Resource resource, Property property) {
        String value = "";
        StmtIterator it = resource.listProperties(property);
        if (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            value = nodo.toString();
            return value;
        }
        return "";
    }
    public static List<String> getRO(Model model, Resource sujeto, Property property) {
        List<String> cadenas = new ArrayList();
        try{
            NodeIterator it = model.listObjectsOfProperty(sujeto, property);
            while (it.hasNext()) {
                RDFNode nodo = it.nextNode();
                String rtitle = nodo.toString();
                cadenas.add(rtitle);
            }
        } catch (Exception e) {

        }
        return cadenas;
        
    }

    /**
     * Gets all the values of the given property. It assumes that all the values
     * are strings.
     *
     * @param resource
     * @param property
     */
    public static List<String> getObjectsGivenSP(Model model, String ssujeto, String sproperty) {
        List<String> cadenas = new ArrayList();
        try {
            Resource sujeto = ModelFactory.createDefaultModel().createProperty(ssujeto);
            Property property = ModelFactory.createDefaultModel().createProperty(sproperty);
            NodeIterator it = model.listObjectsOfProperty(sujeto, property);
            while (it.hasNext()) {
                RDFNode nodo = it.nextNode();
                String rtitle = nodo.toString();
                cadenas.add(rtitle);
            }
        } catch (Exception e) {

        }
        return cadenas;
    }

    //  http://creativecommons.org/licenses/by/4.0/
    public static List<Resource> getAllSubjectsWithObject(Model model, String s) {
        List<Resource> lres = new ArrayList();

        StmtIterator it = model.listStatements();
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            String objeto = nodo.toString();
            if (s.equals(objeto)) {
                lres.add(stmt2.getSubject());
            }
        }
        return lres;
    }

    public static int getNumberOfTriples(Model model) {
        int c = 0;
        StmtIterator it = model.listStatements();
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            c++;
        }
        return c;
    }

    /**
     * Devuelve los objetos para una propiedad dada: - Si es recurso, la URI -
     * Si es literal, solo el literal (sin language tag ni datatype)
     */
    public static List<String> getObjectsForProperty(Model model, String sproperty) {
        List<String> lres = new ArrayList();
        Property property = ModelFactory.createDefaultModel().createProperty(sproperty);
        NodeIterator nit = model.listObjectsOfProperty(property);
        while (nit.hasNext()) {
            RDFNode nodo = nit.next();
            if (nodo.isResource()) {
                Resource r = (Resource) nodo;
                lres.add(r.getURI());
            }
            if (nodo.isLiteral()) {
                Literal l = (Literal) nodo;
                lres.add(l.getString());
            }
        }
        return lres;
    }

    public static List<String> getSubjectsForProperty(Model model, String sproperty) {
        List<String> lres = new ArrayList();
        Property property = ModelFactory.createDefaultModel().createProperty(sproperty);
        ResIterator nit = model.listResourcesWithProperty(property);
        while (nit.hasNext()) {
            Resource nodo = nit.next();
            if (nodo.isResource()) {
                Resource r = (Resource) nodo;
                lres.add(r.getURI());
            }
        }
        return lres;
    }

    /**
     * Devuelve todos objetos dado un recurso y una propiedad.
     */
    public static List<Resource> getAllPropertyResources(Resource resource, Property property) {
        List<Resource> cadenas = new ArrayList();
        StmtIterator it = resource.listProperties(property);
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            if (nodo.isResource()) {
                Resource re = (Resource) nodo;
                cadenas.add(re);
            }
        }
        return cadenas;
    }

    /**
     * Obtains the first literal in the given model for the uri and property
     * given and of the given language
     */
    public static String getFirstLiteral(Model model, String uri, String propiedad, String lan) {
        Resource res = model.getResource(uri);
        if (res == null) {
            return "";
        }
        Property prop = ModelFactory.createDefaultModel().createProperty(propiedad);
        StmtIterator it = res.listProperties(prop);
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            if (nodo.isLiteral()) {
                Literal l = nodo.asLiteral();
                if (l.getLanguage().equals(lan)) {
                    return l.getLexicalForm();
                }
            }
        }
        return "";
    }

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

    public static String getFirstLanguage(Model model, String uri, String propiedad) {
        String s = "[";
        Resource res = model.getResource(uri);
        if (res == null) {
            return "";
        }
        Property prop = ModelFactory.createDefaultModel().createProperty(propiedad);
        StmtIterator it = res.listProperties(prop);
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            if (nodo.isLiteral()) {
                Literal l = nodo.asLiteral();
                if (true) {
                    return l.getLanguage();
                }
                s += "\"" + l.getLanguage() + "\", ";
            }
        }
        s += "]";
        return s;
    }

    public static String getAllLanguages(Model model, String uri, String propiedad) {
        String s = "[";
        Resource res = model.getResource(uri);
        if (res == null) {
            return "";
        }
        Property prop = ModelFactory.createDefaultModel().createProperty(propiedad);
        StmtIterator it = res.listProperties(prop);
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            if (nodo.isLiteral()) {
                Literal l = nodo.asLiteral();
                s += "\"" + l.getLanguage() + "\", ";
            }
        }
        s += "]";
        return s;
    }

    public static String getLabel(Model model, String uri) {
        Resource res = model.getResource(uri);
        if (res == null) {
            return "";
        }
//        StmtIterator it = model.listStatements();
        StmtIterator it = res.listProperties(LABEL);
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            return nodo.toString();
//            RDFNode p = stmt2.getPredicate();
            //          if (p.toString().equals(LABEL.toString()) && )
//                System.out.println(nodo.toString());
        }
        return "";
    }

    /**
     * Parses the ontology from a text
     *
     * @param txt Text with an ontology
     */
    public static Model parseFromText(String txt) {
        Model model = ModelFactory.createDefaultModel();
        InputStream is = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8));
        logger.info("Size: " + txt.length());
        try {
            model.read(is, null, "RDF/XML");
            logger.debug("Read as RDF/XML");
            return model;
        } catch (Exception e) {
            logger.debug("Failed as RDF/XML. " + e.getMessage());
            try {
                is.close();
                is = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8));
                model.read(is, null, "TURTLE");
                logger.debug("Read as TURTLE");
                return model;
            } catch (Exception e2) {
                logger.debug("Failed as TURTLE. " + e2.getMessage());
            }
            return null;
        }
    }

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
        s += "@prefix : <http://purl.org/NET/rdflicense/> .\n";
        return s;
    }

    public static String getLicensePrefixes(String f) {
        String s = "";
        s += "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n";
        s += "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n";
        s += "@prefix : <http://purl.org/NET/rdflicense/> .\n";

        if (f.contains("cc:")) {
            s += "@prefix cc: <http://creativecommons.org/ns#> . \n";
        }
        if (f.contains("dct:")) {
            s += "@prefix dct: <http://purl.org/dc/terms/> . \n";
        }
        if (f.contains("owl:")) {
            s += "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n";
        }
        if (f.contains("dcat:")) {
            s += "@prefix dcat:  <http://www.w3.org/ns/dcat#> .\n";
        }
        if (f.contains("xml:")) {
            s += "@prefix xml: <http://www.w3.org/XML/1998/namespace> .\n";
        }
        if (f.contains("xsd:")) {
            s += "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n";
        }
        if (f.contains("foaf:")) {
            s += "@prefix foaf: <http://xmlns.com/foaf/0.1/> .\n";
        }
        if (f.contains("l4lod:")) {
            s += "@prefix l4lod: <http://ns.inria.fr/l4lod/> .\n";
        }
        if (f.contains("odrl:")) {
            s += "@prefix odrl: <http://www.w3.org/ns/odrl/2/> .\n";
        }
        if (f.contains("ldr:")) {
            s += "@prefix ldr: <http://purl.org/NET/ldr/ns#> .\n";
        }
        if (f.contains("skos:")) {
            s += "@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\n";
        }
        if (f.contains("provo:")) {
            s += "@prefix provo: <http://purl.org/net/provenance/ns#> .\n";
        }
        return s;
    }

    static String getLastPart(String uri) {
        Resource r = ModelFactory.createDefaultModel().createResource(uri);
        return r.getLocalName();
    }

    public static String getLastBitFromUrl(final String url) {
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }

    public static String extractURIFromText(String texto) {
        try {
            URI uri = new URI(texto);
            String suri = uri.toString();
      //      System.out.println(suri);
            return suri;
        } catch (Exception e) {
            List<String> uris = extractUrls(texto);
            for (String uri : uris) {
      //          System.out.println("Candidato: " + uri);
                return uri;
            }
        }
        return "";
    }

    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);
        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    public static List<Resource> guessRDFLicenseFromURI(Model model, String uri) {
        List<Resource> lres = RDFUtils.getAllSubjectsWithObject(model, uri);
        boolean ok = RDFUtils.isSubjectInModel(model, uri);
        if (ok) {
            lres.add(model.createResource(uri));
        }
        return lres;
    }

    public static boolean isSubjectInModel(Model model, String uri) {
        StmtIterator it = model.listStatements();
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getSubject();
            if (nodo.isResource()) {
                Resource res = nodo.asResource();
                if (res != null) {
                    String urimodel = res.getURI();
                    if (urimodel != null && urimodel.equals(uri)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getString(Model model) {
        String syntax = "TURTLE"; // also try "N-TRIPLE" and "TURTLE"
        StringWriter out = new StringWriter();
        model.write(out, syntax);
        String result = out.toString();
        return result;
    }
    public static String getStringJSONLD(Model model) {
        String syntax = "JSON-LD"; // also try "N-TRIPLE" and "TURTLE"
        StringWriter out = new StringWriter();
        model.write(out, syntax);
        String result = out.toString();
        return result;
    }
    
    public static Model inferClassFromRange(Model model, String spropiedad, String sclase)
    {
        Property property = ModelFactory.createDefaultModel().createProperty(spropiedad);
        Resource clase = ModelFactory.createDefaultModel().createProperty(sclase);
        ResIterator rit = model.listResourcesWithProperty(property);
        NodeIterator nit = model.listObjectsOfProperty(property);
        while (nit.hasNext()) {
            RDFNode node = nit.next();
            if (node.isResource())
            {
                Resource r = node.asResource();
                model.add(r,RDF.type,clase);
            }
        }
        return model;
    }
    public static Set<Statement> getSetOfStatements(Model m, Selector selector)
    {
        Set<Statement> set = new HashSet();
        StmtIterator si = m.listStatements(selector);
        while(si.hasNext())
        {
            Statement s = si.next();
            set.add(s);
        }
        return set;
    }
    /**
     * Loads a file from a URI, downloading it. 
     */
    public static String getFile(String uri) {
        String txt = "";
        try {
            URL oracle = new URL(uri);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                txt+=inputLine+" \n";
            }
            in.close();
        } catch (Exception e) {
        }
        return txt;
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
            
            URLConnection oc = obj.openConnection();
            HttpURLConnection conn = null;
            if (url.startsWith("https"))
                conn = (HttpsURLConnection)oc;
            else
                conn = (HttpURLConnection)oc;
              
            /*
            HttpURLConnection conn = null;
            if (url.startsWith("https"))
                conn = (HttpsURLConnection) obj.openConnection();
            else
                conn = (HttpURLConnection) obj.openConnection();
            */
            
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
                conn.disconnect();
                if (newUrl.startsWith("https"))
                    conn = (HttpsURLConnection) new URL(newUrl).openConnection();
                else
                    conn = (HttpURLConnection) new URL(newUrl).openConnection();
                if (cookies!=null)
                    conn.setRequestProperty("Cookie", cookies);                
            }
	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	String inputLine;
	StringBuffer html = new StringBuffer();
	while ((inputLine = in.readLine()) != null) {html.append(inputLine);html.append("\n");}
	in.close();            
            
        document = html.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error navegando la web semántica " + e.getMessage());
        }
        return document;
    }    
}
