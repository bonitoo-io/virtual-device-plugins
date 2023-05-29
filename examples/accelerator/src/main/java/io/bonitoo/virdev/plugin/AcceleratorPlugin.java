package io.bonitoo.virdev.plugin;

import io.bonitoo.qa.conf.data.ItemConfig;
import io.bonitoo.qa.plugin.item.ItemGenPlugin;
import io.bonitoo.qa.plugin.PluginProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class AcceleratorPlugin extends ItemGenPlugin {

  static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static double INITIAL_SPEED = 1.0;
  private static double INITIAL_ACCEL = 1.0;
  private static double SPEED_LIMIT = 60.0;

  double speed; // m/s
  double accel; // m/s**

  long lastRecordStamp;

  public AcceleratorPlugin(){
    this.speed = INITIAL_SPEED;
    this.accel = INITIAL_ACCEL;
    this.lastRecordStamp = System.currentTimeMillis();
  }

  @Override
  public Double getCurrentVal() {
    return speed;
  }

  public AcceleratorPlugin(PluginProperties props, ItemConfig config, boolean enabled, double speed, double accel) {
    super(props, config, enabled);
    this.speed = speed;
    this.accel = accel;
    this.lastRecordStamp = System.currentTimeMillis();
  }

  @Override
  public void onLoad() {

    this.speed = props.getProperties().getProperty("initial.speed") == null ?
      INITIAL_SPEED : Double.parseDouble(props.getProperties().getProperty("initial.speed"));
    this.accel = props.getProperties().getProperty("initial.accel") == null ?
      INITIAL_ACCEL : Double.parseDouble(props.getProperties().getProperty("initial.accel"));

    System.out.println("DEBUG initial.speed " + this.speed);
    System.out.println("DEBUG initial.accel " + this.accel);
    this.enabled = true;
  }

  @Override
  public void applyProps(PluginProperties pluginProperties) {
      // holder
  }

  @Override
  public Object genData(Object... objects) {
    accel = changeAccel(accel, speed);
    long currTimeStamp = System.currentTimeMillis();
    double timeFactor = currTimeStamp - lastRecordStamp;
    lastRecordStamp = currTimeStamp;
    speed += (accel * (timeFactor/1000));
    logger.info(String.format("speed: %.5f", speed));
    return speed;
  }

  private double changeAccel(double curAccel, double curSpeed){
    double delta = Math.random() * 2;
    if(curSpeed < 0){
      if(curSpeed < SPEED_LIMIT /-3){
        return curAccel + delta;
      }else{
        return curAccel - delta;
      }
    }else{
      if(curSpeed > SPEED_LIMIT /3){
        return curAccel - delta;
      }else{
        return curAccel + delta;
      }
    }
  }
}
