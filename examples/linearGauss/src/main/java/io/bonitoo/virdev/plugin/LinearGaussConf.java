package io.bonitoo.virdev.plugin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bonitoo.qa.conf.data.ItemPluginConfig;
import io.bonitoo.qa.plugin.PluginProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonDeserialize(using = LinearGaussConfDeserializer.class)
public class LinearGaussConf extends ItemPluginConfig {

  double max;
  double min;

  public LinearGaussConf(PluginProperties props, String name, double min, double max) {
    super(props, name);
    this.min = min;
    this.max = max;
  }

  /*
  N.B. this copy constructor is key, as it gets used in
  ItemPluginMill.genNewInstance().

  Without copying properties here, the resulting instance
  will have initial values of 0.0.
   */
  public LinearGaussConf(ItemPluginConfig config) {
    super(config);
    if(config instanceof LinearGaussConf){
      this.min = ((LinearGaussConf) config).getMin();
      this.max = ((LinearGaussConf) config).getMax();
    }
  }

  @Override
  public String toString(){
    return String.format("%s, min: %.3f, max: %.3f",
      super.toString(),
      this.min,
      this.max);
  }

  @Override
  public boolean equals(Object obj){
    if( ! (obj instanceof LinearGaussConf)){
      return false;
    }

    if (! super.equals(obj) ) {
      return false;
    };

    LinearGaussConf conf = (LinearGaussConf) obj;

    return this.min == conf.min
      && this.max == conf.max;
  }

}
