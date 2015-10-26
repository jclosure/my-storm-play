/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.joelholder.topology;

import backtype.storm.Config;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import org.apache.log4j.Logger;

import com.joelholder.bolt.SplitSentences;
import com.joelholder.spout.TwitterSpout;

import storm.starter.bolt.IntermediateRankingsBolt;
import storm.starter.bolt.PrinterBolt;
import storm.starter.bolt.RollingCountBolt;
import storm.starter.bolt.TotalRankingsBolt;
import storm.starter.util.StormRunner;

/**
 * This topology does a continuous computation of the top N words that the topology has seen in terms of cardinality.
 * The top N computation is done in a completely scalable way, and a similar approach could be used to compute things
 * like trending topics or trending images on Twitter.
 */
public class RollingTopTwitterWordsMain {

  private static final Logger LOG = Logger.getLogger(RollingTopTwitterWordsMain.class);
  private static final int DEFAULT_RUNTIME_IN_SECONDS = 30;
  private static final int TOP_N = 5;

  private final TopologyBuilder builder;
  private final String topologyName;
  private final Config topologyConfig;
  private final int runtimeInSeconds;
  
  String consumerKey = "wz4Ax8ULQNfQ0YFtzOJig"; 
  String consumerSecret = "2YAjI4jvZY4ILc6fdHtj0r02oDTa8TutGNYodEreZbI"; 
  String accessToken = "293860890-aj7rGubG8CZAD9OkMXcbQFx7UNtVwV8kxgjTXV4s"; 
  String accessTokenSecret = "0lBWw5c5cjbg1SrbnOsHMNdBzfGreRUOKyreGLAXK68";
  
  String[] keyWords = {"obama","clinton","biden"};
  
  double usa[][]={{-125.0011, 24.9493}, {-66.9326,49.5904}};
  
  double geoBox[][]=usa;

  public RollingTopTwitterWordsMain(String topologyName) throws InterruptedException {
    builder = new TopologyBuilder();
    this.topologyName = topologyName;
    topologyConfig = createTopologyConfiguration();
    runtimeInSeconds = DEFAULT_RUNTIME_IN_SECONDS;

    wireTopology();
  }

  private static Config createTopologyConfiguration() {
    Config conf = new Config();
    conf.setDebug(false);
    return conf;
  }

  private void wireTopology() throws InterruptedException {
    String spoutId = "wordGenerator";
    String counterId = "counter";
    String intermediateRankerId = "intermediateRanker";
    String totalRankerId = "finalRanker";
    //builder.setSpout(spoutId, new TestWordSpout(), 5);
    builder.setSpout(spoutId, new TwitterSpout(consumerKey, consumerSecret,
            accessToken, accessTokenSecret, keyWords, geoBox));
    //builder.setBolt("Splitting", new SplitSentences(), 4).fieldsGrouping(spoutId ,new Fields("tweet"));
    builder.setBolt("Splitting", new SplitSentences(), 4).shuffleGrouping(spoutId);
    builder.setBolt(counterId, new RollingCountBolt(9, 3), 4).fieldsGrouping("Splitting", new Fields("tweet"));
    builder.setBolt(intermediateRankerId, new IntermediateRankingsBolt(TOP_N), 4).fieldsGrouping(counterId, new Fields(
        "obj"));
    builder.setBolt(totalRankerId, new TotalRankingsBolt(TOP_N)).globalGrouping(intermediateRankerId);
    builder.setBolt("Printing", new PrinterBolt()).allGrouping(totalRankerId);

  }

  public void runLocally() throws InterruptedException {
    StormRunner.runTopologyLocally(builder.createTopology(), topologyName, topologyConfig, runtimeInSeconds);
  }

  public void runRemotely() throws Exception {
    StormRunner.runTopologyRemotely(builder.createTopology(), topologyName, topologyConfig);
  }

  /**
   * Submits (runs) the topology.
   *
   * Usage: "RollingTopWords [topology-name] [local|remote]"
   *
   * By default, the topology is run locally under the name "slidingWindowCounts".
   *
   * Examples:
   *
   * <pre>
   * {@code
   *
   * # Runs in local mode (LocalCluster), with topology name "slidingWindowCounts"
   * $ storm jar storm-starter-jar-with-dependencies.jar storm.starter.RollingTopWords
   *
   * # Runs in local mode (LocalCluster), with topology name "foobar"
   * $ storm jar storm-starter-jar-with-dependencies.jar storm.starter.RollingTopWords foobar
   *
   * # Runs in local mode (LocalCluster), with topology name "foobar"
   * $ storm jar storm-starter-jar-with-dependencies.jar storm.starter.RollingTopWords foobar local
   *
   * # Runs in remote/cluster mode, with topology name "production-topology"
   * $ storm jar storm-starter-jar-with-dependencies.jar storm.starter.RollingTopWords production-topology remote
   * }
   * </pre>
   *
   * @param args First positional argument (optional) is topology name, second positional argument (optional) defines
   *             whether to run the topology locally ("local") or remotely, i.e. on a real cluster ("remote").
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
	  
	  try {	
		  
		  String topologyName = "slidingWindowCounts";
		    if (args.length >= 1) {
		      topologyName = args[0];
		    }
		    boolean runLocally = true;
		    if (args.length >= 2 && args[1].equalsIgnoreCase("remote")) {
		      runLocally = false;
		    }

		    LOG.info("Topology name: " + topologyName);
		    RollingTopTwitterWordsMain rtw = new RollingTopTwitterWordsMain(topologyName);
		    if (runLocally) {
		      LOG.info("Running in local mode");
		      rtw.runLocally();
		    }
		    else {
		      LOG.info("Running in remote (cluster) mode");
		      rtw.runRemotely();
		    }
	  }
	  catch(Exception ex){
		  
		  LOG.error(ex);
		  
		  
	  }

  }
}
