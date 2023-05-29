## LPFileReaderPlugin

Reads Influx style line protocol data from a source file and converts it to JSON format.  Intended for use as a SamplePlugin with Bonitoo's Virtual Device, which will then send the sample data as a message body to an MQTT broker.  Note that when the last line protocol line from the source file is read, this plugin cycles back to the first line of the file and starts writing the sequence again.  

Configuration: 

```yaml
---
name: "LPFileReaderConf"
id: "random"
topic: "test/linep"
items: []
plugin: "LPFileReader"
source: "myTestLP.lp"
```
Note that the configuration follows standard sample configuration structures for Bonitoo Virtual Device, except for the `source` field, which specifies the Line Protocol source file.  This can be kept in project resources, or be a path to any file in the file system.  For this plugin default `items` are ignored. 
