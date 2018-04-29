import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class InvertedIndex {

  public static class IndexMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {

    private final static Text word = new Text();
    private final static Text docID = new Text();

    public void map(LongWritable key, Text val,
        OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
     
      //Get the input with docID and passage and convert to String type for tokenizing purpose
      String line = val.toString();
      StringTokenizer itr = new StringTokenizer(line.toLowerCase(),"\t");
     
      //Set the docID  
      if(itr.hasMoreTokens()){
     	 docID.set(itr.nextToken());
      }
	
      //Get the passage
      String passage = itr.nextToken();  
      
      //Tokenize the passage on space
      StringTokenizer iter = new StringTokenizer(passage);

      while(iter.hasMoreTokens()){
          word.set(iter.nextToken());
          output.collect(word,docID);
      } 
      
    }
  }



  public static class IndexReducer extends MapReduceBase
      implements Reducer<Text, Text, Text, Text> {

    public void reduce(Text key, Iterator<Text> values,
        OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {
      
      HashMap<String,Integer> map = new HashMap<String,Integer>();
     
      while(values.hasNext()){
           String data = values.next().toString();
           if(map.containsKey(data)){
	       int count = map.get(data);
               map.put(data,count+1);
           }
           else{
               map.put(data,1);
           }    
      }

      StringBuilder toReturn = new StringBuilder();

      for(String x : map.keySet()){
          toReturn.append(x);
          toReturn.append(": ");
          toReturn.append(map.get(x).toString());
          toReturn.append(" ");
        }
        
      output.collect(key, new Text(toReturn.toString()));
    }
  }


  /**
   * The actual main() method for our program; this is the
   * "driver" for the MapReduce job.
   */
  public static void main(String[] args) {
    JobClient client = new JobClient();
    JobConf conf = new JobConf(InvertedIndex.class);

    conf.setJobName("Inverted Index");

    conf.setOutputKeyClass(Text.class);
    conf.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(conf, new Path(args[0]));
    FileOutputFormat.setOutputPath(conf, new Path(args[1]));

    conf.setMapperClass(IndexMapper.class);
    conf.setReducerClass(IndexReducer.class);

    client.setConf(conf);

    try {
      JobClient.runJob(conf);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
