package upm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Rule {

    @JsonIgnore
    private String type;
    private List<Asset> targets;
    private List<Party> assigners;
    private List<Party> assignees;
    private List<Action> actions;
    private List<Rule> duties;
    private List<Rule> consequence;
    private List<Rule> duty;
    private List<Rule> remedy;
    private List<Constraint> constraints;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("target")
    public List<Asset> getTargets() {
        return targets;
    }

    public void setTargets(List<Asset> targets) {
        this.targets = targets;
    }

    @JsonProperty("assigner")
    public List<Party> getAssigners() {
        return assigners;
    }


    public void setAssigners(List<Party> assigners) {
        this.assigners = assigners;
    }

    @JsonProperty("assignee")
    public List<Party> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<Party> assignees) {
        this.assignees = assignees;
    }

    @JsonProperty("action")
    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public List<Rule> getDuties() {
        return duties;
    }

    public void setDuties(List<Rule> duties) {
        this.duties = duties;
    }

    public List<Rule> getConsequence() {
        return consequence;
    }

    public void setConsequence(List<Rule> consequence) {
        this.consequence = consequence;
    }

    public List<Rule> getDuty() {
        return duty;
    }

    public void setDuty(List<Rule> duty) {
        this.duty = duty;
    }

    public List<Rule> getRemedy() {
        return remedy;
    }

    public void setRemedy(List<Rule> remedy) {
        this.remedy = remedy;
    }

    @JsonProperty("constraint")
    public List<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public void processDuties(){
        List<Rule> permissions = new ArrayList<Rule>();
        List<Rule> duties = new ArrayList<Rule>();
        List<Rule> prohibitions = new ArrayList<Rule>();
        if(this.duties!=null) {
            for (Rule rule :
                    this.duties) {
                rule.processDuties();
                if(getType()==null){
                    duties.add(rule);
                }
                else if (getType().equals("Permission")) {
                    permissions.add(rule);
                }
                else if (getType().equals("Prohibition")) {
                    prohibitions.add(rule);
                }
                else {
                    duties.add(rule);
                }
            }
        }

        if(permissions.size()>0){
            this.duty=permissions;
        }

        if(duties.size()>0) {
            this.consequence = duties;
        }

        if(prohibitions.size()>0) {
            this.remedy = prohibitions;
        }

        this.duties=null;
    }



    @Override
    public String toString() {
        return "Rule{" +
                "type='" + type + '\'' +
                ", targets=" + targets +
                ", asigners=" + assigners +
                ", actions=" + actions +
                '}';
    }
}
