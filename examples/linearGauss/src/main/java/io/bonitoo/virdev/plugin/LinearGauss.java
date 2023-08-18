package io.bonitoo.virdev.plugin;

import io.bonitoo.qa.data.generator.NumGenerator;
import io.bonitoo.qa.plugin.PluginProperties;
import io.bonitoo.qa.plugin.item.ItemGenPlugin;
import io.bonitoo.qa.plugin.item.ItemPluginConfigClass;

@ItemPluginConfigClass(conf = LinearGaussConf.class)
public class LinearGauss extends ItemGenPlugin {


  @Override
  public Double getCurrentVal() {
    return (Double)item.getVal();
  }

  @Override
  public void onLoad() {
    this.enabled = this.onEnable();
    item.setVal(this.genData());
  }

  @Override
  public void applyProps(PluginProperties pluginProperties) {
     // holder - no props needed for now
  }

  @Override
  public Double genData() {
    item.setVal(NumGenerator.gaussNormalFilter(getMin(), getMax()));
    return (Double)item.getVal();
  }

  private double getMin(){
    return ((LinearGaussConf)this.getItemConfig()).min;
  }

  private double getMax(){
    return ((LinearGaussConf)this.getItemConfig()).max;
  }
}
