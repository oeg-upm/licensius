package oeg.utils.rdf;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import ldc.model.Grafo;
import odrlmodel.rdf.ODRLModel;
import org.apache.jena.atlas.lib.StrUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.openjena.riot.Lang;

/**
 *
 * @author vroddon
 */
public class RDFUtils {

    static final Logger logger = Logger.getLogger("ldc");

    public static String getFirstLiteral(Model model, String s, String p)
    {
        NodeIterator ni = model.listObjectsOfProperty(model.createResource(s), model.createProperty(p));
        if (ni.hasNext())
            return ni.next().asLiteral().getLexicalForm();
        return "";
    }    

    /**
     * Establece una propieda borrando todo lo anterior.
     */
    public static void setFirstLiteral(Model model, String uri, String p, String o) {
        List<RDFNode> nodes = new ArrayList();
        NodeIterator nit = model.listObjectsOfProperty(model.createResource(uri), model.createProperty(p));
        while(nit.hasNext())
        {
            RDFNode nod=nit.next();
            nodes.add(nod);
        }
        for(RDFNode node : nodes)
        {
            model.remove(model.createResource(uri), model.createProperty(p),node);
        }
        Literal l = model.createLiteral(o);
        model.add(model.createResource(uri), model.createProperty(p), l);
    }
    

    /**
     * Parses the ontology from a text
     *
     * @param txt Text with an ontology
     */
    public static Model parseFromText(String txt) {
        Model model = ModelFactory.createDefaultModel();
//        byte[] bytes = txt.getBytes(StandardCharsets.UTF_8);
        byte[] bytes = txt.getBytes();
        InputStream is = new ByteArrayInputStream(bytes); //
        try {
            model.read(is, null, "TURTLE");
            logger.info("Parseado correctamente como Turtle!!");
            return model;
        } catch (Exception e) {
            logger.warn(e.getMessage());
            logger.error("error" + e.getMessage());
            System.err.println(e.getMessage());
            try {
                is.close();
                is = new ByteArrayInputStream(bytes);
                model.read(is, null, "RDF/XML");
                logger.info("Parseado correctamente como RDF/XML!!");
                return model;
            } catch (Exception e2) {
                try{
                    is.close();
                    is = new ByteArrayInputStream(bytes);
                    model.read(is, null, "NT");
                    logger.info("Parseado correctamente como NT!!");
                    return model;
                }catch(Exception e3)
                {
                    logger.info("No se ha podido parsear de ninguna de la maneras!!");
                }
        }
            return null;
        }
    }    
    public static boolean isURI(String o) {
        try {
            URI uri = new URI(o);
            return true;
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    String property = "";
    String value = "";

    public static Property TITLE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/title");
    public static Property COMMENT = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#comment");
    public static Property RIGHTS = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/rights");
    public static Property PLICENSE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/license");
    public static Property LABEL = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#label");
    public static Property SEEALSO = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");
    public static Property PMAKESOFFER = ModelFactory.createDefaultModel().createProperty("http://schema.org/makesOffer");

    public static Resource RDATASET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/dcat#Dataset");
    public static Resource RLINKSET = ModelFactory.createDefaultModel().createResource("http://rdfs.org/ns/void#Linkset");

    public static void print(Model modelo) {
        RDFDataMgr.write(System.out, modelo, Lang.TURTLE);
    }

    public static boolean save(Model modelo, String filename) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
//            RDFDataMgr.write(System.out, modelo, Lang.TURTLE) ;
            RDFDataMgr.write(out, modelo, Lang.TURTLE);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger("licenser").warn("Error saving file. " + ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean save(String modelo, String filename) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(modelo);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger("licenser").warn("Error saving file. " + ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean isOfKind(Resource rrule, String uri) {
        if (rrule == null || uri == null || uri.isEmpty() || rrule.getURI() == null) {
            return false;
        }
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.add(ODRLModel.coreModel);
        Individual res = ontModel.getIndividual(rrule.getURI());
        OntClass clase = ontModel.getOntClass(uri);
        if (res == null || clase == null) {
            return false;
        }
        return res.hasOntClass(clase);

    }

    public static List<Resource> getOntResourcesOfType(Model model, Resource resource) {
        List<Resource> res = new ArrayList();
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.add(model);
        ontModel.add(ODRLModel.coreModel);

       // RDFUtils.print(ontModel);
        Resource r2 = ontModel.createResource(resource.getURI());
        ExtendedIterator it = ontModel.listIndividuals(r2);
        while (it.hasNext()) {
            Individual ind = (Individual) it.next();
            if (ind.isResource()) {
                res.add(ind);
            }
        }
        return res;
    }

    /**
     * Obtiene todos los recursos que son de un tipo dado
     */
    public static List<Resource> getResourcesOfType(Model model, Resource resource) {
        List<Resource> policies = new ArrayList();
        ResIterator it = model.listResourcesWithProperty(RDF.type, resource);
        while (it.hasNext()) {
            Resource res = it.next();
            Statement s = res.getProperty(RDF.type);
            String objeto = s.getObject().asResource().getURI();
            Resource sujeto = s.getSubject();
            policies.add(sujeto);
        }
        return policies;
    }

    /**
     * Get
     */
    public static List<String> getObjectsOfType(Model model, Resource resource) {
        List<String> policies = new ArrayList();
        ResIterator it = model.listResourcesWithProperty(RDF.type, resource);
        while (it.hasNext()) {
            Resource res = it.next();
            Statement s = res.getProperty(RDF.type);
            String objeto = s.getObject().asResource().getURI();
            Resource sujeto = s.getSubject();
            policies.add(sujeto.getURI());
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

    /**
     * Gets all the values of the given property. It assumes that all the values
     * are strings.
     *
     * @param resource
     * @param property
     */
    public static List<String> getAllPropertyStrings(Resource resource, Property property) {
        List<String> cadenas = new ArrayList();
        StmtIterator it = resource.listProperties(property);
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            String rtitle = nodo.toString();
            cadenas.add(rtitle);
        }
        return cadenas;
    }

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

    public static Map<String, String> prefijos = initprefijos();

    public static Map<String, String> initprefijos()
    {
        Map<String, String> mapa = new HashMap();
        mapa.put("odrl", "http://www.w3.org/ns/odrl/2/"); //http://w3.org/ns/odrl/2/
        mapa.put("dct", "http://purl.org/dc/terms/");
        mapa.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        mapa.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        mapa.put("cc", "http://creativecommons.org/ns#");
        mapa.put("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        mapa.put("void", "http://rdfs.org/ns/void#");
        mapa.put("dcat", "http://www.w3.org/ns/dcat#");
        mapa.put("gr", "http://purl.org/goodrelations/");
        mapa.put("prov", "http://www.w3.org/ns/prov#");
        mapa.put("schema", "http://schema.org/");     
        mapa.put("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
        mapa.put("lemon", "http://lemon-model.net/lemon#");
        return mapa;
    }
    
    /**
     * Adds the most common prefixes to the generated model
     * @deprecated
     */
    public static void addPrefixesToModel(Model model) {
        model.setNsPrefix("odrl", "http://www.w3.org/ns/odrl/2/"); //http://w3.org/ns/odrl/2/
        model.setNsPrefix("dct", "http://purl.org/dc/terms/");
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model.setNsPrefix("cc", "http://creativecommons.org/ns#");
        model.setNsPrefix("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        model.setNsPrefix("void", "http://rdfs.org/ns/void#");
        model.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
        model.setNsPrefix("gr", "http://purl.org/goodrelations/");
        model.setNsPrefix("prov", "http://www.w3.org/ns/prov#");
        model.setNsPrefix("schema", "http://schema.org/");
    }
    
    public static String replaceWithPrefix(String uri)
    {
        String prefixed=uri;
        Iterator it = prefijos.entrySet().iterator();
        while (it.hasNext()) {
            if (uri.startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#"))
                    uri=uri;
            Map.Entry e = (Map.Entry)it.next();
            String prefijo = (String)e.getKey();
            String namespace= (String)e.getValue();
            uri=uri.replace(namespace, prefijo+":");
        }
        return uri;
    }
    

    /**
     * Downloads a file making its best: - following redirects - implementing
     * the best content negotiation curl -I -L -H "Accept: application/rdf+xml"
     * http://datos.bne.es/resource/XX947766
     */
    public static String browseSemanticWeb(String url) {
        String document = "";
        String acceptHeaderValue = StrUtils.strjoin(",", "application/rdf+xml", "application/turtle;q=0.9", "application/x-turtle;q=0.9", "text/n3;q=0.8", "text/turtle;q=0.8", "text/rdf+n3;q=0.7", "application/xml;q=0.5", "text/xml;q=0.5", "text/plain;q=0.4", "*/*;q=0.2");
        boolean redirect = false;
        try {
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestProperty("Accept", acceptHeaderValue);
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
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer html = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                html.append(inputLine);
            }
            in.close();
            document = html.toString();
        } catch (Exception e) {

        }
        return document;
    }

    public static boolean validUTF8(byte[] input) {
        int i = 0;
        // Check for BOM
        if (input.length >= 3 && (input[0] & 0xFF) == 0xEF
                && (input[1] & 0xFF) == 0xBB & (input[2] & 0xFF) == 0xBF) {
            i = 3;
        }

        int end;
        for (int j = input.length; i < j; ++i) {
            int octet = input[i];
            if ((octet & 0x80) == 0) {
                continue; // ASCII
            }

            // Check for UTF-8 leading byte
            if ((octet & 0xE0) == 0xC0) {
                end = i + 1;
            } else if ((octet & 0xF0) == 0xE0) {
                end = i + 2;
            } else if ((octet & 0xF8) == 0xF0) {
                end = i + 3;
            } else {
                // Java only supports BMP so 3 is max
                return false;
            }

            while (i < end) {
                i++;
                octet = input[i];
                if ((octet & 0xC0) != 0x80) {
                    // Not a valid trailing byte
                    return false;
                }
            }
        }
        return true;
    }


    public static int countStatements(Model model) {
        int count = 0;
        StmtIterator it = model.listStatements();
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }
    

    /**
     * Determina si la petición ha de ser servida a un humano o directamente el RDF
     * @param request HTTP request
     */
    public static boolean isRDFXML(HttpServletRequest request) {
        String uri = request.getRequestURI();
        boolean human = true;
        Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            String hname = (String) enume.nextElement();
            Enumeration<String> enum2 = request.getHeaders(hname);
            //      System.out.print(hname + "\t");
            while (enum2.hasMoreElements()) {
                String valor = enum2.nextElement();
                if (hname.equals("Accept")) {
                    if (valor.equals("application/rdf+xml")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determina si la petición ha de ser servida a un humano o directamente el RDF
     * @param request HTTP request
     */
    public static boolean isHuman(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.endsWith(".ttl") || uri.endsWith(".rdf")) {
            return false;
        }
        boolean human = true;
        Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            String hname = (String) enume.nextElement();
            Enumeration<String> enum2 = request.getHeaders(hname);
            //      System.out.print(hname + "\t");
            while (enum2.hasMoreElements()) {
                String valor = enum2.nextElement();
                if (hname.equals("Accept")) {
                    if (valor.equals("application/rdf+xml") || valor.equals("text/turtle") || valor.equals("application/x-turtle")) {
                        return false;
                    }
                }
            }
        }
        return human;
    }    
    public static Grafo getGrafo(List<Grafo> lg, String s) {
        for (Grafo g : lg) {
            if (g.getURI().equals(s)) {
                return g;
            }
        }
        return null;
    }
    
}
