package io.bonitoo.virdev.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.bonitoo.qa.plugin.PluginConfigException;
import io.bonitoo.qa.plugin.PluginProperties;
import io.bonitoo.qa.plugin.sample.SamplePluginConfig;
import io.bonitoo.virdev.plugin.lp.LineProtocol;
import io.bonitoo.virdev.plugin.lp.LineProtocolException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.*;

public class LPFileReaderPluginTest {

  static PluginProperties defaultProps;

  @BeforeAll
  public static void readProps() throws IOException, PluginConfigException {
    Properties props = new Properties();
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    InputStream stream = loader.getResourceAsStream("plugin.props");
    props.load(stream);

    defaultProps = new PluginProperties(props);

  }

  @Test
  public void createLPFileReaderPlugin() throws PluginConfigException, IOException {

    LPFileReaderPluginConf conf = new LPFileReaderPluginConf("random",
      defaultProps.getName() + "Conf",
      "test/linep",
      new ArrayList<>(),
      defaultProps.getName(),
      null);

    LPFileReaderPlugin lpfrp = new LPFileReaderPlugin(defaultProps, conf);

    lpfrp.onLoad();

    assertEquals(24, lpfrp.getLines().size());
    assertEquals(0, lpfrp.getIndex());

    LineProtocol lastLp = lpfrp.getCurrent();
    for(int i = 0; i < 30; i++) {
      assertTrue(lpfrp.update().getCurrent().getTimestamp() > lastLp.getTimestamp());
      assertNotEquals(lastLp, lpfrp.getCurrent());
      lastLp = lpfrp.getCurrent();
      //System.out.println("DEBUG lpfrp " + ((LPFileReaderPlugin) lpfrp.update()).writeLP());
      LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
    }

  }

  @Test
  public void checkLPString(){

    String test1 = "weather,sensor=Vir2023,location=PVL04NW temperature=27.25,pressure=1013,humidity=64.2 1664979438570";
    String test2 = "foo";
    String test3 = "The quick brown fox jumped over the lazy dog's back.";

    assertTrue(LineProtocol.isLP(test1));
    assertFalse(LineProtocol.isLP(test2));
    assertFalse(LineProtocol.isLP(test3));
  }

  @Test
  public void parseLPString(){
    String test1 = "weather,sensor=Vir2023,location=PVL04NW temperature=27.25,pressure=1013,humidity=64.2 1664979438570";
    LineProtocol lp1 = LineProtocol.parseLP(test1);
    assertNotNull(lp1);
    assertEquals("weather", lp1.getMeasurement());
    assertEquals("Vir2023", lp1.getTags().get("sensor"));
    assertEquals("PVL04NW", lp1.getTags().get("location"));
    assertEquals(27.25, lp1.getFields().get("temperature"));
    assertEquals(64.2, lp1.getFields().get("humidity"));
    assertEquals(1013L, lp1.getFields().get("pressure"));

    String test2 = "// This is a comment";
    LineProtocol lp2 = LineProtocol.parseLP(test2);
    assertNull(lp2);
    String test3 = "    \t\n"; // whitespace only
    LineProtocol lp3 = LineProtocol.parseLP(test3);
    assertNull(lp3);
    String test4 = "Something that is not lp - 1234.";
    assertThrows(LineProtocolException.class, () -> LineProtocol.parseLP(test4));

  }

  @Test
  public void configSerialization() throws JsonProcessingException {

    SamplePluginConfig conf = new SamplePluginConfig("random",
      defaultProps.getName() + "Conf",
      "test/linep",
      new ArrayList<>(),
      defaultProps.getName());

    LPFileReaderPluginConf lpConf = new LPFileReaderPluginConf(conf, "myTestLP.lp");

    ObjectMapper om = new ObjectMapper(new YAMLFactory());
    ObjectWriter ow = om.writer();
    // serialize
    String lpConfYaml = ow.writeValueAsString(lpConf);

//     System.out.println("DEBUG lpConf\n" + lpConfYaml);

    // deserialize
    LPFileReaderPluginConf copyConf = om.readValue(lpConfYaml, LPFileReaderPluginConf.class);

    assertEquals(lpConf, copyConf);

  }

  @Test
  public void serializeTest() throws JsonProcessingException {

    LPFileReaderPluginConf conf = new LPFileReaderPluginConf("random",
      defaultProps.getName() + "Conf",
      "test/linep",
      new ArrayList<>(),
      defaultProps.getName(),
      null);

    LPFileReaderPlugin lpfrp = new LPFileReaderPlugin(defaultProps, conf);

    lpfrp.onLoad();

  //  System.out.println("DEBUG lpfrp\n" + lpfrp.toJson());

    String lpJson = lpfrp.toJson();

    ObjectMapper om = new ObjectMapper();

    LineProtocol lp = om.readValue(lpJson, LineProtocol.class);

    assertEquals("weather", lp.getMeasurement());
    assertEquals("Vir2023", lp.getTags().get("sensor"));
    assertEquals("PVL04NW", lp.getTags().get("location"));
    assertEquals(27.15, lp.getFields().get("temperature"));
    assertEquals(60.2, lp.getFields().get("humidity"));
    assertEquals(1013, lp.getFields().get("pressure"));

    lpfrp.update().update();

    String lp2Json = lpfrp.toJson();
    LineProtocol lp2 = om.readValue(lp2Json, LineProtocol.class);


    assertEquals("weather", lp2.getMeasurement());
    assertEquals("WTest9", lp2.getTags().get("sensor"));
    assertEquals("PVL02EE", lp2.getTags().get("location"));
    assertEquals(28.02, lp2.getFields().get("temperature"));
    assertEquals(60.2, lp2.getFields().get("humidity"));
    assertEquals(1012, lp2.getFields().get("pressure"));

  //  System.out.println("DEBUG lpfrp\n" + lpfrp.toJson());

  }

  @Test
  public void readFileFromConfigAsResource() throws JsonProcessingException {

    LPFileReaderPluginConf conf = new LPFileReaderPluginConf("random",
      defaultProps.getName() + "Conf",
      "test/linep",
      new ArrayList<>(),
      defaultProps.getName(),
      "myTestLP.lp");

    LPFileReaderPlugin plugin = new LPFileReaderPlugin(defaultProps, conf);

  //  System.out.println("DEBUG plugin.getLpFile() " + plugin.getLpFile());
    assertEquals("myTestLP.lp", plugin.getLpFile());

    plugin.onLoad();


    assertEquals(32, plugin.getLines().size());

//    for(LineProtocol lp: plugin.getLines()){
//      System.out.println("DEBUG lp " + lp);
//    }

 //   System.out.println("DEBUG plugin.index "  + plugin.getIndex());
    assertEquals(0, plugin.getIndex());

    plugin.update();

//    System.out.println("DEBUG plugin serialize " + plugin.toJson());

    ObjectMapper om = new ObjectMapper();

    LineProtocol lpCheck = om.readValue(plugin.toJson(), LineProtocol.class);

    assertEquals(plugin.getLines().get(1), lpCheck);

  }

  @Test
  public void readFileFromConfigFileSystem() throws JsonProcessingException {
    LPFileReaderPluginConf conf = new LPFileReaderPluginConf("random",
      defaultProps.getName() + "Conf",
      "test/linep",
      new ArrayList<>(),
      defaultProps.getName(),
      "./data/myData.lp");

    LPFileReaderPlugin plugin = new LPFileReaderPlugin(defaultProps, conf);

    assertEquals(conf.getSource(), plugin.getLpFile());

    plugin.onLoad();

    assertEquals(12, plugin.getLines().size());
    assertEquals(0, plugin.getIndex());

    plugin.update().update();

 //   System.out.println("DEBUG plugin.toJson " + plugin.toJson());

    ObjectMapper om = new ObjectMapper();

    LineProtocol lp = om.readValue(plugin.toJson(), LineProtocol.class);

    assertEquals(plugin.getLines().get(2), lp);

  //  for(LineProtocol lpp : plugin.getLines()){
  //    System.out.println("DEBUG lp " + lpp);
  //  }

  }

  @Test
  public void sourceFileNotFound(){

    LPFileReaderPluginConf conf = new LPFileReaderPluginConf("random",
      defaultProps.getName() + "Conf",
      "test/linep",
      new ArrayList<>(),
      defaultProps.getName(),
      "./data/notAFile.lp");

    LPFileReaderPlugin plugin = new LPFileReaderPlugin(defaultProps, conf);

    assertEquals(conf.getSource(), plugin.getLpFile());

    assertThrows(LPFileReaderPluginException.class, () -> plugin.onLoad());

  }

}
