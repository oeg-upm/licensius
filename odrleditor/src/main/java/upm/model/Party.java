package upm.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import upm.model.serializers.PartySerializer;

import java.util.List;



@JsonSerialize(using = PartySerializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Party {
    private String type ="Party";
    private String uid;
    private List<Constraint> constraints;

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

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<Constraint> refinement) {
        this.constraints = refinement;
    }

    @Override
    public String toString() {
        return "Party{" +
                "type='" + type + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
