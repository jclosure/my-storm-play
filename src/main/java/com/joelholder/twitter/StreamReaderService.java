package com.joelholder.twitter;


import java.util.stream.Stream;

import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;



public class StreamReaderService {
	public void readTwitterFeed() throws TwitterException {

		TwitterStream stream = TwitterStreamBuilderUtil.getStream();

		StatusListener listener = new StatusListener() {

			public void onException(Exception e) {
				System.out.println("Exception occured:" + e.getMessage());
				e.printStackTrace();
			}


			public void onTrackLimitationNotice(int n) {
				System.out.println("Track limitation notice for " + n);
			}

	
			public void onStatus(Status status) {
				System.out.println("Got tweet:" + status.getText());
				TwitterStreamBean bean = new TwitterStreamBean();
				String username = status.getUser().getScreenName();
				bean.setUserName(username);
				long tweetId = status.getId();
				bean.setId(tweetId);
				bean.setInReplyUserName(status.getInReplyToScreenName());
				if (status != null && status.getRetweetedStatus() != null
						&& status.getRetweetedStatus().getUser() != null) {
					bean.setRetweetUserName(status.getRetweetedStatus().getUser().getScreenName());
				}
				String content = status.getText();
				bean.setContent(content);
			}


			public void onStallWarning(StallWarning arg0) {
				System.out.println("Stall warning");
			}


			public void onScrubGeo(long arg0, long arg1) {
				System.out.println("Scrub geo with:" + arg0 + ":" + arg1);
			}


			public void onDeletionNotice(StatusDeletionNotice arg0) {
				System.out.println("Status deletion notice");
			}
		};


		stream.addListener(listener);
		
		// sample firehose
		//stream.sample();
		
		// filter keywords
/*		FilterQuery qry = new FilterQuery();
		// String[] keywords = { "football" };
		String[] keywords = { "barbie", "mlp", "monster high" };
		qry.track(keywords);
		stream.filter(qry);*/

		// filter location and language
		/*FilterQuery geoQuery = new FilterQuery();

		double[][] westernHemisphere = new double[][] { { -180, -90 }, { 180, 90 } };
		
	

		double[][] sanFrancisco = new double[][] { { -122.75, 36.8 }, { -121.75, 37.8 } };
		double[][] newYorkCity = new double[][] { { -74, 40 }, { -73, 41 } };
		double[][] cities = Stream.of(sanFrancisco, newYorkCity).flatMap(Stream::of).toArray(double[][]::new);

		double[][] california = new double[][] { { 124.434800, 32.433047 }, { -114.015147, 42.120889 } };
		double[][] texas = new double[][] { { -106.728330, 25.745428 }, { -93.438336, 36.605486 } };
		double[][] newYork = new double[][] { { -79.842750, 40.495969 }, { -71.783881, 45.072033 } };
		double[][] states = Stream.of(california, texas, newYork).flatMap(Stream::of).toArray(double[][]::new);

		double[][] usa = new double[][] { { -125.0011, 24.9493 }, { -66.9326, 49.5904 } };

		double[][] closeToNorway = new double[][]{new double[]{3.339844, 53.644638},
            new double[]{18.984375,72.395706
            }};
		
		double northLatitude = 35.2;
		double southLatitude = 25.2;
		double westLongitude = 62.9;
		double eastLongitude = 73.3;
		double[][]  pakistan = {{westLongitude, southLatitude},{eastLongitude, northLatitude}};
		
		// geoQuery.locations(cities);
		//geoQuery.locations(texas);
		// geoQuery.locations(usa);
		// geoQuery.locations(pakistan);
		geoQuery.locations(closeToNorway);
		
		stream.filter(geoQuery);*/
	   
	    // filter language
	    String[] english = new String[]{"en"};
	    String[] spanish = new String[]{"es"};
	    String[] langs = Stream.of(english, spanish)
				.flatMap(Stream::of)
				.toArray(String[]::new);
	    FilterQuery langQuery = new FilterQuery();
	    langQuery.language(langs);
	    String[] keywords = { "wine", "vino" };
	    langQuery.track(keywords);
	    stream.filter(langQuery);
	    
	    //non stream
	    //Query langQuery = new Query("lang:en AND barbie");
		
	}
}