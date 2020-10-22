package oeg.jodrlapi.odrlmodel;

//JAVA
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import oeg.jodrlapi.helpers.MetadataObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Policy represents an ODRL policy, supporting a reduced set of the features
 * defined in the ODRL2.0 specification
 *
 * The Policy entity is the top-level entity.
 *
 * An abstract common ancestor to Permissions, Prohibitions and Duties.
 *
 * @seeAlso For more information, check
 * http://www.w3.org/community/odrl/two/model/#section-21
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
@JsonIgnoreProperties(ignoreUnknown = true, value={"context"})
@JsonInclude(Include.NON_EMPTY)
public class Policy extends MetadataObject {

    //A policy is made of one or more rules
    public List<Rule> rules = new ArrayList();

    //If it is to be stored somewhere
    @JsonIgnore
    public String fileName = "";

    //Kind of policy it is
    @JsonIgnore
    private int typeofpolicy = POLICY_SET;

    public static final int POLICY_SET = 0; //DEFAULT
    public static final int POLICY_REQUEST = 1;
    public static final int POLICY_OFFER = 2;
    public static final int POLICY_CC = 3;
    public static final int POLICY_AGREEMENT = 4;
    public static final int POLICY_TICKET = 5;

    /**
     * Policy constructor with a random URI in the default namespace A policy is
     * by default a Set policy
     */
    public Policy() {
        uri = MetadataObject.DEFAULT_NAMESPACE + "policy/" + UUID.randomUUID().toString();
    }

    /**
     * Policy constructor with a given URI A policy is by default a Set policy
     *
     * @param _uri Given URI
     */
    public Policy(String _uri) {
        uri = _uri;
    }

    public String toString() {
        if (!getTitle().isEmpty()) {
            return getTitle();
        }
        if (!getLabel("en").isEmpty()) {
            return getLabel("en");
        }
        return uri;
    }

    /**
     * Gets the typeofpolicy of the Policy
     *
     * @return one of: Policy.POLICY_SET, Policy.POLICY_REQUEST,
     * Policy.POLICY_OFFER
     */
    public int getTypeofpolicy() {
        return typeofpolicy;
    }

    /**
     * Sets the typeofpolicy of the policy
     *
     * @param _type, one of: Policy.POLICY_SET, Policy.POLICY_REQUEST,
     * Policy.POLICY_OFFER
     */
    public void setTypeofpolicy(int _type) {
        typeofpolicy = _type;
    }

    /**
     * Adds a rule to the policy
     *
     * @param r Rule to be added
     */
    public void addRule(Rule r) {
        rules.add(r);
    }

    /**
     * Gets the list of rules this policy has
     *
     * @return List of rules
     */
    @JsonIgnore
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * Sets the list of rules
     *
     * @param _rules List of rules
     */
    public void setRules(List<Rule> _rules) {
        rules = _rules;
    }

    /**
     * Sets a certain target in all the rules
     *
     * @param targetName Name of the target
     */
    public void setTargetInAllRules(String targetName) {
        for (Rule r : rules) {
            r.target = targetName;
        }
    }

    /**
     * Sets a certain assignee in all the rules
     *
     * @param assignee Party assigner
     */
    public void setAssigneeInAllRules(Party assignee) {
        for (Rule r : rules) {
            r.assignee = assignee;
        }
    }

    /**
     * Sets a certain assigner in all the rules
     *
     * @param assigner Party assigner
     */
    public void setAssignerInAllRules(Party assigner) {
        for (Rule r : rules) {
            r.assigner = assigner;
        }
    }

    public String toJSONLD() {
        String s = "{}";
        try {
            ObjectMapper om = new ObjectMapper();
            om.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            s = om.writeValueAsString(this);
        } catch (Exception e) {

        }
        return s;
    }
    
    @JsonProperty("@context")
    public String getContext() {
        return "http://www.w3.org/ns/odrl.jsonld";
    }

    @JsonProperty("@type")
    public String getType() {
        if (typeofpolicy == POLICY_SET)
            return "http://www.w3.org/ns/odrl/2/Set";
        return "http://www.w3.org/ns/odrl/2/Policy";
    }
    
    @JsonProperty("permission")
    public List<Rule> getPermissions() {
        List<Rule> res = new ArrayList();
        for(Rule r : rules)
        {
            if (r.getKindOfRuleString().equals("Permission"))
                res.add(r);
        }
        return res;
    }
    @JsonProperty("prohibition")
    public List<Rule> getProhibitions() {
        List<Rule> res = new ArrayList();
        for(Rule r : rules)
        {
            if (r.getKindOfRuleString().equals("Prohibition"))
                res.add(r);
        }
        return res;
    }
    @JsonProperty("duty")
    public List<Rule> getDuties() {
        List<Rule> res = new ArrayList();
        for(Rule r : rules)
        {
            if (r.getKindOfRuleString().equals("Duty"))
                res.add(r);
        }
        return res;
    }

}
