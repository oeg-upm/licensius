package upm.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class License {


    private String type="Agreement";
    private String uid;
    private String profile;
    private String context = "http://www.w3.org/ns/odrl.jsonld";
    private String inheritFrom;
    private String conflict;

    private List<Rule> rules;


    private List<Rule> permissions;


    private List<Rule> prohibitions;

    private List<Rule> duties;

    private List<Constraint> constraints;

    @JsonProperty("prohibition")
    public List<Rule> getProhibitions() {
        return prohibitions;
    }

    public void setProhibitions(List<Rule> prohibitions) {
        this.prohibitions = prohibitions;
    }

    @JsonProperty("permission")
    public List<Rule> getPermissions()
    {
        return permissions;
    }

    public void setPermissions(List<Rule> permissions) {
        this.permissions = permissions;
    }

    @JsonProperty("@type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @JsonProperty("@context")
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getInheritFrom() {
        return inheritFrom;
    }

    public void setInheritFrom(String inheritance) {
        this.inheritFrom = inheritance;
    }

    public String getConflict() {
        return conflict;
    }

    public void setConflict(String conflict) {
        this.conflict = conflict;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules=rules;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    @JsonProperty("obligation")
    public List<Rule> getDuties() {
        return duties;
    }

    public void setDuties(List<Rule> duties) {
        this.duties = duties;
    }

    public void processRules(){

        for (Rule rule:
                getRules()) {
            if(rule.getAssignees()==null && !getType().equals("Set")){
                setType("Offer");
            }
            if(rule.getAssigners()==null){
                setType("Set");
            }
        }


        List<Rule> permissions = new ArrayList<Rule>();
        List<Rule> duties = new ArrayList<Rule>();
        List<Rule> prohibitions = new ArrayList<Rule>();
        for (Rule rule:
                rules) {

            System.out.println(rule.getType());
            if(rule.getType().equals("Permission")){
                permissions.add(rule);
            }
            if(rule.getType().equals("Obligation")){
                duties.add(rule);
            }
            if(rule.getType().equals("Prohibition")){
                prohibitions.add(rule);
            }
            rule.processDuties();
        }

        if(permissions.size()>0){
            this.permissions=permissions;
        }

        if(duties.size()>0) {
            this.duties = duties;
        }

        if(prohibitions.size()>0) {
            this.prohibitions = prohibitions;
        }

        rules=null;


    }

    @Override
    public String toString() {
        return "License{" +
                "type='" + type + '\'' +
                ", uid='" + uid + '\'' +
                ", profile='" + profile + '\'' +
                ", context='" + context + '\'' +
                ", inheritFrom='" + inheritFrom + '\'' +
                ", conflict='" + conflict + '\'' +
                ", rules=" + rules +
                ", permissions=" + permissions +
                ", duties=" + duties +
                ", constraints=" + constraints +
                '}';
    }
}
