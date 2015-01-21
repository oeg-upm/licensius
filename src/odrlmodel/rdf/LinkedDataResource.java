package odrlmodel.rdf;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import odrlmodel.rdf.RDFUtils;

/**
 * A Linked Data Resource is anything identifiable with an URI, 
 * possibly with some common metadata elements
 * @author Víctor Rodríguez
 */
public class LinkedDataResource {

    /// A Resource in Linked Data is basically a URI
    public URI uri=null;
    
    /// The Jena resource 
    public Resource resource = null;
    
    /// The supporting model
    Model model = null;

    
    /**
     * Creates a Linked Data resource, identifiable by its URI.
     */
    public LinkedDataResource(Model _model, String _uri)
    {
        model = _model;
        boolean ok=setURI(_uri);
        if (ok)
            resource = _model.createResource(_uri);

    }

    /**
     * Gets the resource URI
     * @returns an URI
     */
    public URI getURI()
    {
        return uri;
    }
    
    /**
     * Sets the resource URI
     * @param _uri String with the URI
     * @return True if the URI is well formed and correct
     */
    public boolean setURI(String _uri)
    {
        try {
            uri = new URI(_uri);
            return true;
        } catch (URISyntaxException ex) {
             Logger.getLogger("ldr").info("Malformed URI");
             return false;
        }
    }
    
    /**
     * Sets the title (dct:title)
     * There can be only one title.
     * @param _title Title to be set
     */
    public void setTitle(String _title) {
        if (resource==null)
            return;
        if (resource.hasProperty(RDFUtils.TITLE))
            resource.removeAll(RDFUtils.TITLE);
        resource.addProperty(RDFUtils.TITLE, _title);
    }

    /**
     * Gets the title (dct:title)
     * There can be zero or one titles.
     * @return String with the title, an empty string is none.
     */
    public String getTitle() {
        return RDFUtils.getFirstPropertyValue(resource, RDFUtils.TITLE);
    }    

    
    /**
     * Gets the seeAlso (rdfs:seeAlso)
     * There can be zero or one titles.
     * @return String with the title, an empty string is none.
     */
    public String getSeeAlso() {
        return RDFUtils.getFirstPropertyValue(resource, RDFUtils.SEEALSO);
    }    
    
    /**
     * Gets the label (rdfs:label)
     * There can be zero or one label
     * @return String with the label, empty if none
     */
    public String getLabel() {
        return RDFUtils.getFirstPropertyValue(resource, RDFUtils.LABEL);
    }    
    
    /**
     * Sets the label
     * @param label The label
     */
    public void setLabel(String _label) {
        if (resource.hasProperty(RDFUtils.LABEL))
            resource.removeAll(RDFUtils.LABEL);
        resource.addProperty(RDFUtils.LABEL, _label);
    }
    
    /**
     * Gets the comment. There might be zero or one comment.
     * @return The comment or an empty string.
     */
    public String getComment() {
        return RDFUtils.getFirstPropertyValue(resource, RDFUtils.COMMENT);
    }    
    
    /**
     * Sets the comment. 
     * If there was one, it is overwritten.
     * @param _comment
     */
    public void setComment(String _comment) {
        if (resource.hasProperty(RDFUtils.COMMENT))
            resource.removeAll(RDFUtils.COMMENT);
        resource.addProperty(RDFUtils.COMMENT, _comment);
    }

    /**
     * Obtiene el modelo
     */
    public Model getModel()
    {
        return model;
    }
    
    
    /********************** PROTECTED AND PRIVATE METHODS *************************/    

    /**
     * Obtains the Jena resource
     */
    public Resource getResource()
    {
        return resource;
    }
    
    @Override
    public String toString()
    {
        return getURI().toString();
    }    
}
