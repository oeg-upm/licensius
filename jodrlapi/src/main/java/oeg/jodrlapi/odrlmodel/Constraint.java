package oeg.jodrlapi.odrlmodel;



//JAVA
import oeg.jodrlapi.helpers.MetadataObject;
import java.util.UUID;
import oeg.jodrlapi.JODRLApiSettings;
import oeg.jodrlapi.helpers.ODRLRDF;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
* This class represents an ODRL2.0 Constraint.
 * 
* The Constraint entity indicates limits and restrictions to the Permission, the Prohibition and the Duty entity. 
 Constraints express mathematical terms with two operands and one operator. 
 For example, the “number of usages” (name) must be “smaller than” (operator) the “number 10″ (leftOperand). 
 
 If multiple Constraint entities are linked to the same Permission, Prohibition, 
 or Duty entity, then all of the Constraint entities MUST be satisfied. That is, 
 all the Constraint entities are (boolean) anded. In the case where the same 
 Constraint is repeated, then these MUST be represented as a single Constraint 
 entity using an appropriate operator rightOperand (for example, isAnyOf).
 * 
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
*/
public class Constraint extends MetadataObject{

    
    public String operator="";
    public String leftOperand="";
    public String rightOperand="";
    
    /** 
     * Default constraint with a random URI
     */
    public Constraint()
    {
        uri = "";
//        uri=JODRLApiSettings.ODRL_NS+"constraint/" + UUID.randomUUID().toString().substring(0,8);
        
    }
    
    public Constraint(String l, String o, String r)
    {
        this();
        leftOperand = ODRLRDF.unroll(l);
        operator = ODRLRDF.unroll(o);;
        rightOperand = r;
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
     * Sets the leftOperand
     * @param _rightOperand A leftOperand, like http://www.w3.org/ns/odrl/2/language, http://www.w3.org/ns/odrl/2/industry, etc.
     */
    public void setLeftOperand(String _rightOperand)
    {
        leftOperand=_rightOperand;
    }

    /**
     * Sets the vlue
     * @param _value Value, like http://www.lexvo.org/page/iso639-3/eng, 3, etc.
     */
    public void setRightOperand(String _value)
    {
        rightOperand=_value;
    }
    
    public ResourceModel getResourceModel()
    {
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(new AnonId()); //antes era geturi
        model.add(resource, ODRLRDF.POPERATOR, ModelFactory.createDefaultModel().createResource(operator));
        model.add(resource, ODRLRDF.PLEFTOPERAND, ModelFactory.createDefaultModel().createResource(leftOperand));
        if (rightOperand.startsWith("http"))
            model.add(resource, ODRLRDF.PRIGHTOPERAND, ModelFactory.createDefaultModel().createResource(rightOperand));
        else
            model.add(resource, ODRLRDF.PRIGHTOPERAND, ModelFactory.createDefaultModel().createLiteral(rightOperand));
        ResourceModel rm = new ResourceModel(resource,model);
        return rm;
    }
    
}
