package oeg.jodrlapi.odrlmodel;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/**
 *
 * @author vroddon
 */
public class PolicySerializer extends StdSerializer<Policy> {

    public PolicySerializer() {
        this(null);
    }

    public PolicySerializer(Class<Policy> t) {
        super(t);
    }

    @Override
    public void serialize(Policy value, JsonGenerator jgen, SerializerProvider sp) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("@id", value.uri);
        jgen.writeEndObject();
    }
}
