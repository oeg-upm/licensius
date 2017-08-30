package odrlmodel;

/**
 * This class represents an ODRL2.0 Duty 
 * The Duty entity indicates a requirement that MAY be fulfilled in return for 
 * being entitled to the referring Permission entity. 
 * 
 * While implying different semantics, the Duty entity is similar to Permission 
 * in that it is an Action that can be undertaken. If a Permission refers to several 
 * Duty entities, all of them have to be fulfilled for the Permission to become valid. 
 * If several Permission entities refer to one Duty, then the Duty only has to be 
 * fulfilled once for all the Permission entities to become valid.
 * @version Suitable for ODRL 2.1
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class Duty extends Rule {
    
    public Duty()
    {
        setKindOfRule(Rule.RULE_DUTY);
    }        

    /**
     * Creates a duty identified by the given URI. 
     * @param _uri URI of the duty
     */
    public Duty(String _uri) {
        super(_uri);
        setKindOfRule(Rule.RULE_DUTY);
    }
    
    
}
