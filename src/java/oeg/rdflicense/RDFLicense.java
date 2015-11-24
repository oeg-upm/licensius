package oeg.rdflicense;
//import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import main.Licensius;
import org.json.simple.JSONObject;
import vroddon.utils.URLutils;

/**
 *
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
    
    
    public Model getModel() {
        if (model!=null)
            return model; 
        try {
//            model.read(uri);
            String urittl = uri+".ttl";
            String txt = URLutils.browseSemanticWeb(urittl);
            model = ModelFactory.createDefaultModel();
            InputStream is = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8));
            model.read(is, null, "TURTLE");
            //model.read("C:\\Users\\vrodriguez\\Desktop\\a.ttl", null, "TURTLE");
            is.close();
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return model;
    }
    
    public static void main(String[] args) {
        Licensius.initLogger();
        RDFLicense lic = new RDFLicense("http://purl.org/NET/rdflicense/ukogl-nc2.0");
        String label = lic.getLegalCodeLanguages();
        
//        String label = lic.getLabel();
//        System.out.println("LABEL: " + label);
//        System.out.println("=====\n"+lic.toTTL());
    }

    
}
