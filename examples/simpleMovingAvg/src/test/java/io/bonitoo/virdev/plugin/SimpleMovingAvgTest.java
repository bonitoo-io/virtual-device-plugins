package io.bonitoo.virdev.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.bonitoo.qa.data.Item;
import io.bonitoo.qa.plugin.PluginConfigException;
import io.bonitoo.qa.plugin.PluginProperties;
import io.bonitoo.qa.plugin.PluginResultType;
import io.bonitoo.qa.plugin.PluginType;
import io.bonitoo.qa.plugin.item.ItemPluginMill;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SimpleMovingAvgTest {

  static PluginProperties props = new PluginProperties(SimpleMovingAvg.class.getName(),
    "SimpleMovingAvg",
    "propLabel", "Calculates a simple moving average from a randomized queue",
    "0.0.1", PluginType.Item, PluginResultType.Double, new Properties());

  @Test
  public void createConf() throws JsonProcessingException {

    SimpleMovingAvgConf conf = new SimpleMovingAvgConf(props,"SimpleMovingAvgConf",10, 55.0, -6.0);

    System.out.println("DEBUG conf " + conf);

    ObjectMapper omy = new ObjectMapper(new YAMLFactory());
    ObjectWriter ow = omy.writer();

    System.out.println("DEBUG yaml conf\n" + ow.writeValueAsString(conf));

    ItemPluginMill.addPluginClass(props.getName(), SimpleMovingAvg.class, props);

    String confYaml = ow.writeValueAsString(conf);

    SimpleMovingAvgConf parsedConf = omy.readValue(confYaml, SimpleMovingAvgConf.class);

 //   System.out.println("DEBUG parsedConf " + parsedConf);

    assertEquals(conf, parsedConf);

  }

  @Test
  public void createConfFromString() throws JsonProcessingException {

    ItemPluginMill.addPluginClass(props.getName(), SimpleMovingAvg.class, props);

    String yamlConfString = "---\n" +
      "name: \"SimpleMovingAvgConf\"\n" +
      "label: \"blahBlahBlah\"\n" +
      "type: \"Plugin\"\n" +
      "genClassName: \"io.bonitoo.virdev.plugin.SimpleMovingAvg\"\n" +
      "pluginName: \"SimpleMovingAvg\"\n" +
      "resultType: \"Double\"\n" +
      "window: 10\n" +
      "max: 55.0\n" +
      "min: -6.0";

    ObjectMapper omy = new ObjectMapper(new YAMLFactory());

    SimpleMovingAvgConf conf = omy.readValue(yamlConfString, SimpleMovingAvgConf.class);

    assertEquals("blahBlahBlah", conf.getLabel());

  }

  @Test
  public void createPlugin() throws PluginConfigException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {

    SimpleMovingAvgConf conf = new SimpleMovingAvgConf(props,"SimpleMovingAvgConf", 10, 55.0, -6.0);

    ItemPluginMill.addPluginClass(props.getName(), props);

    SimpleMovingAvg sma = (SimpleMovingAvg) ItemPluginMill.genNewInstance(props.getName(), conf);

    System.out.println("DEBUG sma.getItemConfig() " + sma.getItemConfig());

    assertEquals(SimpleMovingAvgConf.MAX_QUEUE_LENGTH, sma.getDataStream().size());

    double startVal = sma.getCurrentVal();

    sma.genData();

    System.out.println("DEBUG sma.getCurrentVal " + sma.getCurrentVal());

    assertNotEquals(startVal, sma.getCurrentVal());


    for(int i = 0; i < 5; i++) {
      LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1000));
      sma.genData();
      System.out.println("DEBUG sma.getCurrentVal " + sma.getCurrentVal());
    }

  }

}
