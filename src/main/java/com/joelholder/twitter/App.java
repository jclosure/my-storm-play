package com.joelholder.twitter;

import twitter4j.TwitterException;

public class App {

	public static void main(String[] args){
		
		StreamReaderService service = new StreamReaderService();
		
		try {
			service.readTwitterFeed();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
