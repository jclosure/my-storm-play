package com.joelholder.twitter;


import java.util.concurrent.FutureTask;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.joelholder.topology.RollingTopTwitterWordsMain;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observable.Transformer;
import rx.subscriptions.Subscriptions;
import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;




public class ObservableStreamReaderService {
	
	public static final Logger LOG = Logger.getLogger(ObservableStreamReaderService.class);
	
	TwitterStream stream;
	
	public void start(FilterQuery query) {
		if (stream != null) {
			stream.filter(query);
		}
	}
	
	public void stop() {
		if (stream != null) {
			stream.cleanUp();
			stream.shutdown();
		}
	}
	
	public Observable<TwitterStreamBean> getStreamBeanFeed() throws TwitterException {
		
		return getStatusFeed().map(status -> {
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
			return bean;
		});
	}
	
	public Observable<Status> getStatusFeed() throws TwitterException {
		
		stream = TwitterStreamBuilderUtil.getStream();
		
		Observable<Status> feed =  Observable.create(subscriber -> {
            
			stream.addListener(new StatusAdapter() {
                public void onStatus(Status status) {
                    subscriber.onNext(status);
                }

                public void onException(Exception ex) {
                    subscriber.onError(ex);
                }
            });
        });
		
		
		// return the stream to calling code
		return feed;
	}


	
	
}