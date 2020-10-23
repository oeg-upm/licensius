package oeg.jodrlapi.odrlmodel;

//JAVA
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import oeg.jodrlapi.helpers.MetadataObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import oeg.jodrlapi.JODRLApiSettings;

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
    //If it is to be stored somewhere
    @JsonIgnore
    public String fileName = "";

    //A policy is made of one or more rules
    public List<Rule> rules = new ArrayList();

    //
    public List<String> profile = new ArrayList();
    
    //
    public List<String> target = new ArrayList(); 

    //
    public List<String> inheritFrom = new ArrayList();
    
    public String conflict = ""; //invalid, perm, prohibit
    
    //Kind of policy it is
    @JsonProperty("@type")
    private String typeofpolicy = "http://www.w3.org/ns/odrl/2/Set";

    //Agreement, Assertion, Offer, Privacy, Request, Set, Ticket
    public static final String POLICY_SET = "http://www.w3.org/ns/odrl/2/Set";             //DEFAULT
    public static final String POLICY_REQUEST = "http://www.w3.org/ns/odrl/2/Request";         
    public static final String POLICY_OFFER = "http://www.w3.org/ns/odrl/2/Offer";
    public static final String POLICY_CC = "http://www.w3.org/ns/odrl/2/CC"; // DEPRECATED
    public static final String POLICY_AGREEMENT = "http://www.w3.org/ns/odrl/2/Agreement";
    public static final String POLICY_TICKET = "http://www.w3.org/ns/odrl/2/Ticket";
    public static final String POLICY_ASSERTION = "http://www.w3.org/ns/odrl/2/Assertion";
    public static final String POLICY_PRIVACY = "http://www.w3.org/ns/odrl/2/Privacy";

    /**
     * Policy constructor with a random URI in the default namespace A policy is
     * by default a Set policy
     */
    public Policy() {
        uri = JODRLApiSettings.ODRL_NS + "policy/" + UUID.randomUUID().toString();
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
    public String getTypeofpolicy() {
        return typeofpolicy;
    }

    /**
     * Sets the typeofpolicy of the policy
     *
     * @param _type, one of: Policy.POLICY_SET, Policy.POLICY_REQUEST,
     * Policy.POLICY_OFFER
     */
    public void setTypeofpolicy(String _type) {
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
