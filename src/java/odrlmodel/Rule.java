package odrlmodel;

//JAVA
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//APACHE COMMONS
import org.apache.commons.io.FilenameUtils;

/**
 * This class represents a simple ODRL rule
 * Rule is an abstract common ancestor to Permissions, Prohibitions and Duties.
 * A Rule (reduced version of the ODRL Rule) is made of:
 * - Zero or more actions
 * - Zero or more constraints
 * - Zero or one assignee (party enjoying the permission/prohibition/duty)
 * - Zero or one target (object over which the rule applies)
 * - Zero or one assigner (party assigning the rule)
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class Rule extends MetadataObject {
    
    public final static int RULE_PERMISSION = 0;
    public final static int RULE_PROHIBITION = 1;
    public final static int RULE_DUTY = 2;
    
    private int ikind = 0; //either RULE_PERMISSION, RULE_PROHIBITION or RULE_DUTY

    
    public List<Action> actions = new ArrayList();

    public List<Constraint> constraints = new ArrayList();
    
    public String target="";   //Target of the rule (object over which the actions are exercised)

    public String assignee=""; //To whom the rule applies
    
    public String assigner=""; //Who is assigning the rule

    
    /**
     * Rule constructor with a random URI in the default namespace
     * A rule is by default a constructor
     */
    public Rule()
    {
        uri=MetadataObject.DEFAULT_NAMESPACE+"rule/" + UUID.randomUUID().toString();
    }
    
    
    /**
     * Gets the kind of the rule
     * @returns either Rule.RULE_PERMISSION, Rule.RULE_PROHIBITION or Rule.RULE_DUTY
     */
    public int getKindOfRule()
    {
        return ikind;
    }

    /**
     * Sets the kind of rule
     * @param _ikind either Rule.RULE_PERMISSION, Rule.RULE_PROHIBITION or Rule.RULE_DUTY
     */
    public void setKindOfRule(int _ikind) {
        ikind=_ikind;
    }
    
    
    /**
     * Gets an string describing the kind of the rule
     * @returns either "Permission", or "Prohibition" or "Duty" -empty if unkown kind.
     */
    public String getKindOfRuleString()
    {
        if (ikind==0)
            return "Permission";
        if (ikind==1)
            return "Prohibition";
        if (ikind==2)
            return "Duty";
        return "";
    }
    
    
    /**
     * Gets the actions associated to this rule
     */
    public List<Action> getActions()
    {
        return actions;
    }
    
    /**
     * Gets the list of constraints the rule has.
     * @return List of constraints
     */
    public List<Constraint> getConstraints()
    {
        return constraints;
    }
    


    public void addAction(Action a) {
        actions.add(a);
    }

    public void addConstraint(Constraint a) {
        constraints.add(a);
    }

    /**
     * Sets the assignee of the rule
     * @param _assignee
     */
    public void setAssignee(String _assignee) {
        assignee=_assignee;
    }

    /**
     * Gets the assignee of the rule.
     * @return The assignee
     */
    public String getAssignee()
    {
        return assignee;
    }
    
    
    /**
     * Sets the assigner of the rule
     * @param _assigner
     */
    public void setAssigner(String _assigner) {
        assigner=_assigner;
    }

    /**
     * Gets the assigner of the rule.
     * @return The assigner
     */
    public String getAssigner()
    {
        return assigner;
    }    
    
    public String toString() {
        if (getLabel("").isEmpty())
            return getLabel("");
        return FilenameUtils.getBaseName(uri);
    }

    public void setTarget(String target1) {
        target=target1;
    }

}
