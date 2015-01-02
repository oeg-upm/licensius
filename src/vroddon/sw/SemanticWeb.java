package vroddon.sw;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;

/**
 *
 * @author Victor
 */
public class SemanticWeb {
    
    
    //DCAT
    public static Resource DATASET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/dcat#Dataset");
    public static Resource DISTRIBUTION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/dcat#Distribution");
    public static Property pDISTRIBUTION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/dcat#distribution");

    //DUBLINCORE
    public static Property IDENTIFIER = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/identifier");
    public static Property FORMAT = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/format");
    public static Property TITLE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/title");
    public static Property LICENSE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/license");
    public static Property RIGHTS = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/rights");
    public static Property DESCRIPTION = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/rights");
    public static Property SOURCE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/source");
    
    //DBPEDIA
    public static Property ALIVE = ModelFactory.createDefaultModel().createProperty("http://dbpedia.org/property/alive");
    
    //VOAF
    public static Resource VOCABULARY = ModelFactory.createDefaultModel().createProperty("http://purl.org/vocommons/voaf#Vocabulary");   
    
    //VANN
     public static Property PREFIX = ModelFactory.createDefaultModel().createProperty("http://purl.org/vocab/vann/preferredNamespacePrefix");   
    
    /**
     * Convierte un modelo Jena en una cadena de texto
     */
    public static String toString(Model model)
    {
        if (model==null)
            return "";
        ByteArrayOutputStream sos = new ByteArrayOutputStream();
        model.write(sos, "TTL") ;
        String s="";
        try {
            s = sos.toString("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger("licenser").info("error in " + ex.getLocalizedMessage());
        }
        return s;
    }    

    
    /**
     * Carga un modelo Jena de una cadena de texto
     */
    public static Model fromString(String s)
    {
        InputStream stream=null;
        try {
            stream = new ByteArrayInputStream(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger("licenser").info("error in " + ex.getLocalizedMessage());
        }
        Model model = ModelFactory.createDefaultModel();
        model = model.read(stream,"","TTL");
        return model;

    }    
        
    
}
