package io.bonitoo.virdev.plugin.lp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LineProtocol {

  String measurement;
  Long timestamp;

  Map<String,String> tags;

  Map<String,Object> fields;

  static String lpPattern = "^\\w+,[\\w|=|,]+ [\\w|=|,]+.*$";

  public static LineProtocol parseLP(String line){

    LineProtocol lp = new LineProtocol();
    lp.tags = new HashMap<>();
    lp.fields = new HashMap<>();

    LinkedList<String> idList;
    LinkedList<String> fieldList;

    if(isLP(line)) {
      String[] chunks = line.split("\\s");

      String[] idChunks = chunks[0].split(",");
      idList = new LinkedList<>(Arrays.asList(idChunks));
      lp.measurement = idList.poll();
      while (idList.peek() != null) {
        String[] kvPair = Objects.requireNonNull(idList.poll()).split("=");
        lp.tags.put(kvPair[0],kvPair[1]);
      }

      String[] fieldChunks = chunks[1].split(",");
      fieldList = new LinkedList<>(Arrays.asList(fieldChunks));
      while(fieldList.peek() != null){
        String[] kvPair = Objects.requireNonNull(fieldList.poll()).split("=");
        if(kvPair[1].matches("^\\d*\\.\\d*$")) { // is double
          lp.fields.put(kvPair[0], Double.parseDouble(kvPair[1]));
        }else if(kvPair[1].matches("^\\d*$")){ // is long
          lp.fields.put(kvPair[0], Long.parseLong(kvPair[1]));
        }else{ // is unknown
          lp.fields.put(kvPair[0], kvPair[1]);
        }
      }

      // TODO from config set time precisions e.g. sec, milli, nano etc.
      // for now use millis
      lp.timestamp = System.currentTimeMillis();

      return lp;

    }

    // ignore comments and whitespaces - empty lines
    if(!(line.matches("^//.*") || line.matches("^\\s*$"))) {
      throw new LineProtocolException(
        String.format("Cannot parse line %s using line protocol format",
          line)
      );
    }

    return null; // only on whitespace and comments
  }

  public static boolean isLP(String candidate){

    return candidate.trim().matches(lpPattern);

  }


  @Override
  public String toString(){
    return String.format("measurement:%s,tags:%s,fields:%s,timestamp%s",
      this.measurement, this.tags, this.fields, this.timestamp);
  }

  @Override
  public boolean equals(Object other){
    if(!(other instanceof LineProtocol)){
      return false;
    }

    LineProtocol lp = (LineProtocol)other;

    // Double check tags
    for(String key: this.tags.keySet()){
      if(!lp.tags.containsKey(key) || !this.tags.get(key).equals(lp.tags.get(key))){
        return false;
      }
    }

    for(String key: lp.tags.keySet()){
      if(!this.tags.containsKey(key) || !lp.tags.get(key).equals(this.tags.get(key))){
        return false;
      }
    }

    // String compare because off-the-cuff jackson deserializer can convert Long fields to Integer
    // and only care about value comparison.
    for(String key: this.fields.keySet()){
      if(!lp.fields.containsKey(key) ||
        !this.fields.get(key).toString().equals(lp.fields.get(key).toString())){
        return false;
      }
    }

    // String compare because off-the-cuff jackson deserializer can convert Long fields to Integer
    // and only care about value comparison.
    for(String key: lp.fields.keySet()){
      if(!this.fields.containsKey(key) ||
        !lp.fields.get(key).toString().equals(this.fields.get(key).toString())){
        return false;
      }
    }

    return this.measurement.equals(lp.measurement) && this.timestamp.equals(lp.timestamp);

  }



}
