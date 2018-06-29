package odrlmodel;

//JAVA
import java.util.UUID;

/**
* This class represents an ODRL Constraint.
* The Constraint entity indicates limits and restrictions to the Permission, the Prohibition and the Duty entity. 
* Constraints express mathematical terms with two operands and one operator. 
* For example, the “number of usages” (name) must be “smaller than” (operator) the “number 10″ (rightOperand). 
* @author Victor Rodriguez Doncel at OEG-UPM 2014
*/
public class Constraint extends MetadataObject{

    public Action action = null;

    /** 
     * Default constraint with a random URI
     */
    public Constraint()
    {
        uri=MetadataObject.DEFAULT_NAMESPACE+"constraint/" + UUID.randomUUID().toString();
    }
    
    /**
     * Cloner constructor
     */
    public Constraint(Constraint copia)
    {
        super(copia);
        
        action=copia.action;
    }
    
    /**
     * Creates an action identified by the given URI. 
     * This Action might be one of those defined by ODRL, in which case label, 
     * comment, and seeAlso are loaded from the ODRL ontology
     * @param _uri URI of the action
     */
    public Constraint(String _uri)
    {
        uri = _uri;
    }
    
    
    
    
    public String toString()
    {
        String label=getLabel("en");
        return label;
    }
    

    /**
     * Decides whether the access is permitted or not, exclusively based on the price.
     * @return true if the access is open
     */
    public boolean isOpen() {
        return true;
/*        if (amount!=null && amount!=0)
            return false;
        return true;*/
    }
        
    
}
