package com.joelholder.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import storm.starter.bolt.*;
import twitter4j.FilterQuery;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.joelholder.bolt.AverageWindowBolt;
import com.joelholder.bolt.FileWriterBolt;
import com.joelholder.bolt.HashtagExtractionBolt;
import com.joelholder.bolt.LanguageDetectionBolt;
import com.joelholder.bolt.SentimentBolt;
import com.joelholder.spout.TwitterSpout;

/**
 * Created with IntelliJ IDEA.
 * User: joning
 * Date: 06.09.13
 * Time: 20:49
 * To change this template use File | Settings | File Templates.
 */
public class TwitterFunTopology {



    static String consumerKey = "wz4Ax8ULQNfQ0YFtzOJig"; 
    static String consumerSecret = "2YAjI4jvZY4ILc6fdHtj0r02oDTa8TutGNYodEreZbI"; 
    static String accessToken = "293860890-aj7rGubG8CZAD9OkMXcbQFx7UNtVwV8kxgjTXV4s"; 
    static String accessTokenSecret = "0lBWw5c5cjbg1SrbnOsHMNdBzfGreRUOKyreGLAXK68";
    

    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.INFO);
        /**************** SETUP ****************/
        String remoteClusterTopologyName = null;
        if (args!=null) {
            if (args.length==1) {
                remoteClusterTopologyName = args[0];
            }
            // If credentials are provided as commandline arguments
            else if (args.length==4) {
                consumerKey =args[0];
                consumerSecret =args[1];
                accessToken =args[2];
                accessTokenSecret =args[3];
            }

        }
        /****************       ****************/





        TopologyBuilder builder = new TopologyBuilder();

        // Read

        FilterQuery tweetFilterQuery = new FilterQuery();
        
        // Filter close to Norway
       tweetFilterQuery.locations();
       
       
       
        // Filter on hashtags
        tweetFilterQuery.track(new String[]{"#wine", "#cabernet", "#chardonnay"});

        builder.setSpout("spout", new TwitterSpout(consumerKey, consumerSecret, accessToken, accessTokenSecret, tweetFilterQuery), 1);
        builder.setBolt("file-writer", new FileWriterBolt("tweets.txt"), 1).shuffleGrouping("spout");

        builder.setBolt("language-detection", new LanguageDetectionBolt(), 4).shuffleGrouping("spout");
        builder.setBolt("sentiment", new SentimentBolt(), 4).shuffleGrouping("language-detection");
        builder.setBolt("avg-sentiment", new AverageWindowBolt("sentiment-value"), 4).shuffleGrouping("sentiment");
        builder.setBolt("avg-sentiment-print", new FileWriterBolt("AVG SENTIMENT.txt")).shuffleGrouping("avg-sentiment");

        builder.setBolt("hashtags", new HashtagExtractionBolt(), 4).shuffleGrouping("sentiment");
        builder.setBolt("hashtag-counter", new RollingCountBolt(9, 3), 4).fieldsGrouping("hashtags", new Fields("entity"));
        builder.setBolt("hashtag-intermediate-ranking", new IntermediateRankingsBolt(100), 4).fieldsGrouping("hashtag-counter", new Fields(
                "obj"));
        builder.setBolt("hashtag-total-ranking", new TotalRankingsBolt(100)).globalGrouping("hashtag-intermediate-ranking");
        builder.setBolt("hashtag-ranking-print", new FileWriterBolt("HASHTAG_RANKING.txt")).shuffleGrouping("hashtag-total-ranking");


 /*       builder.setBolt("feeds", new FeedEntityExtractionBolt(), 4).shuffleGrouping("spout");
        builder.setBolt("feed-counter", new RollingCountBolt(9, 3), 4).fieldsGrouping("feeds", new Fields("entity"));
        builder.setBolt("feed-intermediate-ranking", new IntermediateRankingsBolt(100), 4).fieldsGrouping("feed-counter", new Fields(
                "obj"));
        builder.setBolt("feed-total-ranking", new TotalRankingsBolt(100)).globalGrouping("feed-intermediate-ranking");
   */



        Config conf = new Config();
        conf.setDebug(false);


        if (remoteClusterTopologyName!=null) {
            conf.setNumWorkers(3);

            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        }
        else {
            conf.setMaxTaskParallelism(3);

            LocalCluster cluster = new LocalCluster();
            
            String topologyName = "twitter-fun";
            
            cluster.submitTopology(topologyName, conf, builder.createTopology());

            Utils.sleep(10000);
            
            cluster.shutdown();
            
            cluster.killTopology(topologyName);
        }
    }
}
