package upm.model.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import upm.model.Party;

import java.io.IOException;


public class PartySerializer extends StdSerializer<Party> {


    public PartySerializer() {
        this(null);
    }

    public PartySerializer(Class<Party> t) {
        super(t);
    }

    @Override
    public void serialize(
            Party value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeStringField( value.getConstraints()==null?"uid":"source", value.getUid());
        jgen.writeStringField("@type", value.getType());
        if(value.getConstraints()!=null) {
            jgen.writeObjectField("refinement", value.getConstraints());
        }
        jgen.writeEndObject();
    }
}
