package oeg.rdflicense;
//import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.json.simple.JSONObject;
import vroddon.utils.URLutils;

/**
 *
 * @author Victor
 */
public class RDFLicense {

    public Model model = null;
    String uri = "";
    
    public RDFLicense()
    {
        
    }
    
    public String getLabel()
    {
        if (model==null)
            return "error";
        return RDFUtils.getFirstValue(model, uri, "http://www.w3.org/2000/01/rdf-schema#label");
    }

    public String getLegalCode()
    {
        if (model==null)
            return "error";
        return RDFUtils.getFirstValue(model, uri, "http://creativecommons.org/ns#legalcode");
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
        
}
