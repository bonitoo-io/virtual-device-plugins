package io.bonitoo.virdev.plugin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class OrientationSampleSerializer extends StdSerializer {
  protected OrientationSampleSerializer(Class t) {
    super(t);
  }

  @Override
  public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

  }
}
