package storm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Status;

import java.util.Map;

/**
 * Created by uday on 5/1/2017.
 */
public class ParseBolt extends BaseRichBolt {

    // To output tuples from this bolt to the count bolt
    OutputCollector collector;
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        // save the output collector for emitting tuples
        collector = outputCollector;

    }

    public void execute(Tuple tuple) {

        HashtagEntity tweetSum;
        GeoLocation tweetGeo;
        tweetSum = (HashtagEntity) tuple.getValueByField("hash");
        tweetGeo = (GeoLocation) tuple.getValueByField("geo");
        String hashtag = tweetSum.getText();
        Double longitude = tweetGeo.getLongitude();
        Double latitude = tweetGeo.getLatitude();
        collector.emit(new Values(hashtag,longitude,latitude));
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("hashtag","longitude","latitude"));
    }
}
