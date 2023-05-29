package io.bonitoo.virdev.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.bonitoo.qa.conf.data.SampleConfig;
import io.bonitoo.qa.plugin.PluginProperties;
import io.bonitoo.qa.plugin.SamplePlugin;
import io.bonitoo.qa.plugin.SamplePluginConfig;


public class OrientationSamplePlugin extends SamplePlugin {


  public OrientationSamplePlugin(PluginProperties props, SampleConfig config) {
    super(props, config);
  }

  public static OrientationSamplePlugin create(SamplePluginConfig config){
    return null;
  }

  @Override
  public OrientationSamplePlugin update(){
    return null;
  }

  @Override
  public String toJson() throws JsonProcessingException {
    return null;
  }

  @Override
  public void applyProps(PluginProperties pluginProperties) {
     // holder
  }
}
