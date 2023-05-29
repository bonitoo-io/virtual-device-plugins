package io.bonitoo.virdev.plugin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bonitoo.qa.conf.data.ItemConfig;
import io.bonitoo.qa.plugin.sample.SamplePluginConfig;
import lombok.Getter;

import java.util.List;

@Getter
@JsonDeserialize(using = LPFileReaderPluginConfDeserializer.class)
public class LPFileReaderPluginConf extends SamplePluginConfig {

  String source; // source file for reading lp data

  public LPFileReaderPluginConf(String id,
                                String name,
                                String topic,
                                List<ItemConfig> items,
                                String plugin,
                                String source) {
    super(id, name, topic, items, plugin);
    this.source = source;
  }

  public LPFileReaderPluginConf(SamplePluginConfig conf, String source){
    super(conf);
    this.source = source;
  }

  @Override
  public String toString(){
    return String.format("%s,source:%s", super.toString(), this.source);
  }
}
