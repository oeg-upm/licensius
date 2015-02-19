package odrlmodel;

//JAVA
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//APACHE COMMONS
import org.apache.commons.io.FilenameUtils;

/**
 * Policy represents an ODRL policy, supporting a reduced set of the features defined in the ODRL2.0 specification
 * 
 * The Policy entity is the top-level entity.
 * 
 * An abstract common ancestor to Permissions, Prohibitions and Duties.
 * @seeAlso For more information, check http://www.w3.org/community/odrl/two/model/#section-21
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class Policy extends MetadataObject {

    //A policy is made of one or more rules
    protected List<Rule> rules = new ArrayList();

    //If it is to be stored somewhere
    protected String fileName = "";

    //Kind of policy it is
    private int type = POLICY_SET;
    
    
    public static final int POLICY_SET = 0; //DEFAULT
    public static final int POLICY_REQUEST = 1;
    public static final int POLICY_OFFER = 2;
    public static final int POLICY_CC = 3;
    public static final int POLICY_AGREEMENT = 4;
    public static final int POLICY_TICKET = 5;
    

    /**
     * Policy constructor with a random URI in the default namespace
     * A policy is by default a Set policy
     */
    public Policy() {
        uri = MetadataObject.DEFAULT_NAMESPACE + "policy/" + UUID.randomUUID().toString();
    }

    /**
     * Policy constructor with a given URI 
     * A policy is by default a Set policy
     * @param _uri Given URI
     */
    public Policy(String _uri) {
        uri = _uri;
    }

    public String toString()
    {
        if (!getTitle().isEmpty())
            return getTitle();
        if (!getLabel("en").isEmpty())
            return getLabel("en");
        return uri;
    }
    
    
    /**
     * Gets the type of the Policy
     * @return one of: Policy.POLICY_SET, Policy.POLICY_REQUEST, Policy.POLICY_OFFER
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type of the policy
     * @param _type, one of: Policy.POLICY_SET, Policy.POLICY_REQUEST, Policy.POLICY_OFFER
     */
    public void setType(int _type) {
        type = _type;
    }

    /**
     * Adds a rule to the policy
     * @param r Rule to be added
     */
    public void addRule(Rule r) {
        rules.add(r);
    }

    /**
     * Gets the list of rules this policy has
     * @return List of rules
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * Sets the list of rules
     * @param _rules List of rules
     */
    public void setRules(List<Rule> _rules) {
        rules = _rules;
    }

    /**
     * Sets a certain target in all the rules
     * @param targetName Name of the target
     */
    public void setTargetInAllRules(String targetName) {
        for (Rule r : rules) {
            r.target = targetName;
        }
    }

    /**
     * Sets a certain assignee in all the rules
     * @param assignee Party assigner
     */
    public void setAssigneeInAllRules(Party assignee) {
        for (Rule r : rules) {
            r.assignee = assignee;
        }
    }

    /**
     * Sets a certain assigner in all the rules
     * @param assigner Party assigner
     */
    public void setAssignerInAllRules(Party assigner) {
        for (Rule r : rules) {
            r.assigner = assigner;
        }
    }
    
}
