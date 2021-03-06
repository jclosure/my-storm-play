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

package com.joelholder.spout;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import storm.starter.bolt.TotalRankingsBolt;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class TwitterSpout extends BaseRichSpout {

	public static final String MESSAGE = "message";
	SpoutOutputCollector _collector;
	LinkedBlockingQueue<Status> queue = null;
	TwitterStream _twitterStream;
	String consumerKey;
	String consumerSecret;
	String accessToken;
	String accessTokenSecret;
	
	FilterQuery query;
	double[][] geoFencing;

	private static final Logger LOG = Logger.getLogger(TwitterSpout.class);

	public TwitterSpout(String consumerKey, String consumerSecret,
			String accessToken, String accessTokenSecret) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
	}
	
	public TwitterSpout(String consumerKey, String consumerSecret,
			String accessToken, String accessTokenSecret, FilterQuery query) {
		this(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		this.query = query;
	}
	
	public TwitterSpout(String consumerKey, String consumerSecret,
			String accessToken, String accessTokenSecret, FilterQuery query, double[][] geoFencing) {
		this(consumerKey, consumerSecret, accessToken, accessTokenSecret, query);
		this.geoFencing = geoFencing;
	}

	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		queue = new LinkedBlockingQueue<Status>(1000);
		_collector = collector;

		StatusListener listener = new StatusListener() {

			@Override
			public void onStatus(Status status) {
			
				queue.offer(status);
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice sdn) {
			}

			@Override
			public void onTrackLimitationNotice(int i) {
			}

			@Override
			public void onScrubGeo(long l, long l1) {
			}

			@Override
			public void onException(Exception ex) {
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub

			}

		};

		_twitterStream = new TwitterStreamFactory(
				new ConfigurationBuilder().setJSONStoreEnabled(true).build())
				.getInstance();

		_twitterStream.addListener(listener);
		_twitterStream.setOAuthConsumer(consumerKey, consumerSecret);
		AccessToken token = new AccessToken(accessToken, accessTokenSecret);
		_twitterStream.setOAuthAccessToken(token);
		
		// sample if no query provided
		if (query == null) {
			_twitterStream.sample();
		}
		else {

			// query geo if specified
			if (geoFencing != null)
				query.locations(geoFencing);
			
			// filter for query
			_twitterStream.filter(query);
		}

	}

	@Override
	public void nextTuple() {
		Status ret = queue.poll();
		
		if (ret == null) {
			Utils.sleep(50);
		} else {
			//_collector.emit(new Values(ret.getText().split(" ")[1]));
			_collector.emit(new Values(ret.getText()));

		}
	}

	@Override
	public void close() {

		_twitterStream.cleanUp();
		_twitterStream.shutdown();

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Config ret = new Config();
		ret.setMaxTaskParallelism(1);
		return ret;
	}

	@Override
	public void ack(Object id) {
	}

	@Override
	public void fail(Object id) {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(MESSAGE));
	}
	Logger getLogger() {
	    return LOG;
	}

}
