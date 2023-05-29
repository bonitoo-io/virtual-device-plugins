package io.bonitoo.virdev.plugin;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.bonitoo.qa.conf.data.SampleConfigDeserializer;
import io.bonitoo.qa.plugin.sample.SamplePluginConfig;

import java.io.IOException;

public class LPFileReaderPluginConfDeserializer extends SampleConfigDeserializer {

  public LPFileReaderPluginConfDeserializer(){
    this(null);
  }

  protected LPFileReaderPluginConfDeserializer(Class<?> vc){
    super(vc);
  }

  @Override
  public LPFileReaderPluginConf deserialize(JsonParser jsonParser,
                                            DeserializationContext ctx)
    throws IOException {

    TreeNode tn = jsonParser.readValueAsTree();

    SamplePluginConfig conf = jsonParser.getCodec().treeToValue(tn, SamplePluginConfig.class);

    JsonNode sourceNode = (JsonNode)tn.get("source");

    String source = sourceNode != null ? sourceNode.asText() : null;

    return new LPFileReaderPluginConf(conf, source);

  }

}
