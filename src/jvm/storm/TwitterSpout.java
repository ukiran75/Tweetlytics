package storm;

import org.apache.storm.Config;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by uday on 5/1/2017.
 */
public class TwitterSpout extends BaseRichSpout {

    String custkey ;
    String custsecret;
    String accesstoken ;
    String accesssecret ;

    // To output tuples from spout to the next stage bolt
    SpoutOutputCollector collector;

    /// Twitter4j - twitter stream to get tweets
    TwitterStream twitterStream;

    // Shared hashtagQueue for getting buffering tweets received
    LinkedBlockingQueue<HashtagEntity> hashtag = null;
    LinkedBlockingQueue<GeoLocation> geoLocations = null;

    private  class TweetListener implements StatusListener{

        public void onStatus(Status status) {
          HashtagEntity[] hashtags = status.getHashtagEntities();
          GeoLocation geo = status.getGeoLocation();
          if(geo == null)
          {
              Double longitude = status.getPlace().getBoundingBoxCoordinates()[0][0].getLongitude();
              Double latitude = status.getPlace().getBoundingBoxCoordinates()[0][0].getLatitude();
              geo =  new GeoLocation(latitude,longitude);
          }
          for (int i=0;i<3;i++)
          {
              if(hashtags[i] != null && geo != null)
              {
                hashtag.offer(hashtags[i]);
                geoLocations.offer(geo);
              }
          }

        }

        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

        }

        public void onTrackLimitationNotice(int i) {

        }

        public void onScrubGeo(long l, long l1) {

        }

        public void onStallWarning(StallWarning stallWarning) {

        }

        public void onException(Exception e) {

        }
    }

    /**
     * Constructor for tweet spout that accepts the credentials
     */
    public TwitterSpout(
            String                key,
            String                secret,
            String                token,
            String                tokensecret)
    {
        custkey = key;
        custsecret = secret;
        accesstoken = token;
        accesssecret = tokensecret;
    }

    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {

        // create the buffer to block tweets
        hashtag = new LinkedBlockingQueue<HashtagEntity>(1000);
        geoLocations = new LinkedBlockingQueue<GeoLocation>(1000);
        

        // save the output collector for emitting tuples
        collector = spoutOutputCollector;

        // build the config with credentials for twitter 4j
        ConfigurationBuilder config =
                new ConfigurationBuilder()
                        .setOAuthConsumerKey(custkey)
                        .setOAuthConsumerSecret(custsecret)
                        .setOAuthAccessToken(accesstoken)
                        .setOAuthAccessTokenSecret(accesssecret);

        // create the twitter stream factory with the config
        TwitterStreamFactory fact =
                new TwitterStreamFactory(config.build());

        // get an instance of twitter stream
        twitterStream = fact.getInstance();

        // provide the handler for twitter stream
        twitterStream.addListener(new TweetListener());

        // start the sampling of tweets
        twitterStream.sample();

    }

    public void nextTuple() {
        // try to pick a tweet from the buffer
        HashtagEntity hash = hashtag.poll();
        GeoLocation geo = geoLocations.poll();

        // if no tweet is available, wait for 50 ms and return
        if (hash==null && geo==null)
        {
            Utils.sleep(50);
            return;
        }

        // now emit the tweet to next stage bolt
        collector.emit(new Values(hash,geo));
    }
    @Override
    public void close()
    {
        // shutdown the stream - when we are going to exit
        twitterStream.shutdown();
    }

    /**
     * Component specific configuration
     */
    @Override
    public Map<String, Object> getComponentConfiguration()
    {
        // create the component config
        Config ret = new Config();

        // set the parallelism for this spout to be 1
        ret.setMaxTaskParallelism(2);

        return ret;
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    //tell storm the schema of the output tuple for this spout
        // tuple consists of a single column called 'tweet'
        outputFieldsDeclarer.declare(new Fields("hash","geo"));
    }
}
