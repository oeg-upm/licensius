package oeg.rdflicense;
//import org.apache.jena.rdf.model.Model;
//import org.apache.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import oeg.licensius.core.Licensius;
import oeg.vroddon.util.URLutils;
import org.json.simple.JSONObject;

/**
 * This class offers methods to easily manipulate RDFLicenses: licenses as RDF, as those present in http://rdflicense.linkeddata.es/
 * @author Victor
 */
public class RDFLicense {

    Model model = null;
    String uri = "";
    
    public RDFLicense()
    {
        
    }
    
    public RDFLicense(String _uri)
    {
        uri = _uri;
        model = getModel();
    }
    
    public RDFLicense(Model _model)
    {
        model=_model;
    }
    
    public Model getModel() {
        if (model!=null)
            return model; 
        try {
            String urittl = uri+".ttl";
            String txt = URLutils.browseSemanticWeb(urittl);
            if (txt==null) 
                txt="asdf";
            System.out.println(txt);
            model = ModelFactory.createDefaultModel();
            InputStream is = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8));
            model.read(is, null, "TURTLE");
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return model;
    }    
    
    public String getTitle()
    {
        if (model==null)
            return "error";
        return RDFUtils.getFirstValue(model, uri, "http://www.w3.org/2000/01/rdf-schema#label");
    }

    public String getPublisher()
    {
        if (model==null)
            return "error";
        return RDFUtils.getFirstValue(model, uri, "http://purl.org/dc/terms/publisher");
    }

    public String getVersion()
    {
        if (model==null)
            return "error";
        return RDFUtils.getFirstValue(model, uri, "http://purl.org/dc/terms/hasVersion");
    }
    
    public String getLabel()
    {
        if (model==null)
            return "error";
        return RDFUtils.getFirstValue(model, uri, "http://www.w3.org/2000/01/rdf-schema#label");
    }

    public String getLegalCodeLanguages()
    {
        if (model==null)
            return "error";
        return RDFUtils.getAllLanguages(model, uri, "http://creativecommons.org/ns#legalcode");
    }
    public String getLegalCode(String lan)
    {
        if (model==null)
            return "error";
        return RDFUtils.getFirstLiteral(model, uri, "http://creativecommons.org/ns#legalcode", lan);
    }
    public String getSeeAlso()
    {
        if (model==null)
            return "not found";
        return RDFUtils.getFirstValue(model, uri, "http://www.w3.org/2000/01/rdf-schema#seeAlso");
    }

    public String getURI()
    {
        return uri;
    }
    
    public String toString()
    {
        return uri;
    }
    
    public String toMicroJSON()
    {
        String json ="";
        try {
            JSONObject obj = new JSONObject();
            obj.put("uri", uri);
            obj.put("title", getLabel());
            obj.put("source", getSeeAlso());
            json = obj.toString();
        } catch (Exception e) {
            json = "error";
        }
        return json;
        
    }
    
    
    public String toJSON()
    {
        String json ="";
        try {
            JSONObject obj = new JSONObject();
            obj.put("uri", getSeeAlso());
            obj.put("title", getLabel());
            obj.put("rdflicense", uri);
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
        if (model!=null)
            model.write(sw, "TURTLE");
        return sw.toString();
    }
 
    public String toLongJSON()
    {
        String json ="";
        try {
            JSONObject obj = new JSONObject();
            obj.put("uri", uri);
            obj.put("title", getTitle());
            obj.put("version", getVersion());
            obj.put("seeAlso", getSeeAlso());
            obj.put("label", getLabel());
            obj.put("legalcode", getLegalCodeLanguages());
            json = obj.toString();
        } catch (Exception e) {
            json = "error";
        }
        return json;
    }    

    public String getOriginalText() {
        String urittl = uri+".ttl";
        String txt = URLutils.browseSemanticWeb(urittl);
        return txt;
    }
    
    

    
    public static void main(String[] args) {
        Licensius.init();
        RDFLicense lic = new RDFLicense("http://purl.org/NET/rdflicense/ukogl-nc2.0");
        String label = lic.getLegalCodeLanguages();
        String legalcode = lic.getLegalCode("en");
        System.out.println(legalcode);
    }

    /**
     * Gets an RDFXML representation of the RDFLicense
     */
    public String toRDFXML() {
        StringWriter sw = new StringWriter();
        if (model != null) {
            model.write(sw, "RDF/XML");
        }
        return sw.toString();
    }

    /**
     * Gets the URI for the legal code or an empty string if non-existing
     */
    public String getLegalCode() {
        if (model == null) {
            return "";
        }
        return RDFUtils.getFirstValue(model, uri, "http://creativecommons.org/ns#legalcode");
    }

    public String getSource() {
        if (model == null) {
            return "";
        }
        return RDFUtils.getFirstValue(model, uri, "http://purl.org/dc/terms/source");
    }
    public static Comparator<RDFLicense> COMPARE_PUBLISHER = new Comparator<RDFLicense>() {
        public int compare(RDFLicense o1, RDFLicense o2) {
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            String s1 = o1.getPublisher();
            if (s1.isEmpty()) {
                s1 = o1.uri;
            }
            String s2 = o2.getPublisher();
            if (s2.isEmpty()) {
                s2 = o2.uri;
            }
            return s1.compareTo(s2);
        }
    };    
}
