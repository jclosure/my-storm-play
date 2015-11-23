import java.util.stream.Stream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.joelholder.twitter.ObservableStreamReader;
import com.joelholder.twitter.StreamReaderService;
import com.joelholder.twitter.TwitterStreamBean;

import rx.Observable;
import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;

public class ObservableStreamReaderTests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void test() throws InterruptedException {
		
		ObservableStreamReader service = new ObservableStreamReader();
		
		
		//FilterQuery filterQuery = filterGeography();
		FilterQuery filterQuery = filterKeywords();
		//FilterQuery filterQuery = filterLanguage();
		
		
		try {
			
			Observable<TwitterStreamBean> observable = service.getStreamBeanFeed();
			observable.subscribe(bean -> System.out.println(bean.getContent()));
			
			service.start(filterQuery);
			Thread.sleep(30000);
			service.stop();
			
		
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	// EXAMPLES
	
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
