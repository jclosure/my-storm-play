package com.joelholder.vertx;

import java.util.stream.Stream;

import com.joelholder.twitter.ObservableStreamReader;
import com.joelholder.twitter.TwitterStreamBean;
import com.joelholder.vertx.util.Runner;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import rx.Observable;
import twitter4j.FilterQuery;
import twitter4j.TwitterException;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class SimpleServer extends AbstractVerticle {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    Runner.runExample(SimpleServer.class);
  }
  
  ObservableStreamReader service = new ObservableStreamReader();
  

  @Override
  public void start() throws Exception {

    Router router = Router.router(vertx);

    // Allow outbound traffic to the news-feed address

    BridgeOptions options = new BridgeOptions().addOutboundPermitted(new PermittedOptions().setAddress("news-feed"));

    router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(options, event -> {

      // You can also optionally provide a handler like this which will be passed any events that occur on the bridge
      // You can use this for monitoring or logging, or to change the raw messages in-flight.
      // It can also be used for fine grained access control.

      if (event.type() == BridgeEventType.SOCKET_CREATED) {
        System.out.println("A socket was created");
      }

      
      
      // This signals that it's ok to process the event
      event.complete(true);

    }));

    // Serve the static resources
    router.route().handler(StaticHandler.create());

    vertx.createHttpServer().requestHandler(router::accept).listen(8080);

    // Publish messages to the address "news-feed" in real-time
    
    FilterQuery filterQuery = filterKeywords();
    
    try {
		
		Observable<TwitterStreamBean> observable = service.getStreamBeanFeed();
		observable.subscribe(bean -> vertx.eventBus().publish("news-feed", bean.getContent()));
		
		service.start(filterQuery);
		
	} catch (TwitterException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
  }
  
  
  public void stop() throws Exception {
	  service.stop();
  }
  
	private FilterQuery filterKeywords() {
		// filter keywords
		FilterQuery qry = new FilterQuery();
		// String[] keywords = { "football" };
		//String[] keywords = { "barbie", "mlp", "monster high" };
		String[] keywords = { "terrorism", "paris", "isis" };
		qry.track(keywords);
		return qry;
	}
	
	private FilterQuery filterLanguage(){
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
	    return langQuery;
	}

	private FilterQuery filterGeography(){
		FilterQuery geoQuery = new FilterQuery();

		double[][] westernHemisphere = new double[][] { { -180, -90 }, { 180, 90 } };
		
	

		double[][] sanFrancisco = new double[][] { { -122.75, 36.8 }, { -121.75, 37.8 } };
		double[][] newYorkCity = new double[][] { { -74, 40 }, { -73, 41 } };
		double[][] cities = Stream.of(sanFrancisco, newYorkCity).flatMap(Stream::of).toArray(double[][]::new);

		double[][] california = new double[][] { { 124.434800, 32.433047 }, { -114.015147, 42.120889 } };
		double[][] texas = new double[][] { { -106.728330, 25.745428 }, { -93.438336, 36.605486 } };
		double[][] newYork = new double[][] { { -79.842750, 40.495969 }, { -71.783881, 45.072033 } };
		double[][] states = Stream.of(california, texas, newYork).flatMap(Stream::of).toArray(double[][]::new);

		double[][] usa = new double[][] { { -125.0011, 24.9493 }, { -66.9326, 49.5904 } };

		double[][] closeToNorway = new double[][] { new double[] { 3.339844, 53.644638 },
				new double[] { 18.984375, 72.395706 } };
		
		double northLatitude = 35.2;
		double southLatitude = 25.2;
		double westLongitude = 62.9;
		double eastLongitude = 73.3;
		double[][]  pakistan = {{westLongitude, southLatitude},{eastLongitude, northLatitude}};
		
		geoQuery.locations(cities);
		//geoQuery.locations(texas);
		// geoQuery.locations(usa);
		// geoQuery.locations(pakistan);
		//geoQuery.locations(closeToNorway);
		
		return geoQuery;
	}
}
