package io.bonitoo.virdev.plugin;

import io.bonitoo.qa.plugin.PluginProperties;
import io.bonitoo.qa.plugin.item.ItemGenPlugin;
import io.bonitoo.qa.plugin.item.ItemPluginConfigClass;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

@ItemPluginConfigClass(conf = SimpleMovingAvgConf.class)
public class SimpleMovingAvg extends ItemGenPlugin {

  ArrayBlockingQueue<Double> dataStream = new ArrayBlockingQueue<>(SimpleMovingAvgConf.MAX_QUEUE_LENGTH);

  // double val;

  public SimpleMovingAvg(PluginProperties props, boolean enabled) {
    super(props, enabled);
  }

  public SimpleMovingAvg() {
    super();
  }

  @Override
  public Double getCurrentVal() {
    return (Double) item.getVal();
  }

  @Override
  public void onLoad() {
    this.enabled = this.onEnable();
    System.out.println("DEBUG onLoad itemConfig " + getItemConfig());
    this.populateQueue().calcWindowAverage();
  }

  @Override
  public void applyProps(PluginProperties pluginProperties) {
     // holder
  }

  @Override
  public Object genData() {

    return calcWindowAverage().getCurrentVal();

  }


  public SimpleMovingAvg addDataToQueue(){
    dataStream.add((Math.random() * (this.getMax() - this.getMin())) + this.getMin());
    return this;
  }

  public SimpleMovingAvg populateQueue(){

    if(this.getWindow() > SimpleMovingAvgConf.MAX_QUEUE_LENGTH){
      throw new RuntimeException(
        String.format("Data window of size %d is larger than maximum queue length %d.",
          this.getWindow(),
          SimpleMovingAvgConf.MAX_QUEUE_LENGTH )
      );
    }

    for(int i = 0; i < SimpleMovingAvgConf.MAX_QUEUE_LENGTH; i++){
      addDataToQueue();
    }
    return this;
  }

  public SimpleMovingAvg calcWindowAverage(){

    Double sum = 0.0;

    for(int i = 0; i < this.getWindow(); i++){
      sum += dataStream.poll();
      addDataToQueue();
    }

    item.setVal(sum / this.getWindow());

    return this;
  }

  double getMax(){
     return ((SimpleMovingAvgConf)this.getItemConfig()).max;
  }

  double getMin(){
    return ((SimpleMovingAvgConf)this.getItemConfig()).min;
  }

  int getWindow(){
    return ((SimpleMovingAvgConf)this.getItemConfig()).window;
  }

  ArrayBlockingQueue<Double> getDataStream(){
    return dataStream;
  }

  public String dumpQueue(){
    return Arrays.toString(dataStream.toArray());
  }
}
