package odrlmodel;

//JAVA
import java.util.UUID;

/**
* This class represents an ODRL2.0 Constraint.
 * 
* The Constraint entity indicates limits and restrictions to the Permission, the Prohibition and the Duty entity. 
* Constraints express mathematical terms with two operands and one operator. 
* For example, the “number of usages” (name) must be “smaller than” (operator) the “number 10″ (rightOperand). 
* 
 * If multiple Constraint entities are linked to the same Permission, Prohibition, 
 * or Duty entity, then all of the Constraint entities MUST be satisfied. That is, 
 * all the Constraint entities are (boolean) anded. In the case where the same 
 * Constraint is repeated, then these MUST be represented as a single Constraint 
 * entity using an appropriate operator value (for example, isAnyOf).
 * 
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
*/
public class Constraint extends MetadataObject{

    
    protected String operator="";
    protected String rightOperand="";
    protected String value="";
    
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
    }
    
    /**
     * Creates an action identified by the given URI. 
     * This Action might be one of those defined by ODRL, in which case label, 
     * comment, and seeAlso are loaded from the ODRL ontology
     * @param _uri URI of the action
     */
    public Constraint(String _uri)
    {
        super(_uri);
    }
    
    /**
     * Sets the operator
     * @param _operator An operator, like http://www.w3.org/ns/odrl/2/gt or http://www.w3.org/ns/odrl/2/eq
     */
    public void setOperator(String _operator)
    {
        operator=_operator;
    }
    
    /**
     * Sets the rightOperand
     * @param _rightOperand A rightOperand, like http://www.w3.org/ns/odrl/2/language, http://www.w3.org/ns/odrl/2/industry, etc.
     */
    public void setRightOperand(String _rightOperand)
    {
        rightOperand=_rightOperand;
    }

    /**
     * Sets the vlue
     * @param _value Value, like http://www.lexvo.org/page/iso639-3/eng, 3, etc.
     */
    public void setValue(String _value)
    {
        value=_value;
    }
    
}
