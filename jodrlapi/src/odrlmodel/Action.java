package odrlmodel;

//JAVA
import java.util.UUID;

//APACHE COMMONS
import org.apache.commons.io.FilenameUtils;

/**
 * This class represents an ODRL Action.
 * 
 * The Action entity (when related to a Permission entity) indicates the operations (e.g. play, copy, etc.) that the assignee (i.e. the consumer) 
 * is permitted to perform on the related Asset linked to by Permission. 
 * 
 * When related to a Prohibition, the Action entity indicates the operations that the assignee (again the consumer) is prohibited to perform on the Asset linked to by Prohibition. 
 * Analogously, when related to a Duty, it indicates the operation to be performed.
 * Action contains the following attribute:
 * name: indicates the Action entity term (REQUIRED)
 * As its value, the name attribute MAY take one of a set of Action names which are formally defined in profiles. 
 * 
 * The ODRL Common Vocabulary defines a standard set of potential terms that MAY be used. For example:
 * <ul>
 * <li>	http://www.w3.org/ns/odrl/2/write</li>
 * <li>	http://www.w3.org/ns/odrl/2/read</li>
 * <li>	http://www.w3.org/ns/odrl/2/distribute</li>
 * </ul>
 * 
 * Communities will develop new (or extend existing) profiles to capture additional and refined semantics.
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class Action extends MetadataObject {

    /**
     * Action constructor with a random URI in the default namespace. 
     * By default, an action will be like this: http://salonica.dia.fi.upm.es/ldr/action/2e7de960-7001-4c07-bde5-c5ad1f35133d
     */
    public Action() {
        uri = MetadataObject.DEFAULT_NAMESPACE + "action/" + UUID.randomUUID().toString();
    }

    /**
     * Cloner constructor
     */
    public Action(Action a) {
        super(a);
    }

    /**
     * Creates an action identified by the given URI. 
     * This Action might be one of those defined by ODRL, in which case label, 
     * comment, and seeAlso are loaded from the ODRL ontology
     * @param _uri URI of the action
     */
    public Action(String _uri) {
        super(_uri);
    }

}
