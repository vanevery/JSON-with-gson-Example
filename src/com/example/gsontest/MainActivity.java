package com.example.gsontest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

// This application uses the very nice gson, JSON parsing library from Google
// https://sites.google.com/site/gson/
import com.google.gson.Gson;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.util.Log;

public class MainActivity extends Activity {

	public static final String LOGTAG = "GSONTest";
	
	// This is the URL to a JSON feed that we want to parse
	String twitterURL = "http://api.twitter.com/1/statuses/user_timeline.json?screen_name=projectglass&include_rts=1";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
			
		// Request the JSON and do the parsing in the background
		RequestJSON request = new RequestJSON();
		request.execute(new String[] { twitterURL });
	}
	
	class RequestJSON extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls) {
			String jsonString = "";

			// Here is the setup of our network request, we create an HttpClient and an HttpGet request object with the URL.
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(urls[0]);

			try {
			
				// Once we execute the request, we get an HttpResponse
				HttpResponse getResponse = client.execute(getRequest);
				
				// If the status is OK then we move on.
				final int statusCode = getResponse.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) { 
					
					// Get the content of the response as an InputStream and construct a reader
	        	   HttpEntity getResponseEntity = getResponse.getEntity();
	        	   InputStream inputStream = getResponseEntity.getContent();
	               
	               // Create a BufferedReader and StringBuilder to read form the stream and output a String
	               // Technically we could just hand gson the reader object but I thought this was a valuable example
	               BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputStream));
	               StringBuilder stringbuilder = new StringBuilder();
	        
	               String currentline = null;
	               
	               try {
	                   while ((currentline = bufferedreader.readLine()) != null) {
	                   	stringbuilder.append(currentline + "\n");
	                   }
	               } catch (IOException e) {
	                   e.printStackTrace();
	               }
	               
	               // Here is the resulting string
	               jsonString = stringbuilder.toString();
	               //Log.v("HTTP REQUEST",result);
	               inputStream.close();  
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return jsonString;
		}
		
		@Override
        protected void onProgressUpdate(Void... values) {
        }
		
		@Override
        protected void onPostExecute(String result) {
	       // Create the Gson object and pass in the JSON
	       // In this case, we are receiving an array and we want to cast it to an array of "TwitterFeed" objects
	       // See below for the definition of a "TwitterFeed"
		   Gson gson = new Gson();
	       TwitterFeed[] responses = gson.fromJson(result, TwitterFeed[].class);
	       
	       // Print out the results
	       for (int i = 0; i < responses.length; i++) {
	    	   Log.v(LOGTAG,responses[i].text);
	       }
        }
		
		
	}
	
	// Here we define the TwitterFeed class.  This is a representation in Java of the data coming from the JSON feed
	class TwitterFeed {
		
		public String created_at;
		public long id;
		public String id_str;
		public String text;
		public String source;
		public boolean truncated;
		public long in_reply_to_status_id;
		public String in_reply_to_status_id_str;
		public long in_reply_to_user_id;
		public String in_reply_to_screen_name;
		// Add more fields from elements in the feed as you like
		
		
		/*  Here is an example of an individual element of data from the feed.
		 * To get it in this format, add the feed to a formatter:
		 * http://jsonformatter.curiousconcept.com/
		 
	      "created_at":"Tue Mar 05 01:35:21 +0000 2013",
	      "id":308752560418856963,
	      "id_str":"308752560418856963",
	      "text":"@TanmayBurde Also, no specific release date yet, but you can stay informed here: http:\/\/t.co\/z4pB6376D6",
	      "source":"\u003ca href=\"http:\/\/www.lithium.com\" rel=\"nofollow\"\u003eLithium Social Web\u003c\/a\u003e",
	      "truncated":false,
	      "in_reply_to_status_id":308059347219537921,
	      "in_reply_to_status_id_str":"308059347219537921",
	      "in_reply_to_user_id":75755804,
	      "in_reply_to_user_id_str":"75755804",
	      "in_reply_to_screen_name":"TanmayBurde",
	      "user":{
	         "id":635013792,
	         "id_str":"635013792",
	         "name":"Project Glass",
	         "screen_name":"projectglass",
	         "location":"",
	         "url":"http:\/\/google.com\/glass",
	         "description":"Seeking Glass Explorers",
	         "protected":false,
	         "followers_count":66941,
	         "friends_count":1,
	         "listed_count":488,
	         "created_at":"Fri Jul 13 23:06:59 +0000 2012",
	         "favourites_count":32,
	         "utc_offset":-28800,
	         "time_zone":"Pacific Time (US & Canada)",
	         "geo_enabled":false,
	         "verified":true,
	         "statuses_count":1526,
	         "lang":"en",
	         "contributors_enabled":true,
	         "is_translator":false,
	         "profile_background_color":"FFFFFF",
	         "profile_background_image_url":"http:\/\/a0.twimg.com\/images\/themes\/theme1\/bg.png",
	         "profile_background_image_url_https":"https:\/\/si0.twimg.com\/images\/themes\/theme1\/bg.png",
	         "profile_background_tile":false,
	         "profile_image_url":"http:\/\/a0.twimg.com\/profile_images\/3282060074\/f41669516452b0f7a146555025f2a1e2_normal.jpeg",
	         "profile_image_url_https":"https:\/\/si0.twimg.com\/profile_images\/3282060074\/f41669516452b0f7a146555025f2a1e2_normal.jpeg",
	         "profile_banner_url":"https:\/\/si0.twimg.com\/profile_banners\/635013792\/1361357399",
	         "profile_link_color":"0084B4",
	         "profile_sidebar_border_color":"C0DEED",
	         "profile_sidebar_fill_color":"DDEEF6",
	         "profile_text_color":"333333",
	         "profile_use_background_image":false,
	         "default_profile":false,
	         "default_profile_image":false,
	         "following":null,
	         "follow_request_sent":null,
	         "notifications":null
	      },
	      "geo":null,
	      "coordinates":null,
	      "place":null,
	      "contributors":null,
	      "retweet_count":0,
	      "favorited":false,
	      "retweeted":false,
	      "possibly_sensitive":false	
	      */	
	}
}
