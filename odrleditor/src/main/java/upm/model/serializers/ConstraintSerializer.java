package upm.model.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import upm.model.Constraint;

import java.io.IOException;

public class ConstraintSerializer extends StdSerializer<Constraint> {


    public ConstraintSerializer() {
        this(null);
    }

    public ConstraintSerializer(Class<Constraint> t) {
        super(t);
    }

    @Override
    public void serialize(
            Constraint value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        if(value.getUid()!=null) {
            jgen.writeStringField("@context","http://www.w3.org/ns/odrl.jsonld");
            jgen.writeStringField("@type","Constraint");
            jgen.writeStringField("uid", value.getUid());
        }


        if(value.getOperands()==null) {
            jgen.writeStringField( value.getIsReference()==null?"rightOperand":"rightOperandReference", value.getRightOperand());
            jgen.writeStringField("operator", value.getOperator());
            jgen.writeStringField("leftOperand", value.getLeftOperand());
        }
        else{
            jgen.writeArrayFieldStart(value.getOperator());

            for(String s : value.getOperands()){
                jgen.writeStartObject();

                    jgen.writeStringField("@id", s.toString());
                jgen.writeEndObject();
            }

            jgen.writeEndArray();
        }

        if(value.getUnit()!=null) {
            jgen.writeStringField("unit", value.getUnit());
        }
        if(value.getDataType()!=null) {
            jgen.writeStringField("dataType", value.getDataType());
        }
        if(value.getStatus()!=null) {
            jgen.writeStringField("status", value.getStatus());
        }
        jgen.writeEndObject();
    }
}