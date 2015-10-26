## Geo and Language Filtering

Location tracking:

https://dev.twitter.com/streaming/overview/request-parameters#locations

In this example, streaming will search for any geotagged tweets written in English in Austin, TX. 

FilterQuery filterQuery = new FilterQuery();
filterQuery.language(new String[] { "en" });
filterQuery.locations(new double[][] { { 30, 97 }, { -30, -97 } });

On Friday, January 17, 2014 6:42:48 AM UTC+1, platkaf wrote:Can twitter4j filter for language and location. I.e.: is this valid:

final FilterQuery query = new FilterQuery();
query.locations(location);
query.language(languages);

Or can it only filter for langauge or location. 

If the latter is the case, then what would be a simple possible workaround? 