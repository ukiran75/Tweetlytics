package storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

/**
 * Created by uday on 5/2/2017.
 */
public class TweetTopology {
    public static void main(String[] args) throws Exception{

        TopologyBuilder builder = new TopologyBuilder();
        TwitterSpout twitterSpout = new TwitterSpout(
                "t0uyXdYDKJXmLHZ9NKOvA6isH",
                "lYivt7Ba87NuuU06j2oqXldLx8Dxba1jfYNSk4dyi4IaMuXDkw",
                "470208410-g4tSy3WyQasTLXHUyXKYDotgFjHTXDtq48y1hAWc",
                "l66oBDwPS29y2RJHLp4B5qG1egXXrSd0ZPrFKEXn47jQd"
        );
        // attach the tweet spout to the topology - parallelism of 1
        builder.setSpout("tweet-spout", twitterSpout, 3);

        // attach the parse tweet bolt using global grouping
        builder.setBolt("parse-tweet-bolt", new ParseBolt(), 2).globalGrouping("tweet-spout");

        // attach the report bolt using global grouping - parallelism of 1
        builder.setBolt("dbBolt", new DBBolt(), 2).globalGrouping("parse-tweet-bolt");

        Config conf = new Config();

        // set the config in debugging mode
        conf.setDebug(true);

        // create the local cluster instance
        //LocalCluster cluster = new LocalCluster();

        // submit the topology to the local cluster
        //cluster.submitTopology("tweetAnalytics", conf, builder.createTopology());

        // let the topology run for 300 seconds. note topologies never terminate!
        //Utils.sleep(3000000);

        // now kill the topology
        //cluster.killTopology("tweetAnalytics");

        // we are done, so shutdown the local cluster
        //cluster.shutdown();

            // run it in a live cluster

            // set the number of workers for running all spout and bolt tasks
            conf.setNumWorkers(6);

            // create the topology and submit with config
            StormSubmitter.submitTopology("TweetAnalytics", conf, builder.createTopology());
    }
}
