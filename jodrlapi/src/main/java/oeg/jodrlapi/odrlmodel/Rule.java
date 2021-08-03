package oeg.jodrlapi.odrlmodel;


//JAVA
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import oeg.jodrlapi.helpers.MetadataObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import oeg.jodrlapi.JODRLApiSettings;
import oeg.jodrlapi.helpers.ODRLRDF;
import static oeg.jodrlapi.odrlmodel.Policy.POLICY_SET;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

//APACHE COMMONS
//import org.apache.commons.io.FilenameUtils;

/**
 * This class represents a simple ODRL rule
 * Rule is an abstract common ancestor to Permissions, Prohibitions and Duties.
 * A Rule (reduced version of the ODRL Rule) is made of:
 - Zero or more actions
 - Zero or more constraint
 - Zero or one assignee (party enjoying the permission/prohibition/duty)
 - Zero or one target (object over which the rule applies)
 - Zero or one assigner (party assigning the rule)
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class Rule extends MetadataObject {
    
    public final static int RULE_PERMISSION = 0;
    public final static int RULE_PROHIBITION = 1;
    public final static int RULE_DUTY = 2;
    
    private int ikind = 0; //either RULE_PERMISSION, RULE_PROHIBITION or RULE_DUTY

    @JsonProperty("action")
    public List<Action> actions = new ArrayList();

    public List<Constraint> constraint = new ArrayList();
    
    public List<Rule> consequence = new ArrayList();
    
    public List<Rule> remedy = new ArrayList();
    
    public String target="";   //Target of the rule (object over which the actions are exercised)

    protected Party assignee=null; //To whom the rule applies
    
    protected Party assigner = null; //Who is assigning the rule

    
    /**
     * Rule constructor with a random URI in the default namespace
     * A rule is by default a constructor
     */
    public Rule()
    {
        uri=JODRLApiSettings.ODRL_NS+"rule/" + UUID.randomUUID().toString();
    }
    
    /**
     * Rule constructor with a given URI
     * @param _uri A valid URI. Can be an empty string (anonymous source)
     */
    public Rule(String _uri)
    {
        super(_uri);
    }
    
    
    
    
    /**
     * Gets the kind of the rule
     * @returns either Rule.RULE_PERMISSION, Rule.RULE_PROHIBITION or Rule.RULE_DUTY
     */
    @JsonIgnore
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
    @JsonIgnore
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
     * Sets the actions referred by this rule
     * @param _actions List of actions
     */
    public void setActions(List<Action> _actions)
    {
        actions.clear();
        actions.addAll(_actions);
    }
    
    /**
     * Gets the list of constraint the rule has.
     * @return List of constraint
     */
    public List<Constraint> getConstraint()
    {
        return constraint;
    }

    /**
     * Sets the constraint for this rule to apply
     * @param _constraints List of constratins
     */
    public void setConstraint(List<Constraint> _constraints)
    {
        constraint=_constraints;
    }    

    public void addAction(Action a) {
        actions.add(a);
    }

    public void addConstraint(Constraint a) {
        constraint.add(a);
    }

    /**
     * Sets the assignee of the rule
     * @param _assignee
     */
    public void setAssignee(Party _assignee) {
        assignee=_assignee;
    }

    /**
     * Gets the assignee of the rule.
     * @return The assignee
     */
    public Party getAssignee()
    {
        return assignee;
    }
    
    /**
     * Sets the assigner of the rule
     * @param _assigner
     */
    public void setAssigner(Party _assigner)
    {
        assigner = _assigner;
    }
    

    /**
     * Gets the assigner of the rule.
     * @return The assigner
     */
    public Party getAssigner()
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
    
    @JsonProperty("@type")
    public String getType() {
        if (ikind == 1)
            return "http://www.w3.org/ns/odrl/2/Prohibition";
        if (ikind == 2)
            return "http://www.w3.org/ns/odrl/2/Duty";
        return "http://www.w3.org/ns/odrl/2/Permission";
    }

    public ResourceModel getResourceModel()
    {
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(new AnonId());
        
        //TARGET, ASSIGNER, ASSIGNEE
        if (!target.isEmpty()) {
            resource.addProperty(ODRLRDF.PTARGET, target);
        }        
        if (getAssigner() != null) {
            Resource rparty = getAssigner().getResourceModel().resource;
//            Resource rparty = ODRLRDF.getResourceFromParty(getAssigner());
            resource.addProperty(ODRLRDF.PASSIGNER, rparty);
            model.add(rparty.getModel());
        }
        if (getAssignee() != null) {
            Resource rparty = getAssignee().getResourceModel().resource;
//            Resource rparty = ODRLRDF.getResourceFromParty(getAssignee());
            resource.addProperty(ODRLRDF.PASSIGNEE, rparty);
            model.add(rparty.getModel());
        }
        //ACTIONS
        for (Action a : actions) {
            ResourceModel rm = a.getResourceModel();
            resource.addProperty(ODRLRDF.PACTION, rm.resource);
            model.add(rm.model);
        }
        if (getKindOfRule() == Rule.RULE_PERMISSION) {
            resource.addProperty(RDF.type, ODRLRDF.RPERMISSION);
        }
        if (getKindOfRule() == Rule.RULE_DUTY) {
            resource.addProperty(RDF.type, ODRLRDF.RDUTY);
        }
        if (getKindOfRule() == Rule.RULE_PROHIBITION) {
            resource.addProperty(RDF.type, ODRLRDF.RPROHIBITION);
        }        

        //CONSTRAINTS
        for (Constraint c : constraint) {
            Resource rconstraint = ODRLRDF.getResourceFromConstraint(c);
            resource.addProperty(ODRLRDF.PCONSTRAINT, rconstraint);
            model.add(rconstraint.getModel());
        }

        //DUTIES (ONLY FOR PERMISSIONS)
        if (getClass().equals(Permission.class)) {
            Permission permiso = (Permission) this;
            List<Duty> duties = permiso.getDuties();
            for (Duty duty : duties) {
                ResourceModel rm = duty.getResourceModel();
                Resource rduty = rm.resource;
//                Resource rduty = ODRLRDF.getResourceFromRule(duty);
                model.add(rduty.getModel());
                resource.addProperty(ODRLRDF.PDUTY, rduty);
            }
        }        
        return new ResourceModel(resource, model);
    }

}
