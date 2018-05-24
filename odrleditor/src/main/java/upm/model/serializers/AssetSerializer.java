package upm.model.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import upm.model.Asset;

import java.io.IOException;

public class AssetSerializer extends StdSerializer<Asset> {


    public AssetSerializer() {
        this(null);
    }

    public AssetSerializer(Class<Asset> t) {
        super(t);
    }

    @Override
    public void serialize(
            Asset value, JsonGenerator jgen, SerializerProvider provider)
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