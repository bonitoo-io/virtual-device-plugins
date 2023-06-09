package io.bonitoo.virdev.plugin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bonitoo.qa.VirtualDeviceRuntimeException;
import io.bonitoo.qa.conf.data.ItemPluginConfig;
import io.bonitoo.qa.plugin.PluginProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonDeserialize(using = SimpleMovingAvgConfDeserializer.class)
public class SimpleMovingAvgConf extends ItemPluginConfig {

  static int MAX_QUEUE_LENGTH = 10;

  int window;

  double max;
  double min;

  private static void saneWindow(int window){
    if(window < 1){
      throw new RuntimeException(String.format("window (%d) must be greater than 0", window));
    }

    if(window > MAX_QUEUE_LENGTH){
      throw new RuntimeException(String.format("window (%d) must be less than %d", window, MAX_QUEUE_LENGTH));
    }
  }

  private static void saneLimits(double min, double max){
    if(min > max){
      throw new VirtualDeviceRuntimeException(String.format("min (%f) must be less than or equal to max (%f).",
        min, max));
    }
  }

  public SimpleMovingAvgConf(PluginProperties props, String name, int window, double max, double min) {
    super(props, name);
    saneWindow(window);
    saneLimits(min, max);
    this.window = window;
    this.max = max;
    this.min = min;
  }

  public SimpleMovingAvgConf(ItemPluginConfig config) {
    super(config);
    // when this gets called from the deserializer
    // it will pass a pure ItemPluginConfig instance.
    // However, other copies get made when creating new item instances
    if(config instanceof SimpleMovingAvgConf) {
      saneWindow(((SimpleMovingAvgConf) config).getWindow());
      saneLimits(((SimpleMovingAvgConf) config).getMin(), ((SimpleMovingAvgConf) config).getMax());
      this.window = ((SimpleMovingAvgConf) config).getWindow();
      this.max = ((SimpleMovingAvgConf) config).getMax();
      this.min = ((SimpleMovingAvgConf) config).getMin();
    }
  }

  @Override
  public String toString(){
    return String.format("%s, window:%d, min:%f, max:%f",
      super.toString(),
      this.window,
      this.min,
      this.max);
  }

  @Override
  public boolean equals(Object obj){
    if( ! (obj instanceof SimpleMovingAvgConf)){
      return false;
    }

    if (! super.equals(obj) ) {
      return false;
    };

    SimpleMovingAvgConf conf = (SimpleMovingAvgConf) obj;

    return this.window == conf.window
      && this.min == conf.min
      && this.max == conf.max;
  }

}
