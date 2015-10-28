package com.joelholder.topology;

import com.joelholder.bolt.*;
import com.joelholder.grouping.*;
import com.joelholder.spout.*;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
//import backtype.storm.tuple.Fields;

public class TopologyMain {

	public static String TOPOLOGY_NAME = "My Cool Topology";
	public static int SLEEP_IN_SECONDS = 10;
	
	public static void main(String[] args) throws InterruptedException {
		try {
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("word-reader", new WordReader());
			builder.setSpout("signals-spout", new SignalsSpout());
			
			builder.setBolt("word-normalizer", new WordNormalizer())
					.shuffleGrouping("word-reader");
			
			builder.setBolt("word-counter", new WordCounter(), 2)
					.shuffleGrouping("word-normalizer")
					.allGrouping("signals-spout", "signals");

			Config conf = new Config();
			conf.put("wordsFile", args[0]);
			conf.setDebug(true);
			conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(TOPOLOGY_NAME , conf, builder.createTopology());
			
			Thread.sleep(SLEEP_IN_SECONDS * 1000);
			
			cluster.killTopology(TOPOLOGY_NAME);
			cluster.shutdown();
			
		} catch(Exception ioe) {
			System.out.println("################ Exception thrown ################");
			ioe.printStackTrace();
		}
		finally {
			// ensure we completely exit the vm's runtime
			// give it a few seconds to shutdown the topo
			Thread.sleep(5000);
			Runtime.getRuntime().halt(0);
		}
	}

}
