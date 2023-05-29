package io.bonitoo.virdev.plugin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.bonitoo.virdev.plugin.lp.LineProtocol;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class LPFileReaderPluginSerializer extends StdSerializer<LPFileReaderPlugin> {

  static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset

  static {
    df.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  public LPFileReaderPluginSerializer() {
    this(null);
  }
  protected LPFileReaderPluginSerializer(Class<LPFileReaderPlugin> t) {
    super(t);
  }

  @Override
  public void serialize(LPFileReaderPlugin lpFileReaderPlugin,
                        JsonGenerator jsonGen,
                        SerializerProvider serProvider) throws IOException {

    LineProtocol lp = lpFileReaderPlugin.getCurrent();

    jsonGen.writeStartObject();
    jsonGen.writeStringField("measurement", lp.getMeasurement());
    jsonGen.writeFieldName("tags");
    jsonGen.writeStartObject();
    for (String key : lp.getTags().keySet()){
      jsonGen.writeStringField(key, lp.getTags().get(key));
    }
    jsonGen.writeEndObject();
    jsonGen.writeFieldName("fields");
    jsonGen.writeStartObject();
    for (String key : lp.getFields().keySet()){
      Object val = lp.getFields().get(key);
      if(val instanceof Long){
        jsonGen.writeNumberField(key, (Long)lp.getFields().get(key));
      }else if(val instanceof Double){
        jsonGen.writeNumberField(key, (Double)lp.getFields().get(key));
      }else{ // use String field
        jsonGen.writeStringField(key, lp.getFields().get(key).toString());
      }
    }
    jsonGen.writeEndObject();
    jsonGen.writeNumberField("timestamp", lp.getTimestamp());
    jsonGen.writeEndObject();
  }
}
