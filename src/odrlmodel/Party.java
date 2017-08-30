package odrlmodel;

//JAVA
import java.util.UUID;

//APACHE COMMONS
import org.apache.commons.io.FilenameUtils;

/**
 * This class represents an ODRL2.0 Party
 * The Party entity is aimed at identifying a person, group of people, or organisation. 
 * The Party MUST identify a (legal) entity that can participate in policy transactions
 * @author Victor
 */
public class Party extends MetadataObject {
    /**
     * Party constructor with a random URI in the default namespace. 
     * By default, an party will be like this: http://salonica.dia.fi.upm.es/ldr/party/2e7de960-7001-4c07-bde5-c5ad1f35133d
     */
    public Party() {
        uri = MetadataObject.DEFAULT_NAMESPACE + "party/" + UUID.randomUUID().toString();
    }

    /**
     * Cloner constructor
     */
    public Party(Party a) {
        super(a);
    }

    /**
     * Creates an party identified by the given URI. 
     * This Party might be one of those defined by ODRL, in which case label, 
     * comment, and seeAlso are loaded from the ODRL ontology
     * @param _uri URI of the party
     */
    public Party(String _uri) {
        super(_uri);
    }    
}
