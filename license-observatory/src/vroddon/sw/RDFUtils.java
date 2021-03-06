package vroddon.sw;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.Exception;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

//JENA
import java.util.List;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
//import org.apache.jena.riot.RDFDataMgr;
//import org.apache.log4j.varia.NullAppender;
import org.apache.log4j.Logger;
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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//import org.apache.jena.riot.Lang;
//import org.apache.jena.riot.RDFDataMgr;
//import org.apache.log4j.Logger;

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

    public static void print(Model modelo) {
//        RDFDataMgr.write(System.out, modelo, Lang.TURTLE) ;
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
     * Gets all the values of the given property. It assumes that all the values are strings.
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
     * Devuelve los objetos para una propiedad dada:
     * - Si es recurso, la URI
     * - Si es literal, solo el literal (sin language tag ni datatype)
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

    /**
     * Devuelve los objetos para una propiedad dada:
     * - Si es recurso, la URI
     * - Si es literal, solo el literal (sin language tag ni datatype)
     */
    public static List<Tripleta> getTripletasForProperty(Model model, String sproperty) {
        List<Tripleta> lres = new ArrayList();
        StmtIterator it = model.listStatements();
        while (it.hasNext()) {
            Statement st = it.next();
            String s1=st.getPredicate().getURI();
            if (!s1.equals(sproperty))
                continue;
            RDFNode nodo = st.getObject();
            String so = "";
            if (nodo.isResource()) {
                Resource r = (Resource) nodo;
                so = r.getURI();
            }
            if (nodo.isLiteral()) {
                Literal l = (Literal) nodo;
                so = l.getString();
            }
            lres.add(new Tripleta(st.getSubject().getURI(), st.getPredicate().getURI(),so));
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

    public static String getFirstLabel(Model model, String uri) {
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
        InputStream is = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8));
        logger.info("Size: " + txt.length());
        try {
            model.read(is, null, "RDF/XML");
            logger.warn("Read as RDF/XML");
            return model;
        } catch (Exception e) {
            logger.warn("Failed as RDF/XML. " + e.getMessage());
            try {
                is.close();
                is = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8));
                model.read(is, null, "TURTLE");
                logger.warn("Read as TURTLE");
                return model;
            } catch (Exception e2) {
                logger.warn("Failed as TURTLE. " + e2.getMessage());
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
        s += "@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n";
        s += "@prefix odrl: <http://www.w3.org/ns/odrl/2/> .\n";
        s += "@prefix ldr: <http://purl.org/NET/ldr/ns#> .\n";
        s += "@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\n";
        s += "@prefix provo: <http://purl.org/net/provenance/ns#> .\n";
        s += "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n";
        s += "@prefix : <http://purl.org/NET/rdflicense/> .\n";
        return s;
    }
}
