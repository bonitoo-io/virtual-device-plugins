package io.bonitoo.virdev.plugin;

import io.bonitoo.qa.conf.data.ItemPluginConfig;
import io.bonitoo.qa.plugin.PluginConfigException;
import io.bonitoo.qa.plugin.PluginProperties;
import io.bonitoo.qa.plugin.PluginResultType;
import io.bonitoo.qa.plugin.PluginType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class AcceleratorPluginTest {

  @Test
  public void speedTest() throws InterruptedException {

    PluginProperties props = new PluginProperties(AcceleratorPlugin.class.getName(),
      "Accelerator",
      "speed",
      "Generates speed data",
      "0.1",
      PluginType.Item,
      PluginResultType.Double,
      new Properties()
      );

    //ItemPluginConfig config = new ItemPluginConfig("Accelerator", "acceleratorTest", PluginResultType.Double);

    AcceleratorPlugin accel = new AcceleratorPlugin(props, null, true, 1.0, 1.0);

    accel.onLoad();

    for(int i = 0; i < 60; i++) {
      System.out.println("DEBUG accel.genData speed " + String.format("%.3f", accel.genData()));
      Thread.sleep(500);
    }

  }
}
