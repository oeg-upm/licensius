package odrlmodel;

/**
 * Represents an ODRL2.0 Prohibition.
 * 
 * The Prohibition entity indicates the Actions that the assignee is prohibited 
 * to perform on the related Asset [ODRL-REQ-1.7]. Prohibitions are issued by the 
 * supplier of the Asset – the Party with the Role assigner. If several Prohibition 
 * entities are referred to by a Policy, all of them are valid.
 * 
 * @author Victor
 */
public class Prohibition extends Rule {

    /**
     * Prohibition constructor
     * Prohibitions are by default anonymous
     */
    public Prohibition()
    {
        super(""); 
        setKindOfRule(Rule.RULE_PROHIBITION);
    }    
    
    /**
     * Creates a prohibition identified by the given URI. 
     * @param _uri URI of the prohibition
     */
    public Prohibition(String _uri) {
        super(_uri);
        setKindOfRule(Rule.RULE_PROHIBITION);
    }    
    
}
