package oeg.jodrlapi.helpers;


//APACHE COMMONS
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

/**
 * This class represents a resource with some metadata.
 * Under this simple model, objects may have zero or one metadata elements: comments, title, labels or seeAlso 
 * Every resource may have a URI too, whose default namespace has been made public to be changed at will
 * @author Victor Rodriguez Doncel at OEG-UPM 2014, 2020
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
@JsonPropertyOrder({"@context", "@id", "type", "text", "metadata"})
public class MetadataObject {
    
    //The possibly many rdfs:label
    public List<String> labels = new ArrayList();
    
    public String identifier = "";  //we shall take spdx
    public String comment = "";
    public String seeAlso="";
    public String title="";
    public String note = "";
    public String definition = "";
    public String source = "";
    
    //licenses that are similar or the same.
    public List<String> closeMatch = new ArrayList();
    
    
    @JsonProperty("@id")
    public String uri = "";
    

    /**
     * Builds an empty metadata object
     */
    public MetadataObject()
    {
        
    }
    
    /**
     * Creates an URI with a given URI
     * @param _uri Full URI of the metadata object. If an empty string is given, it will be considered anonymous
     */
    public MetadataObject(String _uri)
    {
        setURI(_uri);
    }
    
    public MetadataObject(MetadataObject mo)
    {
        comment = mo.comment;
        labels.addAll(mo.labels);
        seeAlso = mo.seeAlso;
        title = mo.title;
        uri = mo.uri;
        closeMatch = mo.closeMatch;
        identifier = mo.identifier;
        source = mo.source;
    }
    
    /**
     * Sets the URI
     * @param _uri Full URI of the metadata object. If an empty string is given, it will be considered anonymous
     */
    public void setURI(String _uri)
    {
        uri=_uri;
//        if (_uri.isEmpty())
//            anonymous=true;
    }
    
    /**
     * Gets the object's URI
     */
    public String getURI()
    {
        return uri;
    }
    
    /**
     * Gets if the object is anonymous
     * @return True if the metadata object is anonymous
     */
    @JsonIgnore
    public boolean isAnon()
    {
        return (uri.isEmpty());
    }
    
    /**
     * Gets a comment -supressing the language tag if it exists.
     * @return The comment
     */
    public String getComment()
    {
        String slabel=comment;
        int i =slabel.indexOf("@");
        if (i!=-1)
            slabel=slabel.substring(0, i);
        return slabel;
    }

    /**
     * Sets the comment of the object
     */
    public void setComment(String _comment) {
        comment = _comment;
    }
    
    /**
     * Gets the seeAlso of the object
     */
    public String getSeeAlso() {
        return seeAlso;
    }
    
    /**
     * Gets the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title
     * @param title
     */
    public void setTitle(String _title) {
        title = _title;
    }

    /**
     * Gets the label, after excluding the language tag.
     * If no label is given, gets the local name of the URI
     * rdfs:label is an instance of rdf:Property that may be used to provide a human-readable version of a resource's name.
     * @param lan Language of the label
     */
    public String getLabel(String lan)
    {
        if (!labels.isEmpty())
        {
            for(String label : labels)
            {
                String slabel=label;
                int i =label.indexOf("@");
                if (i!=-1)
                    slabel=slabel.substring(0, i);    
                return slabel;
            }
            return labels.get(0);
        }
        if (uri==null)
            return "";
        String baseName = FilenameUtils.getBaseName(uri);
        return baseName;
    }
    public void addLabel(String label) {
        labels.add(label);
    }
    
    public void setLabel(String label, String lang)
    {
        setLabel(label);
    }
    
    /**
     * Returns the identifier (spdx), if any.
     * @return spdx identifier
     */
    public String getIdentifier()
    {
        return identifier;
    }
    

    /**
     * Sets the only label of a metadata object
     * @param _label Label (default in English)
     */
    public void setLabel(String _label){
        labels.clear();
        labels.add(_label);
    }

    /***
     * Obtains a representative label of the individual.
     * If no label is present, the name is given
     * @return A descriptive string
     */
    @Override
    public String toString() {
        if (getLabel("")!=null && !getLabel("").isEmpty()) {
            return getLabel("");
        }
        return FilenameUtils.getBaseName(uri);
    }    

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }
    
}
