package de.micromata.merlin.paypal.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalSerializer extends StdScalarSerializer<BigDecimal> {

    protected BigDecimalSerializer() {
        super(BigDecimal.class);
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        if (value.scale() == 2) {
            jgen.writeString(value.toString());
        }
        jgen.writeString(value.setScale(2, RoundingMode.HALF_UP).toString());
    }
}
