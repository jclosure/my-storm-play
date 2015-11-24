package com.joelholder.twitter;


import twitter4j.FilterQuery;

/*
 * Example Usage:
 * 
 * TwitterStreamFilterBuilder
	.begin()
	.filterKeywords(new String[]{ "sex" })
	.filterGeography(new double[][] { { -125.0011, 24.9493 }, { -66.9326, 49.5904 } }) //us
	.filterLanguage(new String[]{"en"})
	.build();
 */

public class TwitterStreamFilterBuilder {

	FilterQuery query;
	
	public static TwitterStreamFilterBuilder begin(){
		return new TwitterStreamFilterBuilder(new FilterQuery());
	}
	
	public static TwitterStreamFilterBuilder begin(FilterQuery query){
		return new TwitterStreamFilterBuilder(query);
	}
	
	private TwitterStreamFilterBuilder(FilterQuery query) {
		this.query = query;
	}
	
	public FilterQuery build() {
		return query;
	}
	
	public TwitterStreamFilterBuilder filterKeywords(String[] keywords) {
		query.track(keywords);
		return this;
	}
	
	public TwitterStreamFilterBuilder filterLanguage(String[] langs){
	    query.language(langs);
	    return this;
	}

	public TwitterStreamFilterBuilder filterGeography(double[][] boxes){
		query.locations(boxes);
		return this;
	}
	
}
