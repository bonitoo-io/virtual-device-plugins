package io.bonitoo.virdev.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.bonitoo.qa.conf.VirDevConfigException;
import io.bonitoo.qa.plugin.PluginConfigException;
import io.bonitoo.qa.plugin.PluginProperties;
import io.bonitoo.qa.plugin.PluginResultType;
import io.bonitoo.qa.plugin.PluginType;
import io.bonitoo.qa.plugin.item.ItemPluginMill;
import org.junit.jupiter.api.Test;
import io.bonitoo.qa.data.generator.NumGenerator;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class LinearGaussGenTest {

  static PluginProperties props = new PluginProperties(LinearGauss.class.getName(),
    "LinearGaussGen",
    "propLabel", "Leverages the gauss generator function of NumGenerator",
    "0.1", PluginType.Item, PluginResultType.Double, new Properties());

  @Test
  public void baseTest(){
    for(int i = 0; i < 100; i++) {
      System.out.printf("DEBUG %.3f\n", NumGenerator.gaussNormalFilter(-10, 10));
    }
  }

  @Test
  public void createConf() throws JsonProcessingException {
    LinearGaussConf lgConf = new LinearGaussConf(props, "LinearGaussConfig", -5.0, 15.0);

    System.out.println("DEBUG lgConf as string: " + lgConf);

    ObjectMapper omy = new ObjectMapper(new YAMLFactory());
    ObjectWriter ow = omy.writer().withDefaultPrettyPrinter();

    System.out.println("DEBUG lgConf\n" + ow.writeValueAsString(lgConf));

    ItemPluginMill.addPluginClass(props.getName(), LinearGauss.class, props);

    String confYaml = ow.writeValueAsString(lgConf);
    LinearGaussConf parsedConf = omy.readValue(confYaml, LinearGaussConf.class);

    assertEquals(lgConf, parsedConf);

  }

  @Test
  public void createFromString() throws JsonProcessingException {

    ItemPluginMill.addPluginClass(props.getName(), LinearGauss.class, props);

    String yamlConfString = "---\n" +
      "name: \"LinearGaussConf\"\n" +
      "label: \"gauss\"\n" +
      "type: \"Plugin\"\n" +
      "genClassName: \"io.bonitoo.virdev.plugin.LinearGauss\"\n" +
      "pluginName: \"LinearGaussGen\"\n" +
      "resultType: \"Double\"\n" +
      "min: -6.0\n" +
      "max: 55.0";

    ObjectMapper omy = new ObjectMapper(new YAMLFactory());

    LinearGaussConf lgConf = omy.readValue(yamlConfString, LinearGaussConf.class);

    System.out.println("DEBUG lgConf " + lgConf);
    assertEquals(-6.0, lgConf.getMin());
    assertEquals(55.0, lgConf.getMax());

    System.out.println("DEBUG lgConf.getLabel() " + lgConf.getLabel());
    assertEquals("gauss", lgConf.getLabel());

  }

  @Test
  public void maxLessThanMin() throws JsonProcessingException {

    ItemPluginMill.addPluginClass(props.getName(), LinearGauss.class, props);

    String yamlConfString = "---\n" +
      "name: \"LinearGaussConf\"\n" +
      "label: \"gauss\"\n" +
      "type: \"Plugin\"\n" +
      "genClassName: \"io.bonitoo.virdev.plugin.LinearGauss\"\n" +
      "pluginName: \"LinearGaussGen\"\n" +
      "resultType: \"Double\"\n" +
      "min: 55.0\n" +
      "max: -6.0";

    ObjectMapper omy = new ObjectMapper(new YAMLFactory());

    Exception e = assertThrows(VirDevConfigException.class, () -> {
      omy.readValue(yamlConfString, LinearGaussConf.class);
    });

    assertEquals("In config, encountered max -6.000 less than min 55.000\n", e.getMessage());

  }

  @Test
  public void createPlugin() throws ClassNotFoundException, PluginConfigException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    LinearGaussConf lgConf = new LinearGaussConf(props, "LinearGaussConfig", -6.0, 55.0);

    System.out.println("DEBUG lgConf " + lgConf);

    ItemPluginMill.addPluginClass(props.getName(), props);

    LinearGauss lg = (LinearGauss) ItemPluginMill.genNewInstance(props.getName(), lgConf);

    System.out.println("DEBUG lg conf " + (LinearGaussConf)lg.getItemConfig());

    double val = lg.getCurrentVal();

    System.out.println("DEBUG val " + val);

    for(int i = 0; i < 100; i++){
      lg.genData();
      System.out.println("DEBUG " + lg.getCurrentVal());
      assertTrue(lg.getCurrentVal() >= -6.0 && lg.getCurrentVal() <= 55.0);
    }

  }

}
