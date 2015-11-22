package com.joelholder.twitter;

import twitter4j.TwitterException;
import twitter4j.TwitterStream;

public class App {

	public static void main(String[] args) throws InterruptedException{
		
		StreamReaderService service = new StreamReaderService();
		
		try {
			TwitterStream stream = service.readTwitterFeed();
			Thread.sleep(10000);
			stream.cleanUp();
			stream.shutdown();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
