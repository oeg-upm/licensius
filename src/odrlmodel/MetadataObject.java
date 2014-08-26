package odrlmodel;

//JAVA
import java.net.URI;

//APACHE COMMONS
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

/**
 * This class represents a resource with some metadata.
 * Under this simple model, objects may have zero or one metadata elements: comments, title, labels or seeAlso 
 * Every resource may have a URI too, whose default namespace has been made public to be changed at will
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class MetadataObject {
    public static final String DEFAULT_NAMESPACE = LDRConfig.getNamespace(); 
    
    //The possibly many rdfs:label
    public List<String> labels = new ArrayList();
    
    public String comment = "";
    public String seeAlso="";
    public String title="";
    public String uri = "";

    public MetadataObject()
    {
        
    }
    
    public MetadataObject(MetadataObject mo)
    {
        comment = mo.comment;
        labels.addAll(mo.labels);
        seeAlso = mo.seeAlso;
        title = mo.title;
        uri = mo.uri;
    }
    
    
    /**
     * Sets the URI
     */
    public void setURI(String _uri)
    {
        uri=uri;
    }
    
    /**
     * Gets the object's URI
     */
    public String getURI()
    {
        return uri;
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

    public void setLabel(String label){
        labels.clear();
        labels.add(label);
    }

}
