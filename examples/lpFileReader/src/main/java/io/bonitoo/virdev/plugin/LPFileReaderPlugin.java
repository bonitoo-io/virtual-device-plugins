package io.bonitoo.virdev.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.bonitoo.qa.plugin.*;
import io.bonitoo.qa.plugin.sample.SamplePlugin;
import io.bonitoo.qa.plugin.sample.SamplePluginConfigClass;
import io.bonitoo.qa.plugin.sample.SamplePluginMill;
import io.bonitoo.virdev.plugin.lp.LineProtocol;
import lombok.Getter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@SamplePluginConfigClass(conf = LPFileReaderPluginConf.class)
@JsonSerialize(using = LPFileReaderPluginSerializer.class)
public class LPFileReaderPlugin extends SamplePlugin {

  String lpFile;

  List<LineProtocol> lines;

  int index;
  public static LPFileReaderPlugin create(LPFileReaderPluginConf conf){

    // should have been loaded into SamplePluginMill by PluginLoader
    return new LPFileReaderPlugin(SamplePluginMill.getPluginProps(conf.getPlugin()), conf);

  }

  public LPFileReaderPlugin(PluginProperties props, LPFileReaderPluginConf conf) {
    super(props, conf);
    lines = new ArrayList<>();
    lpFile = conf.getSource() != null ? conf.getSource() : (String) props.getProperties().get("default.lp.file");
    index = 0;
  }

  @Override
  public LPFileReaderPlugin update(){
    index++;
    if(index >= lines.size()){
      index = 0;
    }
    lines.get(index).setTimestamp(System.currentTimeMillis());
    return this;
  }

  @Override
  public String toJson() throws JsonProcessingException {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    return ow.writeValueAsString(this);
  }

  protected File resolveSourceFile() throws URISyntaxException {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL fileUrl = loader.getResource(lpFile);
    File result;
    if( fileUrl != null){ // located as resource
      return new File(Objects.requireNonNull(loader.getResource(lpFile)).toURI());
    }

    return new File(lpFile);
  }

  @Override
  public void onLoad(){
    super.onLoad();

    try {
      File inputFile = resolveSourceFile();
      try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
        String line;
        while((line = br.readLine()) != null){
          LineProtocol lp = LineProtocol.parseLP(line);
          if(lp != null) {
            lines.add(LineProtocol.parseLP(line));
          }
        }
      } catch (IOException e) {
        throw new LPFileReaderPluginException(e);
      }
    } catch (URISyntaxException e) {
      throw new LPFileReaderPluginException(e);
    }

  }

  @Override
  public void applyProps(PluginProperties pluginProperties) {
    // holder
  }

  public LineProtocol get(int ndx){
    return lines.get(ndx);
  }

  public LineProtocol getCurrent(){
    return get(this.index);
  }

}
