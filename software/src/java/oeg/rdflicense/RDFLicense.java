package oeg.rdflicense;

import java.io.StringWriter;
import java.util.Comparator;
import odrlmodel.ODRLRDF;
import odrlmodel.Policy;

//JENA
import com.hp.hpl.jena.rdf.model.Model;

//JSONSIMPLE
import org.json.simple.JSONObject;

/**
 * This class represents a license in RDF.
 * 
 * HOW TO DEPLOY IN GOOGLE APP ENGINE
 * ==================================
 * If this project is to be deployed in google app engine, then it will have to be compiled with Java SDK 7.
 * Fix both the PATH environment variable and the JAVA_HOME to point to the Java 7 SDK.
 * Use the gcloud command, or upload the app by using the plugin in Netbeans7
 * Also, the following excerpt must be present in the web.xml
 *     <servlet>
 *        <servlet-name>default</servlet-name>
 *       <servlet-class>oeg.rdflicense.DefaultWrapperServlet</servlet-class> 
 *    </servlet> 
 * 
 * 
 * HOW TO DEPLOY IN TOMCAT
 * ======================
 * Make sure that the excerpt above is not in the web.xml
 * 
 * @author Victor
 */
public class RDFLicense {

    public Model model = null;
    String uri = "";
    
    /**
     * Builds an empty RDFlicense
     */
    public RDFLicense()
    {
        
    }

    public RDFLicense(Model m)
    {
        model = m;
    }

    /**
     * Obtains a Java policy object
     */
    public Policy getPolicy()
    {
        String ttl = toTTL();
        return ODRLRDF.getPolicy(ttl);
    }
    
    /**
     * Gets the label for the RDF license, or an empty string if non-existing
     */
    public String getLabel()
    {
        if (model==null)
            return "";
        return RDFUtils.getFirstValue(model, uri, "http://www.w3.org/2000/01/rdf-schema#label");
    }
    
    public String getPublisher()
    {
        if (model==null)
            return "";
        String s= RDFUtils.getFirstValue(model, uri, "http://purl.org/dc/terms/publisher");
        if (s.isEmpty())
            s="Unknown";
        return s;
    }
    
    public String getVersion() {
        if (model==null)
            return "";
        return RDFUtils.getFirstValue(model, uri, "http://purl.org/dc/terms/hasVersion");
    }
    

    /**
     * Gets the URI for the legal code or an empty string if non-existing
     */
    public String getLegalCode()
    {
        if (model==null)
            return "";
        return RDFUtils.getFirstValue(model, uri, "http://creativecommons.org/ns#legalcode");
    }
    
    
    /**
     * Gets the official URI of the license
     */
    public String getURI()
    {
        return uri;
    }    
    /**
     * Gets the official URI of the license
     */
    public String getSeeAlso()
    {
        if (model==null)
            return "error";
        return RDFUtils.getFirstValue(model, uri, "http://www.w3.org/2000/01/rdf-schema#seeAlso");
    }
    
    /**
     * Gets the URI of the license
     */
    public String toString()
    {
        return uri;
    }
    
    /**
     * Gets a JSON representation of the most important elements of the license. Incomplete.
     */
    public String toJSON()
    {
        String json ="";
        try {
            JSONObject obj = new JSONObject();
            obj.put("rdf", uri);
            obj.put("uri", getLegalCode());
            obj.put("title", getLabel());
            json = obj.toString();
        } catch (Exception e) {
            json = "error";
        }
        return json;
    }
    
    /**
     * Gets a TTL representation of the RDFLicense
     */
    public String toTTL()
    {
        StringWriter sw = new StringWriter();
        model = RDFUtils.cleanUnusedPrefixes(model);
        if (model!=null)
            model.write(sw, "TURTLE");
        return sw.toString();
    }
    
    /**
     * Gets an RDFXML representation of the RDFLicense
     */
    public String toRDFXML()
    {
        StringWriter sw = new StringWriter();
        if (model!=null)
            model.write(sw, "RDF/XML");
//        RDFDataMgr.write(sw, model, Lang.RDFXML); //does not work in APPEngine
        return sw.toString();
    }
    public static Comparator<RDFLicense> COMPARE_PUBLISHER = new Comparator<RDFLicense>() {
        public int compare(RDFLicense o1, RDFLicense o2) {
            if (o1==null)
                return -1;
            if(o2==null)
                return 1;
            String s1 = o1.getPublisher();
            if (s1.isEmpty()) s1=o1.uri;
            String s2 = o2.getPublisher();
            if (s2.isEmpty()) s2=o2.uri;
            return s1.compareTo(s2);
        }
    };    

    public static Comparator<RDFLicense> COMPARE_LABEL = new Comparator<RDFLicense>() {
        public int compare(RDFLicense o1, RDFLicense o2) {
            String s1 = o1.getLabel();
            if (s1.isEmpty()) s1=o1.uri;
            String s2 = o2.getLabel();
            if (s2.isEmpty()) s2=o2.uri;
            return s1.compareTo(s2);
        }
    };    

}
