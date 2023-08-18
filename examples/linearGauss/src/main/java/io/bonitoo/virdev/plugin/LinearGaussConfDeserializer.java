package io.bonitoo.virdev.plugin;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.bonitoo.qa.conf.VirDevConfigException;
import io.bonitoo.qa.conf.data.ItemConfigDeserializer;
import io.bonitoo.qa.conf.data.ItemPluginConfig;

import java.io.IOException;

public class LinearGaussConfDeserializer extends ItemConfigDeserializer {

  @Override
  public LinearGaussConf deserialize(JsonParser jsonParser,
                                     DeserializationContext ctx)
    throws IOException {

//    System.out.println("DEBUG using LinearGaussConfDeserializer");

    TreeNode tn = jsonParser.readValueAsTree();

    // At this point use the deserializer for the base class
    ItemPluginConfig itemPluginConfig = jsonParser
      .getCodec()
      .treeToValue(tn, ItemPluginConfig.class);

    //Note that itemPluginConfig is a pure base class instance
    LinearGaussConf conf = new LinearGaussConf(itemPluginConfig);

    conf.min = safeGetNode((JsonNode)tn, "min").asDouble();
    conf.max = safeGetNode((JsonNode)tn, "max").asDouble();

    if(conf.max < conf.min){
      throw new VirDevConfigException(
        String.format("In config, encountered max %.3f less than min %.3f\n",
          conf.max,
          conf.min)
      );
    }

    return conf;

  }

}
