package io.bonitoo.virdev.plugin;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.bonitoo.qa.conf.data.ItemConfigDeserializer;
import io.bonitoo.qa.conf.data.ItemPluginConfig;

import java.io.IOException;

public class SimpleMovingAvgConfDeserializer extends ItemConfigDeserializer {


  @Override
  public SimpleMovingAvgConf deserialize(JsonParser jsonParser,
                                             DeserializationContext ctx)
    throws IOException {

    System.out.println("DEBUG using SimpleMovingAvgConfDeserializer");

    TreeNode tn = jsonParser.readValueAsTree();

    // At this point use the deserializer for the base class
    ItemPluginConfig itemPluginConfig = jsonParser
      .getCodec()
      .treeToValue(tn, ItemPluginConfig.class);

    //Note that itemPluginConfig is a pure base class instance
    SimpleMovingAvgConf conf = new SimpleMovingAvgConf(itemPluginConfig);

    TreeNode windowNode = tn.get("window");
    if (windowNode != null) {
      conf.window = ((JsonNode)windowNode).asInt();
    }

    TreeNode minNode = tn.get("min");
    if(minNode != null){
      conf.min = ((JsonNode)minNode).asDouble();
    }

    TreeNode maxNode = tn.get("max");
    if(maxNode != null){
      conf.max = ((JsonNode)maxNode).asDouble();
    }

    return conf;
  }

}
