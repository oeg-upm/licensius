package ldserver;

import java.util.Comparator;
import org.json.simple.JSONObject;

/**
 *
 * @author Victor
 */
public class Recurso implements Comparator<Recurso>{
    private String uri ="";
    private String label ="";
    private String comment ="";

    public Recurso()
    {
    }
    
    public Recurso(String s)
    {
        uri=s;
    }
    
    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    public int compare(Recurso o1, Recurso o2) {
        if (o1.uri.equals(o2.uri))
            return 0;
        else return 1;
    }

    public JSONObject toJSON()
    {
        JSONObject res = new JSONObject();
        res.put("label", getLabel());
        res.put("link", getUri());
        res.put("comment", getComment());
        return res;
    }    
    
}
