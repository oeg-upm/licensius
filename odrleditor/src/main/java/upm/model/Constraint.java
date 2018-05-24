package upm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import upm.model.serializers.ConstraintSerializer;

import java.util.List;

@JsonSerialize(using = ConstraintSerializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Constraint {

    private String uid;
    private String leftOperand;
    private String rightOperand;
    private String operator;
    private String unit;
    private String dataType;
    private String status;
    private List<String> operands;

    private String isReference;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLeftOperand() {
        return leftOperand;
    }

    public void setLeftOperand(String leftOperand) {
        this.leftOperand = leftOperand;
    }

    public String getRightOperand() {
        return rightOperand;
    }

    public void setRightOperand(String rightOperand) {
        this.rightOperand = rightOperand;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public String getIsReference() {
        return isReference;
    }

    public void setIsReference(String isReference) {
        this.isReference = isReference;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getOperands() {
        return operands;
    }

    public void setOperands(List<String> operands) {
        this.operands = operands;
    }

    @Override
    public String toString() {
        return "Constraint{" +
                "uid='" + uid + '\'' +
                ", leftOperand='" + leftOperand + '\'' +
                ", rightOperand='" + rightOperand + '\'' +
                ", operator='" + operator + '\'' +
                '}';
    }
}
